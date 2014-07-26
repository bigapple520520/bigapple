package com.winupon.andframe.bigapple.http2.response.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import android.text.TextUtils;

import com.winupon.andframe.bigapple.io.IOUtils;

/**
 * 返回流的字符串方式处理，保存到文件
 * 
 * @author xuan
 * 
 */
public class EntityToFile {
	private static final int BUFFER_SIZE = 4 * 1024;
	private volatile boolean stop = false;

	/**
	 * 响应实体保存成文件
	 * 
	 * @param connection
	 * @param callback
	 * @param target
	 * @return
	 * @throws IOException
	 */
	public File toFile(HttpURLConnection connection, HandlerCallBack callback,
			String target) throws IOException {
		return toFile(connection, callback, target, false);
	}

	/**
	 * 响应实体保存成文件
	 * 
	 * @param connection
	 * @param callback
	 * @param target
	 * @param isResume
	 * @return
	 * @throws IOException
	 */
	public File toFile(HttpURLConnection connection, HandlerCallBack callback,
			String target, boolean isResume) throws IOException {
		if (TextUtils.isEmpty(target)) {
			return null;
		}

		File targetFile = new File(target);
		File parentFile = targetFile.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}

		if (stop) {
			return targetFile;
		}

		long current = 0;
		FileOutputStream out = null;
		if (isResume) {
			current = targetFile.length();
			out = new FileOutputStream(target, true);
		} else {
			out = new FileOutputStream(target);
		}

		if (stop) {
			IOUtils.closeQuietly(out);
			return targetFile;
		}

		InputStream in = new BufferedInputStream(connection.getInputStream());
		long count = connection.getContentLength() + current;

		if (current >= count || stop) {
			IOUtils.closeQuietly(out);
			return targetFile;
		}

		int readLen = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		while (!stop && current < count
				&& ((readLen = in.read(buffer, 0, BUFFER_SIZE)) > 0)) {
			out.write(buffer, 0, readLen);
			current += readLen;

			if (null != callback) {
				callback.callBack(count, current, false);
			}
		}

		if (null != callback) {
			callback.callBack(count, current, true);
		}

		if (stop && current < count) {
			// 用户主动停止
			IOUtils.closeQuietly(out);
			throw new IOException("user stop download thread");
		}

		IOUtils.closeQuietly(out);
		return targetFile;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

}
