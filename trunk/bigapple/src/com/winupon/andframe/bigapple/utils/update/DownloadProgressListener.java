/* 
 * @(#)DownloadProgressListener.java    Created on 2014-7-29
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.update;

/**
 * apk下载的进度监听
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-7-29 下午2:26:59 $
 */
public interface DownloadProgressListener {

    /**
     * 下载过程中的进度提示
     * 
     * @param progress
     *            加载进度条，完整是100
     */
    public void onProgress(int progress);

}
