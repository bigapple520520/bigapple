package com.xuan.bigapple.lib.asynctask.callback;

import com.xuan.bigapple.lib.asynctask.helper.Result;

/**
 * 耗时任务成功回调接口
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-2-17 下午4:28:41 $
 */
public interface AsyncTaskSuccessCallback<T> {
	/**
	 * 执行方法
	 * 
	 * @param result
	 *            处理结果
	 */
	public void successCallback(Result<T> result);
}
