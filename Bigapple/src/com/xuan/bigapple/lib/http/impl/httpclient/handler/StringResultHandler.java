package com.xuan.bigapple.lib.http.impl.httpclient.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;

import com.xuan.bigapple.lib.http.callback.ResultHandlerCallback;

/**
 * 返回已字符串的方式处理
 * 
 * @author xuan
 */
public class StringResultHandler {
	private static int BUFFER_SIZE = 1024;

	/**
	 * 处理
	 * 
	 * @param entity
	 *            处理实体
	 * @param resultHandlerCallback
	 *            处理时的回调
	 * @param charset
	 *            字符串的编码方式
	 * 
	 * @return
	 * @throws IOException
	 */
	public String handleEntity(HttpEntity entity,
			ResultHandlerCallback resultHandlerCallback, String charset)
			throws IOException {
		if (null == entity) {
			return null;
		}

		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUFFER_SIZE];

		long count = entity.getContentLength();
		long curCount = 0;
		int len = -1;
		InputStream is = entity.getContent();
		while ((len = is.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
			curCount += len;
			if (null != resultHandlerCallback) {
				resultHandlerCallback.callBack(count, curCount, false);
			}
		}

		if (null != resultHandlerCallback) {
			resultHandlerCallback.callBack(count, curCount, true);
		}

		byte[] data = outStream.toByteArray();
		outStream.close();
		is.close();
		return new String(data, charset);
	}

}
