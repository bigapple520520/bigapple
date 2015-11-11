package com.xuan.bigapple.lib.bitmap.listeners;

/**
 * 图片如果不存在缓存，就会触发下载，这个回调就是监听下载的过程。<br>
 * 如果图片从缓存中加载，不会触发该监听。<br>
 * 
 * 用途：有些图片网络上加载比较耗时，可以用该回调设置显示隐藏滚动条。
 * 
 * @author xuan
 */
public interface DownloaderProcessListener {
	/**
	 * 开始下载任务
	 * 
	 * @param url
	 *            下载地址
	 */
	void onStartLoading(String url);

	/**
	 * 下载中回调，随时回调下载情况
	 * 
	 * @param total
	 * @param current
	 */
	void onLoading(int total, int current);

	/**
	 * 下载完成
	 */
	void onEndLoading();

}
