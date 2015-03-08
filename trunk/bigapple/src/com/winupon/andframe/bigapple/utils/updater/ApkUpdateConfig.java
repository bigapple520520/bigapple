package com.winupon.andframe.bigapple.utils.updater;

import android.app.AlertDialog;
import android.app.ProgressDialog;

/**
 * 下载提示框配置
 * 
 * @author xuan
 *
 */
public class ApkUpdateConfig {
	/**
	 * 自定义弹出框
	 */
	public AlertDialog cusTomAlertDialog;
	
	/**
	 * 自定义进度条
	 */
	public ProgressDialog cusTomProgressDialog;
	
	/**
	 * 以下是使用默认选择框的文字提示，如果progressDialog不是空，那么以下文字无效
	 */
	public String title = "提示";
	public String message = "软件版本更新";
	public String okBtnText = "确定";
	public String cancelBtnText = "取消";
	public boolean cancelable = true;
	
	/**
	 * 选择事件
	 */
	public OnChoiceListener onChoiceListener;
	
	/**
	 * 提示选择事件
	 * 
	 * @author xuan
	 */
	public interface OnChoiceListener{
		/**
		 * 选择确定
		 * 
		 * @return true：事件已处理，无须框架进行下一步操作，false：事件未处理，按正常框架流程往下走
		 */
		public boolean onChoiceOk();
		
		/**
		 * 选择取消
		 * 
		 * @return
		 */
		public boolean onChoiceCancel();
	}
	
}
