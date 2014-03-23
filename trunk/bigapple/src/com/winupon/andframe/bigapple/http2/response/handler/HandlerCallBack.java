package com.winupon.andframe.bigapple.http2.response.handler;

/**
 * 处理实体的回调，可监听到处理数据的过程
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:11:00 $
 */
public interface HandlerCallBack {

	/**
	 * 处理结果时回调
	 * 
	 * @param count
	 *            总量字节
	 * @param current
	 *            当前处理字节
	 * @param isFinish
	 *            读取字节是否完成
	 */
	public void callBack(long count, long current, boolean isFinish);

}
