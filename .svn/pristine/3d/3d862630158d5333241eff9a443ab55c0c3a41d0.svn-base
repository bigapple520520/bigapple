package com.winupon.andframe.bigapple.bitmap.callback;

/**
 * 图片如果不存在缓存，就会触发下载，这个回调就是监听下载的过程<br>
 * 如果图片从缓存中加载，不会触发该监听<br>
 * 
 * 有些图片网络上加载比较耗时，可以用该回调设置显示隐藏滚动条
 * 
 * @author xuan
 */
public interface DownloaderCallBack {
	/**
	 * 开始下载
	 * 
	 * @param url
	 */
	void onStartLoading(String url);

	/**
	 * 下载中
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
