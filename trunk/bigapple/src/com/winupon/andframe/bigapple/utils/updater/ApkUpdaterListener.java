/* 
 * @(#)ApkUpdaterListener.java    Created on 2015-3-6
 * Copyright (c) 2015 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.updater;

/**
 * 下载更新APK监听
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2015-3-6 下午5:15:19 $
 */
public interface ApkUpdaterListener {

	/**
	 * 下载过程进度
	 * 
	 * @param progress
	 */
    public void downloadProgress(int progress);
    
    /**
     * 发送错误时的回调
     * 
     * @param e
     * @param message
     */
    public void downloadError(Throwable e, String message);
    
    /**
     * 下载被终止
     * 
     * @param saveFilename
     */
    public void downloadStop(String saveFilename);
    
    /**
     * 下载完成
     * 
     * @param saveFilename
     */
    public void downloadFinish(String saveFilename);
    
}
