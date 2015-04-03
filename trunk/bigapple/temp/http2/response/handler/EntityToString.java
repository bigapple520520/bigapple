package com.winupon.andframe.bigapple.http2.response.handler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import android.text.TextUtils;

import com.winupon.andframe.bigapple.io.IOUtils;

/**
 * 返回流的字符串方式处理
 * 
 * @author xuan
 */
public class EntityToString {
	private static int BUFFER_SIZE = 1024;
	private static String DEFAULT_CHARSET = "utf-8";

	/**
	 * 响应实体处理成字符串,默认utf8编码
	 * 
	 * @param connection
	 * @return
	 * @throws IOException
	 */
	public String toString(HttpURLConnection connection) throws IOException {
		return toString(connection, null);
	}

	/**
	 * 响应实体处理成字符串
	 * 
	 * @param connection
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public String toString(HttpURLConnection connection, String charset)
			throws IOException {
		return toString(connection, null, charset);
	}

	/**
	 * 响应实体处理成字符串
	 * 
	 * @param connection
	 * @param callBack
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public String toString(HttpURLConnection connection,
			HandlerCallBack callback, String charset) throws IOException {
		InputStream inputStream = connection.getInputStream();

		if (null == inputStream) {
			return null;
		}

		if (TextUtils.isEmpty(charset)) {
			// 默认使用utf8编码
			charset = DEFAULT_CHARSET;
		}

		ByteArrayOutputStream outStream = null;
		InputStream is = null;
		try {
			is = new BufferedInputStream(inputStream);
			outStream = new ByteArrayOutputStream();

			byte[] buffer = new byte[BUFFER_SIZE];
			long count = connection.getContentLength();
			long curCount = 0;
			int len = -1;
			while ((len = is.read(buffer)) != -1) {
				outStream.write(buffer, 0, len);
				curCount += len;

				if (null != callback) {
					callback.callBack(count, curCount, false);
				}
			}

			if (null != callback) {
				callback.callBack(count, curCount, true);
			}
		} catch (Exception e) {
		} finally {
			IOUtils.closeQuietly(is);
		}

		return new String(outStream.toByteArray(), charset);
	}

}
