package com.xuan.bigapple.lib.http.impl.httpclient.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;

import android.text.TextUtils;

import com.xuan.bigapple.lib.http.callback.ResultHandlerCallback;

/**
 * 返回结果是文件的方式处理
 * 
 * @author xuan
 */
public class FileResultHandler {
	private static final int BUFFER_SIZE = 1024;
	private boolean stop = false;

	/**
	 * 处理
	 * 
	 * @param entity
	 *            处理实体
	 * @param resultHandlerCallback
	 *            处理时回调接口
	 * @param target
	 *            文件存放路径
	 * @param isResume
	 *            是否是断点续传
	 * @return
	 * @throws IOException
	 */
	public File handleEntity(HttpEntity entity,
			ResultHandlerCallback resultHandlerCallback, String target,
			boolean isResume) throws IOException {
		if (TextUtils.isEmpty(target) || target.trim().length() == 0) {
			return null;
		}

		File targetFile = new File(target);
		File parentFile = targetFile.getParentFile();
		if (!parentFile.exists() || !parentFile.isDirectory()) {
			parentFile.mkdirs();
		}

		if (!targetFile.exists()) {
			targetFile.createNewFile();
		}

		if (stop) {
			return targetFile;
		}

		long current = 0;
		FileOutputStream fileOutputStream = null;
		if (isResume) {
			current = targetFile.length();
			fileOutputStream = new FileOutputStream(target, true);
		} else {
			fileOutputStream = new FileOutputStream(target);
		}

		if (stop) {
			quietlyClose(fileOutputStream);
			return targetFile;
		}

		InputStream input = entity.getContent();
		long count = entity.getContentLength() + current;

		if (current >= count || stop) {
			quietlyClose(fileOutputStream);
			return targetFile;
		}

		int readLen = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		while (!stop && !(current >= count)
				&& ((readLen = input.read(buffer, 0, BUFFER_SIZE)) > 0)) {
			fileOutputStream.write(buffer, 0, readLen);
			current += readLen;

			if (null != resultHandlerCallback) {
				resultHandlerCallback.callBack(count, current, false);
			}
		}

		if (null != resultHandlerCallback) {
			resultHandlerCallback.callBack(count, current, true);
		}

		// 用户主动停止
		if (stop && current < count) {
			quietlyClose(fileOutputStream);
			throw new IOException("User stop download thread!");
		}

		quietlyClose(fileOutputStream);
		return targetFile;
	}

	public boolean isStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}

	// 安静的关闭
	private void quietlyClose(FileOutputStream fileOutputStream) {
		try {
			fileOutputStream.close();
		} catch (Exception e) {
			// Ignore
		}
	}

}
