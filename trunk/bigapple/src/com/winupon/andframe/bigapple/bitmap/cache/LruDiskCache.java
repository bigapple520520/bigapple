package com.winupon.andframe.bigapple.bitmap.cache;

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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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

import com.winupon.andframe.bigapple.io.IOUtils;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * A cache that uses a bounded amount of space on a filesystem. Each cache entry has a string key and a fixed number of
 * values. Values are byte sequences, accessible as streams or files. Each value must be between {@code 0} and
 * {@code Integer.MAX_VALUE} bytes in length.
 * <p/>
 * <p>
 * The cache stores its data in a directory on the filesystem. This directory must be exclusive to the cache; the cache
 * may delete or overwrite files from its directory. It is an error for multiple processes to use the same cache
 * directory at the same time.
 * <p/>
 * <p>
 * This cache limits the number of bytes that it will store on the filesystem. When the number of stored bytes exceeds
 * the limit, the cache will remove entries in the background until the limit is satisfied. The limit is not strict: the
 * cache may temporarily exceed it while waiting for files to be deleted. The limit does not include filesystem overhead
 * or the cache journal so space-sensitive applications should set a conservative limit.
 * <p/>
 * <p>
 * Clients call {@link #edit} to create or update the values of an entry. An entry may have only one editor at one time;
 * if a value is not available to be edited then {@link #edit} will return null.
 * <ul>
 * <li>When an entry is being <strong>created</strong> it is necessary to supply a full set of values; the empty value
 * should be used as a placeholder if necessary.
 * <li>When an entry is being <strong>edited</strong>, it is not necessary to supply data for every value; values
 * default to their previous value.
 * </ul>
 * Every {@link #edit} call must be matched by a call to {@link Editor#commit} or {@link Editor#abort}. Committing is
 * atomic: a read observes the full set of values as they were before or after the commit, but never a mix of values.
 * <p/>
 * <p>
 * Clients call {@link #get} to read a snapshot of an entry. The read will observe the value at the time that
 * {@link #get} was called. Updates and removals after the call do not impact ongoing reads.
 * <p/>
 * <p>
 * This class is tolerant of some I/O errors. If files are missing from the filesystem, the corresponding entries will
 * be dropped from the cache. If an error occurs while writing a cache value, the edit will fail silently. Callers
 * should handle other problems by catching {@code IOException} and responding appropriately.
 */
public final class LruDiskCache implements Closeable {
    static final String JOURNAL_FILE = "journal";
    static final String JOURNAL_FILE_TEMP = "journal.tmp";
    static final String JOURNAL_FILE_BACKUP = "journal.bkp";

    static final String MAGIC = "libcore.io.DiskLruCache";
    static final String VERSION_1 = "1";
    static final long ANY_SEQUENCE_NUMBER = -1;
    static final Pattern LEGAL_KEY_PATTERN = Pattern.compile("[a-z0-9_-]{1,64}");

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
     * DIRTY 1a7bd9492ce711c1c9400fd885296988 <br>
     * CLEAN 1a7bd9492ce711c1c9400fd885296988 t_1382060889349 55361 <br>
     * REMOVE 335c4c6028171cfddfbaae1a9c313c52 <br>
     * READ 335c4c6028171cfddfbaae1a9c313c52 <br>
     * READ 3400330d1dfc7f3f7f4b8d4d803dfcf6
     * 
     * 
     * Each of the subsequent lines in the file is a record of the state of a cache entry. Each line contains
     * space-separated values: a state, a key, and optional state-specific values. o DIRTY lines track that an entry is
     * actively being created or updated. Every successful DIRTY action should be followed by a CLEAN or REMOVE action.
     * DIRTY lines without a matching CLEAN or REMOVE indicate that temporary files may need to be deleted. o CLEAN
     * lines track a cache entry that has been successfully published and may be read. A publish line is followed by the
     * lengths of each of its values. o READ lines track accesses for LRU. o REMOVE lines track entries that have been
     * deleted.
     * 
     * The journal file is appended to as cache operations occur. The journal may occasionally be compacted by dropping
     * redundant lines. A temporary file named "journal.tmp" will be used during compaction; that file should be deleted
     * if it exists when the cache is opened.
     */

    private final File directory;// 缓存目录
    private final File journalFile;
    private final File journalFileTmp;
    private final File journalFileBackup;

    private final int appVersion;
    private long maxSize;
    private final int valueCount;
    private long size = 0;

    private Writer journalWriter;
    private final LinkedHashMap<String, Entry> lruEntries = new LinkedHashMap<String, Entry>(0, 0.75f, true);
    private int redundantOpCount;

    private long nextSequenceNumber = 0;

    final ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, 1, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());
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

    private LruDiskCache(File directory, int appVersion, int valueCount, long maxSize) {
        this.directory = directory;
        this.appVersion = appVersion;
        this.journalFile = new File(directory, JOURNAL_FILE);
        this.journalFileTmp = new File(directory, JOURNAL_FILE_TEMP);
        this.journalFileBackup = new File(directory, JOURNAL_FILE_BACKUP);
        this.valueCount = valueCount;
        this.maxSize = maxSize;
    }

    /**
     * 初始化打开磁盘缓存
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
    public static LruDiskCache open(File directory, int appVersion, int valueCount, long maxSize) throws IOException {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("非法参数错误，原因： 缓存最大容量maxSize <= 0");
        }

        if (valueCount <= 0) {
            throw new IllegalArgumentException("非法参数错误，原因：缓存数valueCount <= 0");
        }

        // 如果备份文件存在，说明上一次在记录日志的时候异常退出了，所以恢复备份文件
        File backupFile = new File(directory, JOURNAL_FILE_BACKUP);
        if (backupFile.exists()) {
            File journalFile = new File(directory, JOURNAL_FILE);
            if (journalFile.exists()) {
                backupFile.delete();// 删除异常记录的日志文件
            }
            else {
                renameTo(backupFile, journalFile, false);// 恢复备份文件
            }
        }

        // Prefer to pick up where we left off.
        LruDiskCache cache = new LruDiskCache(directory, appVersion, valueCount, maxSize);
        if (cache.journalFile.exists()) {
            try {
                cache.readJournal();
                cache.processJournal();
                cache.journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cache.journalFile,
                        true), HTTP.US_ASCII));
                return cache;
            }
            catch (IOException journalIsCorrupt) {
                LogUtils.e(
                        "DiskLruCache " + directory + " is corrupt: " + journalIsCorrupt.getMessage() + ", removing",
                        journalIsCorrupt);
                cache.delete();
            }
        }

        // 第一次，新建目录和日志文件
        directory.mkdirs();
        cache = new LruDiskCache(directory, appVersion, valueCount, maxSize);
        cache.rebuildJournal();
        return cache;
    }

    // 读取日志文件
    private void readJournal() throws IOException {
        StrictLineReader reader = null;
        try {
            reader = new StrictLineReader(new FileInputStream(journalFile));
            String magic = reader.readLine();
            String version = reader.readLine();
            String appVersionString = reader.readLine();
            String valueCountString = reader.readLine();
            String blank = reader.readLine();
            if (!MAGIC.equals(magic) || !VERSION_1.equals(version)
                    || !Integer.toString(appVersion).equals(appVersionString)
                    || !Integer.toString(valueCount).equals(valueCountString) || !"".equals(blank)) {
                throw new IOException("日志文件头格式错误: [" + magic + ", " + version + ", " + valueCountString + ", " + blank
                        + "]");
            }

            int lineCount = 0;
            while (true) {
                try {
                    readJournalLine(reader.readLine());
                    lineCount++;
                }
                catch (EOFException endOfJournal) {
                    // 日子读取到最后一行
                    break;
                }
            }
            redundantOpCount = lineCount - lruEntries.size();// 冗余的数量
        }
        finally {
            IOUtils.closeQuietly(reader);
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
        final String diskKey;
        if (secondSpace == -1) {
            diskKey = line.substring(keyBegin);
            if (firstSpace == REMOVE.length() && line.startsWith(REMOVE)) {
                // REMOVE记录处理
                lruEntries.remove(diskKey);
                return;
            }
        }
        else {
            diskKey = line.substring(keyBegin, secondSpace);
        }

        Entry entry = lruEntries.get(diskKey);
        if (entry == null) {
            entry = new Entry(diskKey);
            lruEntries.put(diskKey, entry);
        }

        if (secondSpace != -1 && firstSpace == CLEAN.length() && line.startsWith(CLEAN)) {
            // CLEAN日志处理
            entry.readable = true;
            entry.currentEditor = null;
            String[] parts = line.substring(secondSpace + 1).split(" ");
            if (parts.length > 0) {
                try {
                    if (parts[0].startsWith("t_")) {
                        entry.expiryTimestamp = Long.valueOf(parts[0].substring(2));
                        entry.setLengths(parts, 1);
                    }
                    else {
                        entry.expiryTimestamp = Long.MAX_VALUE;
                        entry.setLengths(parts, 0);
                    }
                }
                catch (Exception e) {
                    throw new IOException("unexpected journal line: " + line);
                }
            }
        }
        else if (secondSpace == -1 && firstSpace == DIRTY.length() && line.startsWith(DIRTY)) {
            // DIRTY日志处理
            entry.currentEditor = new Editor(entry);
        }
        else if (secondSpace == -1 && firstSpace == READ.length() && line.startsWith(READ)) {
            // READ日志处理
            // This work was already done by calling lruEntries.get().
        }
        else {
            throw new IOException("unexpected journal line: " + line);
        }
    }

    // 处理日志
    private void processJournal() throws IOException {
        deleteIfExists(journalFileTmp);
        for (Iterator<Entry> i = lruEntries.values().iterator(); i.hasNext();) {
            Entry entry = i.next();
            if (entry.currentEditor == null) {
                for (int t = 0; t < valueCount; t++) {
                    size += entry.lengths[t];
                }
            }
            else {
                entry.currentEditor = null;
                for (int t = 0; t < valueCount; t++) {
                    deleteIfExists(entry.getCleanFile(t));
                    deleteIfExists(entry.getDirtyFile(t));
                }
                i.remove();
            }
        }
    }

    // 重新记录日志，如果已有日志存在，将会被替换
    private synchronized void rebuildJournal() throws IOException {
        if (journalWriter != null) {
            IOUtils.closeQuietly(journalWriter);
        }

        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(journalFileTmp), HTTP.US_ASCII));
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
                    writer.write(DIRTY + ' ' + entry.diskKey + '\n');
                }
                else {
                    writer.write(CLEAN + ' ' + entry.diskKey + " t_" + entry.expiryTimestamp + entry.getLengths()
                            + '\n');
                }
            }
        }
        finally {
            IOUtils.closeQuietly(writer);
        }

        if (journalFile.exists()) {
            renameTo(journalFile, journalFileBackup, true);
        }
        renameTo(journalFileTmp, journalFile, false);
        journalFileBackup.delete();

        journalWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(journalFile, true),
                HTTP.US_ASCII));
    }

    private static void deleteIfExists(File file) throws IOException {
        if (file.exists() && !file.delete()) {
            throw new IOException();
        }
    }

    private static void renameTo(File from, File to, boolean deleteDestination) throws IOException {
        if (deleteDestination) {
            deleteIfExists(to);
        }
        if (!from.renameTo(to)) {
            throw new IOException();
        }
    }

    public synchronized long getExpiryTimestamp(String key) throws IOException {
        String diskKey = DiskCacheKeyGenerator.generate(key);
        checkNotClosed();
        validateKey(diskKey);
        Entry entry = lruEntries.get(diskKey);
        if (entry == null) {
            return 0;
        }
        else {
            return entry.expiryTimestamp;
        }
    }

    public Snapshot get(String key) throws IOException {
        String diskKey = DiskCacheKeyGenerator.generate(key);
        return getByDiskKey(diskKey);
    }

    private synchronized Snapshot getByDiskKey(String diskKey) throws IOException {
        checkNotClosed();
        validateKey(diskKey);
        Entry entry = lruEntries.get(diskKey);
        if (entry == null) {
            return null;
        }

        if (!entry.readable) {
            return null;
        }

        // If expired, delete the entry.
        if (entry.expiryTimestamp < System.currentTimeMillis()) {
            for (int i = 0; i < valueCount; i++) {
                File file = entry.getCleanFile(i);
                if (file.exists() && !file.delete()) {
                    throw new IOException("failed to delete " + file);
                }
                size -= entry.lengths[i];
                entry.lengths[i] = 0;
            }
            redundantOpCount++;
            journalWriter.append(REMOVE + ' ' + diskKey + '\n');
            lruEntries.remove(diskKey);
            if (journalRebuildRequired()) {
                executorService.submit(cleanupCallable);
            }
            return null;
        }

        // Open all streams eagerly to guarantee that we see a single published
        // snapshot. If we opened streams lazily then the streams could come
        // from different edits.
        FileInputStream[] ins = new FileInputStream[valueCount];
        try {
            for (int i = 0; i < valueCount; i++) {
                ins[i] = new FileInputStream(entry.getCleanFile(i));
            }
        }
        catch (FileNotFoundException e) {
            // A file must have been deleted manually!
            for (int i = 0; i < valueCount; i++) {
                if (ins[i] != null) {
                    IOUtils.closeQuietly(ins[i]);
                }
                else {
                    break;
                }
            }
            return null;
        }

        redundantOpCount++;
        journalWriter.append(READ + ' ' + diskKey + '\n');
        if (journalRebuildRequired()) {
            executorService.submit(cleanupCallable);
        }

        return new Snapshot(diskKey, entry.sequenceNumber, ins, entry.lengths);
    }

    public Editor edit(String key) throws IOException {
        String diskKey = DiskCacheKeyGenerator.generate(key);
        return editByDiskKey(diskKey, ANY_SEQUENCE_NUMBER);
    }

    private synchronized Editor editByDiskKey(String diskKey, long expectedSequenceNumber) throws IOException {
        checkNotClosed();
        validateKey(diskKey);
        Entry entry = lruEntries.get(diskKey);
        if (expectedSequenceNumber != ANY_SEQUENCE_NUMBER
                && (entry == null || entry.sequenceNumber != expectedSequenceNumber)) {
            return null; // Snapshot is stale.
        }

        if (entry == null) {
            entry = new Entry(diskKey);
            lruEntries.put(diskKey, entry);
        }
        else if (entry.currentEditor != null) {
            return null; // Another edit is in progress.
        }

        Editor editor = new Editor(entry);
        entry.currentEditor = editor;

        // Flush the journal before creating files to prevent file leaks.
        journalWriter.write(DIRTY + ' ' + diskKey + '\n');
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

    private synchronized void completeEdit(Editor editor, boolean success) throws IOException {
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
                    throw new IllegalStateException("Newly created entry didn't create value for index " + i);
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
            }
            else {
                deleteIfExists(dirty);
            }
        }

        redundantOpCount++;
        entry.currentEditor = null;
        if (entry.readable | success) {
            entry.readable = true;
            journalWriter
                    .write(CLEAN + ' ' + entry.diskKey + " t_" + entry.expiryTimestamp + entry.getLengths() + '\n');
            if (success) {
                entry.sequenceNumber = nextSequenceNumber++;
            }
        }
        else {
            lruEntries.remove(entry.diskKey);
            journalWriter.write(REMOVE + ' ' + entry.diskKey + '\n');
        }
        journalWriter.flush();

        if (size > maxSize || journalRebuildRequired()) {
            executorService.submit(cleanupCallable);
        }
    }

    private boolean journalRebuildRequired() {
        final int redundantOpCompactThreshold = 2000;
        return redundantOpCount >= redundantOpCompactThreshold //
                && redundantOpCount >= lruEntries.size();
    }

    /**
     * 删除缓存
     * 
     * @param key
     * @return
     * @throws IOException
     */
    public boolean remove(String key) throws IOException {
        String diskKey = DiskCacheKeyGenerator.generate(key);
        return removeByDiskKey(diskKey);
    }

    private synchronized boolean removeByDiskKey(String diskKey) throws IOException {
        checkNotClosed();
        validateKey(diskKey);
        Entry entry = lruEntries.get(diskKey);
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
        journalWriter.append(REMOVE + ' ' + diskKey + '\n');
        lruEntries.remove(diskKey);

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
            Map.Entry<String, Entry> toEvict = lruEntries.entrySet().iterator().next();
            removeByDiskKey(toEvict.getKey());
        }
    }

    public void delete() throws IOException {
        IOUtils.closeQuietly(this);
        deleteContents(directory);
    }

    private void validateKey(String diskKey) {
        Matcher matcher = LEGAL_KEY_PATTERN.matcher(diskKey);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("key必须符合正则表达式：[a-z0-9_-]{1,64}: \"" + diskKey + "\"");
        }
    }

    private static String inputStreamToString(InputStream in) throws IOException {
        return readFully(new InputStreamReader(in, HTTP.UTF_8));
    }

    private static final OutputStream NULL_OUTPUT_STREAM = new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            // Eat all writes silently. Nom nom.
        }
    };

    // ///////////////////////////////////// 工具方法
    // //////////////////////////////////////////////////////////////////
    private static String readFully(Reader reader) throws IOException {
        StringWriter writer = null;
        try {
            writer = new StringWriter();
            char[] buffer = new char[1024];
            int count;
            while ((count = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, count);
            }
            return writer.toString();
        }
        finally {
            IOUtils.closeQuietly(reader);
            IOUtils.closeQuietly(writer);
        }
    }

    // 递归删除
    private static void deleteContents(File dir) throws IOException {
        File[] files = dir.listFiles();
        if (files == null) {
            throw new IOException("not a readable directory: " + dir);
        }
        for (File file : files) {
            if (file.isDirectory()) {
                deleteContents(file);
            }
            if (file.exists() && !file.delete()) {
                throw new IOException("failed to delete file: " + file);
            }
        }
    }

    // ////////////////////////////////////////////内部类/////////////////////////////////////////////////////////////

    // ////////////////////////////////////严格的行阅读器StrictLineReader//////////////////////////////////////////////
    /**
     * 严格的行阅读器StrictLineReader
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-17 下午7:21:29 $
     */
    private class StrictLineReader implements Closeable {
        private static final byte CR = (byte) '\r';
        private static final byte LF = (byte) '\n';

        private final InputStream in;
        private final Charset charset = Charset.forName(HTTP.US_ASCII);

        /*
         * Buffered data is stored in {@code buf}. As long as no exception occurs, 0 <= pos <= end and the data in the
         * range [pos, end) is buffered for reading. At end of input, if there is an unterminated line, we set end ==
         * -1, otherwise end == pos. If the underlying {@code InputStream} throws an {@code IOException}, end may remain
         * as either pos or -1.
         */
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
         * Reads the next line. A line ends with {@code "\n"} or {@code "\r\n"}, this end of line marker is not included
         * in the result.
         * 
         * @return the next line from the input.
         * @throws IOException
         *             for underlying {@code InputStream} errors.
         * @throws EOFException
         *             for the end of source stream.
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
                // Try to find LF in the buffered data and return the line if
                // successful.
                for (int i = pos; i != end; ++i) {
                    if (buf[i] == LF) {
                        int lineEnd = (i != pos && buf[i - 1] == CR) ? i - 1 : i;
                        String res = new String(buf, pos, lineEnd - pos, charset.name());
                        pos = i + 1;
                        return res;
                    }
                }

                // Let's anticipate up to 80 characters on top of those already
                // read.
                ByteArrayOutputStream out = new ByteArrayOutputStream(end - pos + 80) {
                    @Override
                    public String toString() {
                        int length = (count > 0 && buf[count - 1] == CR) ? count - 1 : count;
                        try {
                            return new String(buf, 0, length, charset.name());
                        }
                        catch (UnsupportedEncodingException e) {
                            throw new AssertionError(e); // Since we control the
                                                         // charset this will
                                                         // never happen.
                        }
                    }
                };

                while (true) {
                    out.write(buf, pos, end - pos);
                    // Mark unterminated line in case fillBuf throws
                    // EOFException or IOException.
                    end = -1;
                    fillBuf();
                    // Try to find LF in the buffered data and return the line
                    // if successful.
                    for (int i = pos; i != end; ++i) {
                        if (buf[i] == LF) {
                            if (i != pos) {
                                out.write(buf, pos, i - pos);
                            }
                            out.flush();
                            pos = i + 1;
                            return out.toString();
                        }
                    }
                }
            }
        }

        /**
         * Reads new input data into the buffer. Call only with pos == end or end == -1, depending on the desired
         * outcome if the function throws.
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

    // //////////////////////////////////////////磁盘缓存key生成器/////////////////////////////////////////////////////
    /**
     * 磁盘缓存key生成器
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-17 下午7:19:33 $
     */
    private static class DiskCacheKeyGenerator {
        private DiskCacheKeyGenerator() {
        }

        public static String generate(String key) {
            String cacheKey;
            try {
                final MessageDigest mDigest = MessageDigest.getInstance("MD5");
                mDigest.update(key.getBytes());
                cacheKey = bytesToHexString(mDigest.digest());
            }
            catch (NoSuchAlgorithmException e) {
                // 当请求算法在特定环境中不可用时
                cacheKey = String.valueOf(key.hashCode());
            }
            return cacheKey;
        }

        // 字节转16进制串
        private static String bytesToHexString(byte[] bytes) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < bytes.length; i++) {
                String hex = Integer.toHexString(0xFF & bytes[i]);
                if (hex.length() == 1) {
                    sb.append('0');
                }
                sb.append(hex);
            }
            return sb.toString();
        }
    }

    /**
     * 对应一个key的文件条目（一个key可包含多个文件）
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-17 下午7:57:30 $
     */
    private final class Entry {
        private final String diskKey;

        private long expiryTimestamp = Long.MAX_VALUE;// 过期时间

        private final long[] lengths;// 条目文件的长度
        private boolean readable;// 条目文件是否可读
        private Editor currentEditor;// 条目的编辑器
        private long sequenceNumber;// 条目流水号

        private Entry(String diskKey) {
            this.diskKey = diskKey;
            this.lengths = new long[valueCount];
        }

        public String getLengths() throws IOException {
            StringBuilder result = new StringBuilder();
            for (long size : lengths) {
                result.append(' ').append(size);
            }
            return result.toString();
        }

        /**
         * 设置长度，请用十进制，例如："10123"
         * 
         * @param strings
         *            十进制数组
         * @param startIndex
         *            开始设置的位置索引
         * @throws IOException
         */
        private void setLengths(String[] strings, int startIndex) throws IOException {
            if ((strings.length - startIndex) != valueCount) {
                throw invalidLengths(strings);
            }

            try {
                for (int i = 0; i < valueCount; i++) {
                    lengths[i] = Long.parseLong(strings[i + startIndex]);
                }
            }
            catch (NumberFormatException e) {
                throw invalidLengths(strings);
            }
        }

        private IOException invalidLengths(String[] strings) throws IOException {
            throw new IOException("unexpected journal line: " + java.util.Arrays.toString(strings));
        }

        public File getCleanFile(int i) {
            return new File(directory, diskKey + "." + i);
        }

        public File getDirtyFile(int i) {
            return new File(directory, diskKey + "." + i + ".tmp");
        }
    }

    /**
     * 条目文件的编辑器，用于对文件的读写
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-17 下午8:00:54 $
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

        public void setEntryExpiryTimestamp(long timestamp) {
            entry.expiryTimestamp = timestamp;
        }

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
                }
                catch (FileNotFoundException e) {
                    return null;
                }
            }
        }

        public String getString(int index) throws IOException {
            InputStream in = newInputStream(index);
            return in != null ? inputStreamToString(in) : null;
        }

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
                }
                catch (FileNotFoundException e) {
                    // 创建缓存目录
                    directory.mkdirs();
                    try {
                        outputStream = new FileOutputStream(dirtyFile);
                    }
                    catch (FileNotFoundException e2) {
                        // 无法恢复文件，返回一个空输出流
                        return NULL_OUTPUT_STREAM;
                    }
                }
                return new FaultHidingOutputStream(outputStream);
            }
        }

        public void set(int index, String value) throws IOException {
            Writer writer = null;
            try {
                writer = new OutputStreamWriter(newOutputStream(index), HTTP.UTF_8);
                writer.write(value);
            }
            finally {
                IOUtils.closeQuietly(writer);
            }
        }

        public void commit() throws IOException {
            if (hasErrors) {
                completeEdit(this, false);
                removeByDiskKey(entry.diskKey); // The previous entry is stale.
            }
            else {
                completeEdit(this, true);
            }
            committed = true;
        }

        public void abort() throws IOException {
            completeEdit(this, false);
        }

        public void abortUnlessCommitted() {
            if (!committed) {
                try {
                    abort();
                }
                catch (IOException ignored) {
                }
            }
        }

        /**
         * 一个静默的输出流，异常时能被默默的捕捉到
         * 
         * @author xuan
         * @version $Revision: 1.0 $, $Date: 2014-5-5 上午9:44:33 $
         */
        private class FaultHidingOutputStream extends FilterOutputStream {
            private FaultHidingOutputStream(OutputStream out) {
                super(out);
            }

            @Override
            public void write(int oneByte) {
                try {
                    out.write(oneByte);
                }
                catch (IOException e) {
                    hasErrors = true;
                }
            }

            @Override
            public void write(byte[] buffer, int offset, int length) {
                try {
                    out.write(buffer, offset, length);
                    out.flush();
                }
                catch (IOException e) {
                    hasErrors = true;
                }
            }

            @Override
            public void close() {
                try {
                    out.close();
                }
                catch (IOException e) {
                    hasErrors = true;
                }
            }

            @Override
            public void flush() {
                try {
                    out.flush();
                }
                catch (IOException e) {
                    hasErrors = true;
                }
            }
        }
    }

    /**
     * 磁盘缓存的一个条目文件（包含多个流）
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-17 下午8:07:27 $
     */
    public final class Snapshot implements Closeable {
        private final String diskKey;
        private final long sequenceNumber;
        private final FileInputStream[] ins;
        private final long[] lengths;

        private Snapshot(String diskKey, long sequenceNumber, FileInputStream[] ins, long[] lengths) {
            this.diskKey = diskKey;
            this.sequenceNumber = sequenceNumber;
            this.ins = ins;
            this.lengths = lengths;
        }

        /**
         * Returns an editor for this snapshot's entry, or null if either the entry has changed since this snapshot was
         * created or if another edit is in progress.
         */
        /**
         * 返回条目的编辑对象
         * 
         * @return
         * @throws IOException
         */
        public Editor edit() throws IOException {
            return LruDiskCache.this.editByDiskKey(diskKey, sequenceNumber);
        }

        /**
         * 获取指定流，未加过缓存的
         * 
         * @param index
         * @return
         */
        public FileInputStream getInputStream(int index) {
            return ins[index];
        }

        public String getString(int index) throws IOException {
            return inputStreamToString(getInputStream(index));
        }

        public long getLength(int index) {
            return lengths[index];
        }

        @Override
        public void close() {
            for (InputStream in : ins) {
                IOUtils.closeQuietly(in);
            }
        }
    }

}
