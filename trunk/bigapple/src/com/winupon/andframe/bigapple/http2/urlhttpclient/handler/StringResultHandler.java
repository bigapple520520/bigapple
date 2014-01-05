package com.winupon.andframe.bigapple.http2.urlhttpclient.handler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import com.winupon.andframe.bigapple.io.IOUtils;

/**
 * 返回流的字符串方式处理
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:08:42 $
 */
public class StringResultHandler {
	private static int BUFFER_SIZE = 1024;

	/**
	 * 处理返回接口成字符串
	 * 
	 * @param connection
	 * @param callback
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public String handleResult(HttpURLConnection connection,
			ResultCallBack callback, String charset) throws IOException {
		if (null == connection) {
			return null;
		}

		int responseCode = connection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {// 返回成功
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[BUFFER_SIZE];

			long count = connection.getContentLength();
			long curCount = 0;
			int len = -1;
			InputStream is = new BufferedInputStream(
					connection.getInputStream());
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

			IOUtils.closeQuietly(is);
			return new String(outStream.toByteArray(), charset);
		} else {// 返回失败，就返回状态值
			throw new IOException(String.valueOf(responseCode));
		}
	}

}
