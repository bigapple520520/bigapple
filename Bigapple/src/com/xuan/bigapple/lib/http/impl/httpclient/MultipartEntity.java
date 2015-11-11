package com.xuan.bigapple.lib.http.impl.httpclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

/**
 * 带文件的请求体
 * 
 * @author xuan
 */
public class MultipartEntity implements HttpEntity {
	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	private String boundary = null;// 边界

	ByteArrayOutputStream out = new ByteArrayOutputStream();

	boolean isSetLast = false;// 是否设置过最后一次
	boolean isSetFirst = false;// 是否设置过第一次

	public MultipartEntity() {
		final StringBuffer buf = new StringBuffer();
		final Random rand = new Random();
		for (int i = 0; i < 30; i++) {
			buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}
		this.boundary = buf.toString();
	}

	public void writeFirstBoundaryIfNeeds() {
		if (isSetFirst) {
			return;
		}

		try {
			out.write(("--" + boundary + "\r\n").getBytes());
		} catch (final IOException e) {
			e.printStackTrace();
		}

		isSetFirst = true;
	}

	public void writeLastBoundaryIfNeeds() {
		if (isSetLast) {
			return;
		}

		try {
			out.write(("\r\n--" + boundary + "--\r\n").getBytes());
		} catch (final IOException e) {
			e.printStackTrace();
		}

		isSetLast = true;
	}

	/**
	 * 添加普通参数
	 * 
	 * @param key
	 * @param value
	 */
	public void addPart(final String key, final String value) {
		writeFirstBoundaryIfNeeds();
		try {
			out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n")
					.getBytes());
			out.write(value.getBytes());
			out.write(("\r\n--" + boundary + "\r\n").getBytes());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 添加文件
	 * 
	 * @param key
	 * @param fileName
	 * @param fin
	 * @param isLast
	 */
	public void addPart(final String key, final String fileName,
			final InputStream fin, final boolean isLast) {
		addPart(key, fileName, fin, "application/octet-stream", isLast);
	}

	/**
	 * 添加文件
	 * 
	 * @param key
	 * @param fileName
	 * @param fin
	 * @param type
	 * @param isLast
	 */
	public void addPart(final String key, final String fileName,
			final InputStream fin, String type, final boolean isLast) {
		writeFirstBoundaryIfNeeds();
		try {
			type = "Content-Type: " + type + "\r\n";
			out.write(("Content-Disposition: form-data; name=\"" + key
					+ "\"; filename=\"" + fileName + "\"\r\n").getBytes());
			out.write(type.getBytes());
			out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

			final byte[] tmp = new byte[4096];
			int l = 0;
			while ((l = fin.read(tmp)) != -1) {
				out.write(tmp, 0, l);
			}
			if (!isLast) {
				out.write(("\r\n--" + boundary + "\r\n").getBytes());
			}
			out.flush();
		} catch (final IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fin.close();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 添加文件
	 * 
	 * @param key
	 * @param value
	 * @param isLast
	 */
	public void addPart(final String key, final File value, final boolean isLast) {
		try {
			addPart(key, value.getName(), new FileInputStream(value), isLast);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long getContentLength() {
		writeLastBoundaryIfNeeds();
		return out.toByteArray().length;
	}

	@Override
	public Header getContentType() {
		return new BasicHeader("Content-Type", "multipart/form-data; boundary="
				+ boundary);
	}

	@Override
	public boolean isChunked() {
		return false;
	}

	@Override
	public boolean isRepeatable() {
		return false;
	}

	@Override
	public boolean isStreaming() {
		return false;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		outstream.write(out.toByteArray());
	}

	@Override
	public Header getContentEncoding() {
		return null;
	}

	@Override
	public void consumeContent() throws IOException,
			UnsupportedOperationException {
		if (isStreaming()) {
			throw new UnsupportedOperationException(
					"Streaming entity does not implement #consumeContent()");
		}
	}

	@Override
	public InputStream getContent() throws IOException,
			UnsupportedOperationException {
		return new ByteArrayInputStream(out.toByteArray());
	}
}
