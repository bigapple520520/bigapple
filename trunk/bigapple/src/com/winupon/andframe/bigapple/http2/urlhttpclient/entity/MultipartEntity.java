package com.winupon.andframe.bigapple.http2.urlhttpclient.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import com.winupon.andframe.bigapple.http2.urlhttpclient.core.FileWraper;
import com.winupon.andframe.bigapple.http2.urlhttpclient.core.RequestParams;
import com.winupon.andframe.bigapple.io.IOUtils;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 对应表单上传文件
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:49:53 $
 */
public class MultipartEntity implements Entity {
	private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
			.toCharArray();

	private String boundary = null;// 边界
	ByteArrayOutputStream out = new ByteArrayOutputStream();

	boolean isSetLast = false;
	boolean isSetFirst = false;

	private final RequestParams requestParams;

	public MultipartEntity(RequestParams requestParams) {
		initBoundary();
		this.requestParams = requestParams;
	}

	// 初始化一个随机分隔符
	private void initBoundary() {
		final StringBuilder sb = new StringBuilder();
		final Random rand = new Random();
		for (int i = 0; i < 30; i++) {
			sb.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
		}
		this.boundary = sb.toString();
	}

	/**
	 * 设置第一个分界
	 */
	public void writeFirstBoundaryIfNeeds() {
		if (isSetFirst) {
			return;
		}

		try {
			out.write(("--" + boundary + "\r\n").getBytes());
		} catch (IOException e) {
			LogUtils.e("", e);
		}

		isSetFirst = true;
	}

	/**
	 * 设置最后一个分界
	 */
	public void writeLastBoundaryIfNeeds() {
		if (isSetLast) {
			return;
		}

		try {
			out.write(("\r\n--" + boundary + "--\r\n").getBytes());
		} catch (final IOException e) {
			LogUtils.e("", e);
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
		} catch (IOException e) {
			LogUtils.e("", e);
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

			final byte[] tmp = new byte[4 * 1024];
			int l = 0;
			while ((l = fin.read(tmp)) != -1) {
				out.write(tmp, 0, l);
			}
			if (!isLast) {
				out.write(("\r\n--" + boundary + "\r\n").getBytes());
			}
			out.flush();
		} catch (final IOException e) {
			LogUtils.e("", e);
		} finally {
			IOUtils.closeQuietly(fin);
		}
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		Map<String, String> paramMap = null;
		Map<String, FileWraper> fileParamMap = null;

		if (null != requestParams) {
			paramMap = requestParams.getParamMap();
			fileParamMap = requestParams.getFileParamMap();
		}

		// 添加普通参数
		if (null != paramMap && !paramMap.isEmpty()) {
			for (ConcurrentHashMap.Entry<String, String> entry : paramMap
					.entrySet()) {
				addPart(entry.getKey(), entry.getValue());
			}
		}

		// 添加文件参数
		if (null != fileParamMap && !fileParamMap.isEmpty()) {
			int currentIndex = 0;
			int lastIndex = fileParamMap.entrySet().size() - 1;
			for (ConcurrentHashMap.Entry<String, FileWraper> entry : fileParamMap
					.entrySet()) {
				FileWraper file = entry.getValue();
				if (file.inputStream != null) {
					boolean isLast = (currentIndex == lastIndex);
					if (file.contentType != null) {
						addPart(entry.getKey(), file.getFileName(),
								file.inputStream, file.contentType, isLast);
					} else {
						addPart(entry.getKey(), file.getFileName(),
								file.inputStream, isLast);
					}
				}
				currentIndex++;
			}
		}

		writeLastBoundaryIfNeeds();
		outstream.write(out.toByteArray());
	}

	public String getBoundary() {
		return boundary;
	}

	public void setBoundary(String boundary) {
		this.boundary = boundary;
	}

}
