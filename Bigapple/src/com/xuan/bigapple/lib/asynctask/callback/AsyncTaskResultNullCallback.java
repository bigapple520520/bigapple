package com.xuan.bigapple.lib.asynctask.callback;

/**
 * 当继承的doHttpRequest返回null结果回调的接口，一般在网络判断时候可以用到，当判断网络不存在的时候，就直接返回null，
 * 这个事件就是捕捉这个效果
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-1-13 上午11:02:05 $
 */
public interface AsyncTaskResultNullCallback {
	/**
	 * 回调
	 */
	public void resultNullCallback();
}
