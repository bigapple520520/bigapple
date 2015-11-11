package com.xuan.bigapple.lib.asynctask;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.xuan.bigapple.lib.asynctask.callback.AsyncTaskFailCallback;
import com.xuan.bigapple.lib.asynctask.callback.AsyncTaskResultNullCallback;
import com.xuan.bigapple.lib.asynctask.callback.AsyncTaskSuccessCallback;
import com.xuan.bigapple.lib.asynctask.helper.CompatibleAsyncTask;
import com.xuan.bigapple.lib.asynctask.helper.Result;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 耗时任务类的基类，采用了监听器设计模式，模板方法,注意这个任务类的实例只能在UI线程中被创建
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-2-17 下午4:32:49 $
 */
public abstract class AbstractTask<T> extends
		CompatibleAsyncTask<Object, Integer, Result<T>> {
	protected final Context context;
	/** 是否显示正在加载中提示：默认显示 */
	private boolean isShowProgressDialog = true;

	/** 正在加载中提示控件 */
	private Dialog progressDialog;

	/** 默认提示消息 */
	private String progressTitle = "请稍后...";
	/** 加载中是否可以取消：默认可以取消 */
	private boolean isCancel = true;

	/** 成功回调 */
	private AsyncTaskSuccessCallback<T> asyncTaskSuccessCallback;
	/** 失败回调 */
	private AsyncTaskFailCallback<T> asyncTaskFailCallback;
	/** result返回null的回调 */
	private AsyncTaskResultNullCallback asyncTaskResultNullCallback;

	/**
	 * 构造方法
	 * 
	 * @param context
	 */
	public AbstractTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		// 显示加载中对话框
		if (isShowProgressDialog()) {
			if (null == getProgressDialog()) {
				progressDialog = new ProgressDialog(context);
				progressDialog.setTitle(progressTitle);
				progressDialog.setCancelable(isCancel);
			}
			showDialog(getProgressDialog());
		}
	}

	@Override
	protected Result<T> doInBackground(Object... objects) {
		Result<T> result = null;
		try {
			result = doHttpRequest(objects);
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
			result = new Result<T>(false, "DoHttpRequest exception. Cause:"
					+ e.getMessage());
		}

		return result;
	}

	@Override
	protected void onPostExecute(Result<T> result) {
		if (isShowProgressDialog()) {
			// 先隐藏提示框
			dismissDialog(getProgressDialog());
		}

		if (null == result) {
			if (null != asyncTaskResultNullCallback) {
				asyncTaskResultNullCallback.resultNullCallback();
			}
			return;
		}

		if (result.isSuccess()) {
			if (null != asyncTaskSuccessCallback) {
				asyncTaskSuccessCallback.successCallback(result);
			} else {
				if (!TextUtils.isEmpty(result.getMessage())) {
					Toast.makeText(context, result.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		} else {
			if (null != asyncTaskFailCallback) {
				asyncTaskFailCallback.failCallback(result);
			} else {
				if (!TextUtils.isEmpty(result.getMessage())) {
					Toast.makeText(context, result.getMessage(),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/**
	 * 设置成功监听
	 * 
	 * @param asyncTaskSuccessCallback
	 */
	public void setAsyncTaskSuccessCallback(
			AsyncTaskSuccessCallback<T> asyncTaskSuccessCallback) {
		this.asyncTaskSuccessCallback = asyncTaskSuccessCallback;
	}

	/**
	 * 设置失败监听
	 * 
	 * @param asyncTaskFailCallback
	 */
	public void setAsyncTaskFailCallback(
			AsyncTaskFailCallback<T> asyncTaskFailCallback) {
		this.asyncTaskFailCallback = asyncTaskFailCallback;
	}

	/**
	 * 设置result返回null的回调
	 * 
	 * @param asyncTaskResultNullCallback
	 */
	public void setAsyncTaskResultNullCallback(
			AsyncTaskResultNullCallback asyncTaskResultNullCallback) {
		this.asyncTaskResultNullCallback = asyncTaskResultNullCallback;
	}

	public boolean isShowProgressDialog() {
		return isShowProgressDialog;
	}

	public void setShowProgressDialog(boolean isShowProgressDialog) {
		this.isShowProgressDialog = isShowProgressDialog;
	}

	public Dialog getProgressDialog() {
		return progressDialog;
	}

	public void setProgressDialog(ProgressDialog progressDialog) {
		this.progressDialog = progressDialog;
	}

	public boolean isCancel() {
		return isCancel;
	}

	public void setCancel(boolean isCancel) {
		this.isCancel = isCancel;
	}

	public String getProgressTitle() {
		return progressTitle;
	}

	public void setProgressTitle(String progressTitle) {
		this.progressTitle = progressTitle;
	}

	/**
	 * http请求现在这里，需要子类自己实现
	 * 
	 * @param params
	 * @return
	 */
	protected abstract Result<T> doHttpRequest(Object... objects);

	private void showDialog(Dialog dialog) {
		try {
			dialog.show();
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
		}
	}

	private void dismissDialog(Dialog dialog) {
		try {
			dialog.dismiss();
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
		}
	}

}
