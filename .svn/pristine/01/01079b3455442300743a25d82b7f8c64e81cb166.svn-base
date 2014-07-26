package com.winupon.andframe.bigapple.http2.urlhttpclient.handler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import android.text.TextUtils;

import com.winupon.andframe.bigapple.io.IOUtils;

/**
 * 返回流的存储文件方式处理
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:10:03 $
 */
public class FileResultHandler {
	private static final int BUFFER_SIZE = 4 * 1024;
	private boolean stop = false;

	/**
	 * 处理返回接口成文件保存
	 * 
	 * @param connection
	 * @param callback
	 * @param target
	 * @param isResume
	 * @return
	 * @throws IOException
	 */
	public File handleResult(HttpURLConnection connection,
			ResultCallBack callback, String target, boolean isResume)
			throws IOException {
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
