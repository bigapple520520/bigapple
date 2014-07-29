/* 
 * @(#)DownloadFinishListener.java    Created on 2014-7-29
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.update;

/**
 * apk下载完成后的事件回调
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-7-29 下午2:25:29 $
 */
public interface DownloadFinishListener {

    /**
     * 下载完成回调
     * 
     * @param filePath
     *            apk的保存地址
     */
    public void downloadFinish(String filePath);

}
