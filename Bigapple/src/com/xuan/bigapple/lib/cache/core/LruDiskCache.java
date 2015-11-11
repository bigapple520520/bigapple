package com.xuan.bigapple.lib.cache.core;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.protocol.HTTP;

/**
 * Android 4.1.1中的本地缓存（bitmap模块中的缓存更好用，扩展了缓存过期时间设置）
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午5:20:21 $
 */
public final class LruDiskCache implements Closeable {
	static final String JOURNAL_FILE = "journal";
	static final String JOURNAL_FILE_TEMP = "journal.tmp";
	static final String JOURNAL_FILE_BACKUP = "journal.bkp";

	static final String MAGIC = "libcore.io.DiskLruCache";
	static final String VERSION_1 = "1";
	static final long ANY_SEQUENCE_NUMBER = -1;
	static final Pattern LEGAL_KEY_PATTERN = Pattern
			.compile("[a-z0-9_-]{1,64}");

	private static final String CLEAN = "CLEAN";
	private static final String DIRTY = "DIRTY";
	private static final String REMOVE = "REMOVE";
	private static final String READ = "READ";

	/**
	 * 磁盘缓存的日志格式如下: <br>
	 * libcore.io.DiskLruCache <br>
	 * 1 -----------磁盘缓存的版本信息<br>
	 * 100 ---------应用的版本信息<br>
	 * 2------------缓存的数量<br>
	 * -------------空行 <br>
	 * CLEAN 3400330d1dfc7f3f7f4b8d4d803dfcf6 832 21054 <br>
	 * DIRTY 335c4c6028171cfddfbaae1a9c313c52 <br>
	 * CLEAN 335c4c6028171cfddfbaae1a9c313c52 3934 2342 <br>
	 * REMOVE 335c4c6028171cfddfbaae1a9c313c52 <br>
	 * DIRTY 1ab96a171faeeee38496d8b330771a7a <br>
	 * CLEAN 1ab96a171faeeee38496d8b330771a7a 1600 234 <br>
	 * READ 335c4c6028171cfddfbaae1a9c313c52 <br>
	 * READ 3400330d1dfc7f3f7f4b8d4d803dfcf6
	 * 
	 * 
	 * Each of the subsequent lines in the file is a record of the state of a
	 * cache entry. Each line contains space-separated values: a state, a key,
	 * and optional state-specific values. o DIRTY lines track that an entry is
	 * actively being created or updated. Every successful DIRTY action should
	 * be followed by a CLEAN or REMOVE action. DIRTY lines without a matching
	 * CLEAN or REMOVE indicate that temporary files may need to be deleted. o
	 * CLEAN lines track a cache entry that has been successfully published and
	 * may be read. A publish line is followed by the lengths of each of its
	 * values. o READ lines track accesses for LRU. o REMOVE lines track entries
	 * that have been deleted.
	 * 
	 * The journal file is appended to as cache operations occur. The journal
	 * may occasionally be compacted by dropping redundant lines. A temporary
	 * file named "journal.tmp" will be used during compaction; that file should
	 * be deleted if it exists when the cache is opened.
	 */

	private final File directory;// 日志文件
	private final File journalFile;// 日志文件临时文件
	private final File journalFileTmp;
	private final File journalFileBackup;
	private final int appVersion;
	private long maxSize;
	private final int valueCount;
	private long size = 0;
	private Writer journalWriter;
	private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap<String, Entry>(
			0, 0.75f, true);
	private int redundantOpCount;

	// 区分老的和当前的快照，每一个实体在每次编辑被committed时都被赋予一个序列号。 一个快照的序列号如果不等于entry的序列号那它就是废弃的。
	private long nextSequenceNumber = 0;

	final ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, 1,
			60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

	private final Callable<Void> cleanupCallable = new Callable<Void>() {
		@Override
		public Void call() throws Exception {
			synchronized (LruDiskCache.this) {
				if (journalWriter == null) {
					return null; // Closed.
				}
				trimToSize();
				if (journalRebuildRequired()) {
					rebuildJournal();
					redundantOpCount = 0;
				}
			}
			return null;
		}
	};

	private LruDiskCache(File directory, int appVersion, int valueCount,
			long maxSize) {
		this.directory = directory;
		this.appVersion = appVersion;
		this.valueCount = valueCount;
		this.maxSize = maxSize;

		this.journalFile = new File(directory, JOURNAL_FILE);
		this.journalFileTmp = new File(directory, JOURNAL_FILE_TEMP);
		this.journalFileBackup = new File(directory, JOURNAL_FILE_BACKUP);
	}

	/**
	 * 打开缓存，如果不存在就创建一个
	 * 
	 * @param directory
	 *            缓存路径
	 * @param appVersion
	 *            应用程序版本
	 * @param valueCount
	 *            缓存数量
	 * @param maxSize
	 *            缓存最大容量
	 * @return
	 * @throws IOException
	 */
	public static LruDiskCache open(File directory, int appVersion,
			int valueCount, long maxSize) throws IOException {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize <= 0");
		}

		if (valueCount <= 0) {
			throw new IllegalArgumentException("valueCount <= 0");
		}

		// 如果备份文件存在，说明上一次在记录日志的时候异常退出了，所以恢复备份文件
		File backupFile = new File(directory, JOURNAL_FILE_BACKUP);
		if (backupFile.exists()) {
			File journalFile = new File(directory, JOURNAL_FILE);
			// 删除异常记录的日志文件
			if (journalFile.exists()) {
				backupFile.delete();
			} else {
				// 恢复备份文件
				renameTo(backupFile, journalFile, false);
			}
		}

		// Prefer to pick up where we left off.
		LruDiskCache cache = new LruDiskCache(directory, appVersion,
				valueCount, maxSize);
		if (cache.journalFile.exists()) {
			try {
				cache.readJournal();
				cache.processJournal();
				cache.journalWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(
								cache.journalFile, true), HTTP.US_ASCII));
				return cache;
			} catch (IOException journalIsCorrupt) {
				System.out.println("DiskLruCache " + directory
						+ " is corrupt: " + journalIsCorrupt.getMessage()
						+ ", removing");
				cache.delete();
			}
		}

		// 如果是第一次，新建日志文件
		directory.mkdirs();
		cache = new LruDiskCache(directory, appVersion, valueCount, maxSize);
		cache.rebuildJournal();
		return cache;
	}

	// 读取日志文件
	private void readJournal() throws IOException {
		StrictLineReader reader = new StrictLineReader(new FileInputStream(
				journalFile));
		try {
			String magic = reader.readLine();
			String version = reader.readLine();
			String appVersionString = reader.readLine();
			String valueCountString = reader.readLine();
			String blank = reader.readLine();
			if (!MAGIC.equals(magic) || !VERSION_1.equals(version)
					|| !Integer.toString(appVersion).equals(appVersionString)
					|| !Integer.toString(valueCount).equals(valueCountString)
					|| !"".equals(blank)) {
				throw new IOException("unexpected journal header: [" + magic
						+ ", " + version + ", " + valueCountString + ", "
						+ blank + "]");
			}

			int lineCount = 0;
			while (true) {
				try {
					readJournalLine(reader.readLine());
					lineCount++;
				} catch (EOFException endOfJournal) {
					break;
				}
			}
			redundantOpCount = lineCount - lruEntries.size();
		} finally {
			closeQuietly(reader);
		}
	}

	// 读取日志文件的每一行
	private void readJournalLine(String line) throws IOException {
		int firstSpace = line.indexOf(' ');
		if (firstSpace == -1) {
			throw new IOException("unexpected journal line: " + line);
		}

		int keyBegin = firstSpace + 1;
		int secondSpace = line.indexOf(' ', keyBegin);
		final String key;
		if (secondSpace == -1) {
			key = line.substring(keyBegin);
			if (firstSpace == REMOVE.length() && line.startsWith(REMOVE)) {
				// REMOVE记录处理
				lruEntries.remove(key);
				return;
			}
		} else {
			key = line.substring(keyBegin, secondSpace);
		}

		Entry entry = lruEntries.get(key);
		if (entry == null) {
			entry = new Entry(key);
			lruEntries.put(key, entry);
		}

		if (secondSpace != -1 && firstSpace == CLEAN.length()
				&& line.startsWith(CLEAN)) {
			// CLEAN记录处理
			String[] parts = line.substring(secondSpace + 1).split(" ");
			entry.readable = true;
			entry.currentEditor = null;
			entry.setLengths(parts);
		} else if (secondSpace == -1 && firstSpace == DIRTY.length()
				&& line.startsWith(DIRTY)) {
			// DIRTY记录处理
			entry.currentEditor = new Editor(entry);
		} else if (secondSpace == -1 && firstSpace == READ.length()
				&& line.startsWith(READ)) {
			// DIRTY记录处理
			// This work was already done by calling lruEntries.get().
		} else {
			throw new IOException("unexpected journal line: " + line);
		}
	}

	/**
	 * 处理日志，删除REMOVE标记的记录
	 * 
	 * @throws IOException
	 */
	private void processJournal() throws IOException {
		deleteIfExists(journalFileTmp);
		for (Iterator<Entry> i = lruEntries.values().iterator(); i.hasNext();) {
			Entry entry = i.next();
			if (entry.currentEditor == null) {
				for (int t = 0; t < valueCount; t++) {
					size += entry.lengths[t];
				}
			} else {
				entry.currentEditor = null;
				for (int t = 0; t < valueCount; t++) {
					deleteIfExists(entry.getCleanFile(t));
					deleteIfExists(entry.getDirtyFile(t));
				}
				i.remove();
			}
		}
	}

	/**
	 * 创建一个新的删掉冗余信息的日志。替换当前的日志
	 * 
	 * @throws IOException
	 */
	private synchronized void rebuildJournal() throws IOException {
		if (journalWriter != null) {
			journalWriter.close();
		}

		Writer writer = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(journalFileTmp), HTTP.US_ASCII));
		try {
			writer.write(MAGIC);
			writer.write("\n");
			writer.write(VERSION_1);
			writer.write("\n");
			writer.write(Integer.toString(appVersion));
			writer.write("\n");
			writer.write(Integer.toString(valueCount));
			writer.write("\n");
			writer.write("\n");

			for (Entry entry : lruEntries.values()) {
				if (entry.currentEditor != null) {
					writer.write(DIRTY + ' ' + entry.key + '\n');
				} else {
					writer.write(CLEAN + ' ' + entry.key + entry.getLengths()
							+ '\n');
				}
			}
		} finally {
			writer.close();
		}

		if (journalFile.exists()) {
			renameTo(journalFile, journalFileBackup, true);
		}
		renameTo(journalFileTmp, journalFile, false);
		journalFileBackup.delete();

		journalWriter = new BufferedWriter(new OutputStreamWriter(
				new FileOutputStream(journalFile, true), HTTP.US_ASCII));
	}

	private static void deleteIfExists(File file) throws IOException {
		if (file.exists() && !file.delete()) {
			throw new IOException();
		}
	}

	private static void renameTo(File from, File to, boolean deleteDestination)
			throws IOException {
		if (deleteDestination) {
			deleteIfExists(to);
		}
		if (!from.renameTo(to)) {
			throw new IOException();
		}
	}

	/**
	 * 返回key对应的entry的snapshot，当key相应的entry不存在或者当前不可读时返回null。<br>
	 * 如果返回相应的值，它就会被移动到LRU队列的头部。
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public synchronized Snapshot get(String key) throws IOException {
		checkNotClosed();
		validateKey(key);
		Entry entry = lruEntries.get(key);
		if (entry == null) {
			return null;
		}

		if (!entry.readable) {
			return null;
		}

		// Open all streams eagerly to guarantee that we see a single published
		// snapshot. If we opened streams lazily then the streams could come
		// from different edits.
		InputStream[] ins = new InputStream[valueCount];
		try {
			for (int i = 0; i < valueCount; i++) {
				ins[i] = new FileInputStream(entry.getCleanFile(i));
			}
		} catch (FileNotFoundException e) {
			// 文件没找到，就是被删除了
			for (int i = 0; i < valueCount; i++) {
				if (ins[i] != null) {
					closeQuietly(ins[i]);
				} else {
					break;
				}
			}
			return null;
		}

		redundantOpCount++;
		journalWriter.append(READ + ' ' + key + '\n');
		if (journalRebuildRequired()) {
			executorService.submit(cleanupCallable);
		}

		return new Snapshot(key, entry.sequenceNumber, ins, entry.lengths);
	}

	/**
	 * Returns an editor for the entry named {@code key}, or null if another
	 * edit is in progress.
	 */
	public Editor edit(String key) throws IOException {
		return edit(key, ANY_SEQUENCE_NUMBER);
	}

	private synchronized Editor edit(String key, long expectedSequenceNumber)
			throws IOException {
		checkNotClosed();
		validateKey(key);
		Entry entry = lruEntries.get(key);
		if (expectedSequenceNumber != ANY_SEQUENCE_NUMBER
				&& (entry == null || entry.sequenceNumber != expectedSequenceNumber)) {
			return null; // Snapshot is stale.
		}

		if (entry == null) {
			entry = new Entry(key);
			lruEntries.put(key, entry);
		} else if (entry.currentEditor != null) {
			return null; // Another edit is in progress.
		}

		Editor editor = new Editor(entry);
		entry.currentEditor = editor;

		// Flush the journal before creating files to prevent file leaks.
		journalWriter.write(DIRTY + ' ' + key + '\n');
		journalWriter.flush();
		return editor;
	}

	public File getDirectory() {
		return directory;
	}

	public synchronized long getMaxSize() {
		return maxSize;
	}

	public synchronized void setMaxSize(long maxSize) {
		this.maxSize = maxSize;
		executorService.submit(cleanupCallable);
	}

	public synchronized long size() {
		return size;
	}

	// 提交完成指令
	private synchronized void completeEdit(Editor editor, boolean success)
			throws IOException {
		Entry entry = editor.entry;
		if (entry.currentEditor != editor) {
			throw new IllegalStateException();
		}

		// If this edit is creating the entry for the first time, every index
		// must have a value.
		if (success && !entry.readable) {
			for (int i = 0; i < valueCount; i++) {
				if (!editor.written[i]) {
					editor.abort();
					throw new IllegalStateException(
							"Newly created entry didn't create value for index "
									+ i);
				}
				if (!entry.getDirtyFile(i).exists()) {
					editor.abort();
					return;
				}
			}
		}

		for (int i = 0; i < valueCount; i++) {
			File dirty = entry.getDirtyFile(i);
			if (success) {
				if (dirty.exists()) {
					File clean = entry.getCleanFile(i);
					dirty.renameTo(clean);
					long oldLength = entry.lengths[i];
					long newLength = clean.length();
					entry.lengths[i] = newLength;
					size = size - oldLength + newLength;
				}
			} else {
				deleteIfExists(dirty);
			}
		}

		redundantOpCount++;
		entry.currentEditor = null;
		if (entry.readable | success) {
			entry.readable = true;
			journalWriter.write(CLEAN + ' ' + entry.key + entry.getLengths()
					+ '\n');
			if (success) {
				entry.sequenceNumber = nextSequenceNumber++;
			}
		} else {
			lruEntries.remove(entry.key);
			journalWriter.write(REMOVE + ' ' + entry.key + '\n');
		}
		journalWriter.flush();

		if (size > maxSize || journalRebuildRequired()) {
			executorService.submit(cleanupCallable);
		}
	}

	// 重建日志
	private boolean journalRebuildRequired() {
		final int redundantOpCompactThreshold = 2000;
		return redundantOpCount >= redundantOpCompactThreshold
				&& redundantOpCount >= lruEntries.size();
	}

	/**
	 * 删除指定缓存，但实际只是在日志中做了一个标志，实际文件还是没有删除的
	 * 
	 * @param key
	 * @return
	 * @throws IOException
	 */
	public synchronized boolean remove(String key) throws IOException {
		checkNotClosed();
		validateKey(key);
		Entry entry = lruEntries.get(key);
		if (entry == null || entry.currentEditor != null) {
			return false;
		}

		for (int i = 0; i < valueCount; i++) {
			File file = entry.getCleanFile(i);
			if (file.exists() && !file.delete()) {
				throw new IOException("failed to delete " + file);
			}
			size -= entry.lengths[i];
			entry.lengths[i] = 0;
		}

		redundantOpCount++;
		journalWriter.append(REMOVE + ' ' + key + '\n');
		lruEntries.remove(key);

		if (journalRebuildRequired()) {
			executorService.submit(cleanupCallable);
		}

		return true;
	}

	public synchronized boolean isClosed() {
		return journalWriter == null;
	}

	private void checkNotClosed() {
		if (journalWriter == null) {
			throw new IllegalStateException("cache is closed");
		}
	}

	/**
	 * 强制将缓冲区内容写入
	 * 
	 * @throws IOException
	 */
	public synchronized void flush() throws IOException {
		checkNotClosed();
		trimToSize();
		journalWriter.flush();
	}

	@Override
	public synchronized void close() throws IOException {
		if (journalWriter == null) {
			return; // Already closed.
		}

		for (Entry entry : new ArrayList<Entry>(lruEntries.values())) {
			if (entry.currentEditor != null) {
				entry.currentEditor.abort();
			}
		}

		trimToSize();
		journalWriter.close();
		journalWriter = null;
	}

	private void trimToSize() throws IOException {
		while (size > maxSize) {
			Map.Entry<String, Entry> toEvict = lruEntries.entrySet().iterator()
					.next();
			remove(toEvict.getKey());
		}
	}

	/**
	 * 关闭缓存，然后删除缓存目录下的所有缓存文件
	 * 
	 * @throws IOException
	 */
	public void delete() throws IOException {
		close();
		deleteContents(directory);
	}

	private void validateKey(String key) {
		Matcher matcher = LEGAL_KEY_PATTERN.matcher(key);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(
					"keys must match regex [a-z0-9_-]{1,64}: \"" + key + "\"");
		}
	}

	private static String inputStreamToString(InputStream in)
			throws IOException {
		return readFully(new InputStreamReader(in, HTTP.UTF_8));
	}

	/**
	 * 一个Entry的快照，一个快照里面可以放几个文件
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2013-9-18 上午10:12:31 $
	 */
	public final class Snapshot implements Closeable {
		private final String key;
		private final long sequenceNumber;
		private final InputStream[] ins;
		private final long[] lengths;

		private Snapshot(String key, long sequenceNumber, InputStream[] ins,
				long[] lengths) {
			this.key = key;
			this.sequenceNumber = sequenceNumber;
			this.ins = ins;
			this.lengths = lengths;
		}

		/**
		 * 生成这个快照的Editor对象
		 * 
		 * @return
		 * @throws IOException
		 */
		public Editor edit() throws IOException {
			return LruDiskCache.this.edit(key, sequenceNumber);
		}

		/**
		 * 获取这个快照的指定文件的输入流
		 * 
		 * @param index
		 * @return
		 */
		public InputStream getInputStream(int index) {
			return ins[index];
		}

		/**
		 * 读取这个快照的指定文件的字符串内容
		 * 
		 * @param index
		 * @return
		 * @throws IOException
		 */
		public String getString(int index) throws IOException {
			return inputStreamToString(getInputStream(index));
		}

		/**
		 * 获取这个快照的指定文件的长度
		 * 
		 * @param index
		 * @return
		 */
		public long getLength(int index) {
			return lengths[index];
		}

		@Override
		public void close() {
			for (InputStream in : ins) {
				closeQuietly(in);
			}
		}
	}

	private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
		@Override
		public void write(int b) throws IOException {
			// Eat all writes silently. Nom nom.
		}
	};

	/**
	 * 操作Entry的Editor
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2013-8-20 下午7:31:44 $
	 */
	public final class Editor {
		private final Entry entry;
		private final boolean[] written;
		private boolean hasErrors;
		private boolean committed;

		private Editor(Entry entry) {
			this.entry = entry;
			this.written = (entry.readable) ? null : new boolean[valueCount];
		}

		/**
		 * 获取一个输入流，得到CLEAN指向文件
		 * 
		 * @param index
		 * @return
		 * @throws IOException
		 */
		public InputStream newInputStream(int index) throws IOException {
			synchronized (LruDiskCache.this) {
				if (entry.currentEditor != this) {
					throw new IllegalStateException();
				}

				if (!entry.readable) {
					return null;
				}

				try {
					return new FileInputStream(entry.getCleanFile(index));
				} catch (FileNotFoundException e) {
					return null;
				}
			}
		}

		public String getString(int index) throws IOException {
			InputStream in = newInputStream(index);
			return in != null ? inputStreamToString(in) : null;
		}

		/**
		 * 获取一个输出流，一般是得到DIRTY指向的文件，机制是这样的，先输出到DIRTY文件，等提交了再切存到CLEAN文件
		 * 
		 * @param index
		 * @return
		 * @throws IOException
		 */
		public OutputStream newOutputStream(int index) throws IOException {
			synchronized (LruDiskCache.this) {
				if (entry.currentEditor != this) {
					throw new IllegalStateException();
				}

				if (!entry.readable) {
					written[index] = true;
				}

				File dirtyFile = entry.getDirtyFile(index);
				FileOutputStream outputStream;
				try {
					outputStream = new FileOutputStream(dirtyFile);
				} catch (FileNotFoundException e) {
					// Attempt to recreate the cache directory.
					directory.mkdirs();
					try {
						outputStream = new FileOutputStream(dirtyFile);
					} catch (FileNotFoundException e2) {
						// We are unable to recover. Silently eat the writes.
						return NULL_OUTPUT_STREAM;
					}
				}
				return new FaultHidingOutputStream(outputStream);
			}
		}

		public void set(int index, String value) throws IOException {
			Writer writer = null;
			try {
				writer = new OutputStreamWriter(newOutputStream(index),
						HTTP.UTF_8);
				writer.write(value);
			} finally {
				closeQuietly(writer);
			}
		}

		/**
		 * 确认保存
		 * 
		 * @throws IOException
		 */
		public void commit() throws IOException {
			if (hasErrors) {
				completeEdit(this, false);
				remove(entry.key); // The previous entry is stale.
			} else {
				completeEdit(this, true);
			}
			committed = true;
		}

		/**
		 * 取消保存
		 * 
		 * @throws IOException
		 */
		public void abort() throws IOException {
			completeEdit(this, false);
		}

		/**
		 * 如果保存不成功就取消
		 */
		public void abortUnlessCommitted() {
			if (!committed) {
				try {
					abort();
				} catch (IOException ignored) {
				}
			}
		}

		/**
		 * 这个流存在的意思是，如果写出现错误时，默默的记录下来，而不是要抛出异常
		 * 
		 * @author xuan
		 * @version $Revision: 1.0 $, $Date: 2013-9-18 上午10:23:04 $
		 */
		private class FaultHidingOutputStream extends FilterOutputStream {
			private FaultHidingOutputStream(OutputStream out) {
				super(out);
			}

			@Override
			public void write(int oneByte) {
				try {
					out.write(oneByte);
				} catch (IOException e) {
					hasErrors = true;
				}
			}

			@Override
			public void write(byte[] buffer, int offset, int length) {
				try {
					out.write(buffer, offset, length);
				} catch (IOException e) {
					hasErrors = true;
				}
			}

			@Override
			public void close() {
				try {
					out.close();
				} catch (IOException e) {
					hasErrors = true;
				}
			}

			@Override
			public void flush() {
				try {
					out.flush();
				} catch (IOException e) {
					hasErrors = true;
				}
			}
		}
	}

	/**
	 * 一个缓存的具体条目，一个条目可能可以存在多个文件
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2013-8-20 下午7:29:41 $
	 */
	private final class Entry {
		private final String key;
		private final long[] lengths;

		private boolean readable;

		private Editor currentEditor;
		private long sequenceNumber;

		private Entry(String key) {
			this.key = key;
			this.lengths = new long[valueCount];
		}

		/**
		 * 获取条目中各个文件的长度
		 * 
		 * @return
		 * @throws IOException
		 */
		public String getLengths() throws IOException {
			StringBuilder result = new StringBuilder();
			for (long size : lengths) {
				result.append(' ').append(size);
			}
			return result.toString();
		}

		/**
		 * 设置各个文件的长度
		 * 
		 * @param strings
		 * @throws IOException
		 */
		private void setLengths(String[] strings) throws IOException {
			if (strings.length != valueCount) {
				throw invalidLengths(strings);
			}

			try {
				for (int i = 0; i < strings.length; i++) {
					lengths[i] = Long.parseLong(strings[i]);
				}
			} catch (NumberFormatException e) {
				throw invalidLengths(strings);
			}
		}

		private IOException invalidLengths(String[] strings) throws IOException {
			throw new IOException("unexpected journal line: "
					+ java.util.Arrays.toString(strings));
		}

		/**
		 * 获取CLEAN指向的文件
		 * 
		 * @param i
		 * @return
		 */
		public File getCleanFile(int i) {
			return new File(directory, key + "." + i);
		}

		/**
		 * 获取DIRTY指向的文件，一般做中间过渡
		 * 
		 * @param i
		 * @return
		 */
		public File getDirtyFile(int i) {
			return new File(directory, key + "." + i + ".tmp");
		}
	}

	// 工具部分
	// -------------------------------------------------------------------------------------------
	/**
	 * 读取Reader中的所有
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private static String readFully(Reader reader) throws IOException {
		try {
			StringWriter writer = new StringWriter();
			char[] buffer = new char[1024];
			int count;
			while ((count = reader.read(buffer)) != -1) {
				writer.write(buffer, 0, count);
			}
			return writer.toString();
		} finally {
			reader.close();
		}
	}

	/**
	 * 递归删除dir下所有文件
	 * 
	 * @param dir
	 * @throws IOException
	 */
	private static void deleteContents(File dir) throws IOException {
		File[] files = dir.listFiles();
		if (files == null) {
			throw new IOException("not a readable directory: " + dir);
		}

		for (File file : files) {
			if (file.isDirectory()) {
				deleteContents(file);
			}

			if (!file.delete()) {
				throw new IOException("failed to delete file: " + file);
			}
		}
	}

	private static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (RuntimeException rethrown) {
				throw rethrown;
			} catch (Exception ignored) {
			}
		}
	}

	/**
	 * 流的行读取器
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2013-8-2 上午10:18:41 $
	 */
	private class StrictLineReader implements Closeable {
		private static final byte CR = (byte) '\r';// 回车
		private static final byte LF = (byte) '\n';// 换行

		private final InputStream in;
		private final Charset charset = Charset.forName(HTTP.US_ASCII);

		// buf用来存放缓存字节，一般0<=pos<=end，[pos, end)之间的数据可读取
		// 输入结束时，如果有一行未终止，我们设置end=-1，否则end == pos，程序意外终止时，end有可能是pos或者-1
		private byte[] buf;
		private int pos;
		private int end;

		public StrictLineReader(InputStream in) {
			this(in, 8192);
		}

		public StrictLineReader(InputStream in, int capacity) {
			if (in == null) {
				throw new NullPointerException();
			}

			if (capacity < 0) {
				throw new IllegalArgumentException("capacity <= 0");
			}

			this.in = in;
			buf = new byte[capacity];
		}

		/**
		 * 关闭行读取器
		 */
		@Override
		public void close() throws IOException {
			synchronized (in) {
				if (buf != null) {
					buf = null;
					in.close();
				}
			}
		}

		/**
		 * 读取行
		 * 
		 * @return
		 * @throws IOException
		 */
		public String readLine() throws IOException {
			synchronized (in) {
				if (buf == null) {
					throw new IOException("LineReader is closed");
				}

				// Read more data if we are at the end of the buffered data.
				// Though it's an error to read after an exception, we will let
				// {@code fillBuf()}
				// throw again if that happens; thus we need to handle end == -1
				// as well as end == pos.
				if (pos >= end) {
					fillBuf();
				}

				// 试图在缓存中找LF（换行），如果有找到，就返回
				for (int i = pos; i != end; ++i) {
					if (buf[i] == LF) {
						int lineEnd = (i != pos && buf[i - 1] == CR) ? i - 1
								: i;
						String res = new String(buf, pos, lineEnd - pos,
								charset.name());
						pos = i + 1;
						return res;
					}
				}

				// Let's anticipate up to 80 characters on top of those already
				// read.
				ByteArrayOutputStream out = new ByteArrayOutputStream(end - pos
						+ 80) {
					@Override
					public String toString() {
						int length = (count > 0 && buf[count - 1] == CR) ? count - 1
								: count;
						try {
							return new String(buf, 0, length, charset.name());
						} catch (UnsupportedEncodingException e) {
							// 由于我们已经设置了字符编码，所以这个不会发生
							throw new AssertionError(e);
						}
					}
				};

				while (true) {
					out.write(buf, pos, end - pos);
					// Mark unterminated line in case fillBuf throws
					// EOFException or IOException.
					end = -1;
					fillBuf();

					// 试图在缓存中找LF（换行），如果有找到，就返回
					for (int i = pos; i != end; ++i) {
						if (buf[i] == LF) {
							if (i != pos) {
								out.write(buf, pos, i - pos);
							}
							pos = i + 1;
							return out.toString();
						}
					}
				}
			}
		}

		/**
		 * 读取一些内容到buf中，如果pos==end或者end==-1，抛异常
		 * 
		 * @throws IOException
		 */
		private void fillBuf() throws IOException {
			int result = in.read(buf, 0, buf.length);
			if (result == -1) {
				throw new EOFException();
			}
			pos = 0;
			end = result;
		}
	}

}
