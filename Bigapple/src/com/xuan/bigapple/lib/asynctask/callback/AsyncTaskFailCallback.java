package com.xuan.bigapple.lib.asynctask.callback;

import com.xuan.bigapple.lib.asynctask.helper.Result;

/**
 * 耗时任务失败回调接口
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-2-21 下午12:28:39 $
 */
public interface AsyncTaskFailCallback<T> {
	/**
	 * 执行方法
	 * 
	 * @param result
	 *            处理结果
	 */
	public void failCallback(Result<T> result);
}
