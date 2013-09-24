package com.winupon.andframe.bigapple.http.handler;

/**
 * 结果处理回调
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:11:00 $
 */
public interface EntityCallBack {

    /**
     * 处理结果时回调
     * 
     * @param count
     *            总量字节
     * @param current
     *            当前处理字节
     * @param mustNoticeUI
     *            是否处理OK通知UI
     */
    public void callBack(long count, long current, boolean mustNoticeUI);

}
