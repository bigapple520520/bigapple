package com.xuan.bigapple.lib.asynctask;

import android.content.Context;

import com.xuan.bigapple.lib.asynctask.callback.AsyncTaskResultNullCallback;
import com.xuan.bigapple.lib.asynctask.helper.Result;
import com.xuan.bigapple.lib.utils.ContextUtils;
import com.xuan.bigapple.lib.utils.ToastUtils;

/**
 * 在请求http前，会先判断网络是否存在
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-5-20 下午3:12:25 $
 */
public abstract class NetAbstractTask<T> extends AbstractTask<T> {
	public NetAbstractTask(final Context context) {
		super(context);
		setAsyncTaskResultNullCallback(new AsyncTaskResultNullCallback() {
			@Override
			public void resultNullCallback() {
				ToastUtils.displayTextShort(context, "无网络连接");
			}
		});
	}

	@Override
	protected Result<T> doHttpRequest(Object... params) {
		if (!ContextUtils.hasNetwork(context)) {
			return null;
		}

		// 正真的网络操作
		return onHttpRequest(params);
	}

	/**
	 * 子类实现，网络请求操作
	 * 
	 * @param params
	 * @return
	 */
	protected abstract Result<T> onHttpRequest(Object... params);

}
