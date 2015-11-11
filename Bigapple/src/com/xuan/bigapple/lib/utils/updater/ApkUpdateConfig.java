package com.xuan.bigapple.lib.utils.updater;

import android.content.DialogInterface;

/**
 * 下载提示框配置
 * 
 * @author xuan
 * 
 */
public class ApkUpdateConfig {
	/**
	 * 以下是PregressDialog参数
	 */
	public String progressTitle;
	public boolean progressCancelable = true;
	private ProgressListener progressListener;
	public boolean canShowProgress = true;

	public ProgressListener getProgressListener() {
		return progressListener;
	}

	public void setProgressListener(ProgressListener progressListener) {
		this.progressListener = progressListener;
	}

	/**
	 * 进度条监听事件
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2015-3-18 上午9:43:00 $
	 */
	public static abstract class ProgressListener {
		/**
		 * 下载中，进度条被取消监听
		 * 
		 * @return 返回true表示事件处理，不进一步框架流程
		 */
		public abstract boolean cancel(DialogInterface dialogInterface);
	}

	/**
	 * 以下是AlertDialog参数
	 */
	public String alertDialogTitle = "提示";
	public String alertDialogMessage = "软件版本更新";
	public String alertDialogOkBtnText = "确定";
	public String alertDialogCancelBtnText = "取消";
	public boolean alertDialogCancelable = true;
	private AlertDialogListener alertDialogListener;
	public boolean canShowAlertDialog = true;

	public AlertDialogListener getAlertDialogListener() {
		return alertDialogListener;
	}

	public void setAlertDialogListener(AlertDialogListener alertDialogListener) {
		this.alertDialogListener = alertDialogListener;
	}

	/**
	 * 提示选择事件
	 * 
	 * @author xuan
	 */
	public static abstract class AlertDialogListener {
		/**
		 * 选择确定
		 * 
		 * @return 返回true表示事件处理，不进一步框架流程
		 */
		public abstract boolean onChoiceOk();

		/**
		 * 选择取消
		 * 
		 * @return 返回true表示事件处理，不进一步框架流程
		 */
		public abstract boolean onChoiceCancel();
	}

	/**
	 * 下载完成后是否自动更新
	 */
	public boolean isAutoInstall = true;

}
