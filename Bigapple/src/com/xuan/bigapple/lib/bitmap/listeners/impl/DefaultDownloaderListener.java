package com.xuan.bigapple.lib.bitmap.listeners.impl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.xuan.bigapple.lib.bitmap.listeners.DownloaderListener;
import com.xuan.bigapple.lib.bitmap.listeners.DownloaderProcessListener;
import com.xuan.bigapple.lib.io.IOUtils;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 图片下载器接口，简单实现
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午5:30:37 $
 */
public class DefaultDownloaderListener implements DownloaderListener {
	public static final int BUFFER_SIZE = 4096;// 4K
	public static final int CONNECT_TIMEOUT = 1000 * 15;// 15S
	public static final int READ_TIMEOUT = 1000 * 30;// 30S

	/**
	 * 把网络或本地图片下载到文件的 outputStream
	 * 
	 * @param uri
	 *            加载图片地址，('/'打头的加载本地，其余从网络中加载)
	 * @param outputStream
	 *            加载输出流
	 * @param callback
	 *            加载中回调
	 * 
	 * @return 图片过期时间点； 小于零，下载失败
	 */
	@Override
	public long downloadToStream(String uri, OutputStream outputStream,
			DownloaderProcessListener callback) {
		if (null != callback) {
			// 开始加载回调
			callback.onStartLoading(uri);
		}

		URLConnection urlConnection = null;
		BufferedInputStream bis = null;

		long result = -1;
		try {
			if (uri.startsWith("/")) {
				bis = new BufferedInputStream(new FileInputStream(uri));
				result = System.currentTimeMillis() + getDefaultExpiry();
			} else {
				final URL url = new URL(uri);
				urlConnection = url.openConnection();
				urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
				urlConnection.setReadTimeout(READ_TIMEOUT);
				bis = new BufferedInputStream(urlConnection.getInputStream());
				result = urlConnection.getExpiration(); // 如果header中不包含expires返回0
				result = result < System.currentTimeMillis() ? System
						.currentTimeMillis() + getDefaultExpiry() : result;
			}

			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			int curCount = 0;

			int total = -1;
			if (null != urlConnection) {
				// 表示网络加载
				total = urlConnection.getContentLength();
			}

			while ((len = bis.read(buffer)) != -1) {
				curCount += len;

				if (null != callback) {
					// 加载中回调
					callback.onLoading(total, curCount);
				}

				outputStream.write(buffer, 0, len);
			}

			if (null != callback) {
				// 加载结束回调
				callback.onEndLoading();
			}

			outputStream.flush();
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
		} finally {
			IOUtils.closeQuietly(bis);
		}

		return result;
	}

	private long defaultExpiry;

	@Override
	public void setDefaultExpiry(long expiry) {
		this.defaultExpiry = expiry;
	}

	@Override
	public long getDefaultExpiry() {
		return this.defaultExpiry;
	}

}
