package com.xuan.bigapple.lib.utils.updater;

/**
 * 文件下载监听
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2015-3-6 下午5:15:19 $
 */
public interface DownloadListener {
	/**
	 * 开始下载
	 */
	public void downloadStart();

	/**
	 * 下载过程进度
	 * 
	 * @param progress
	 */
	public void downloadProgress(int progress);

	/**
	 * 下载过程中发生错误调
	 * 
	 * @param e
	 * @param message
	 */
	public void downloadError(Throwable e, String message);

	/**
	 * 下载被终止回调，一般由程序员发起停止下载，即被调用了DownloadHelper.stopDownload方法
	 * 
	 * @param saveFilename
	 */
	public void downloadStop(String saveFilename);

	/**
	 * 下载完成回调
	 * 
	 * @param saveFilename
	 */
	public void downloadFinish(String saveFilename);

}
