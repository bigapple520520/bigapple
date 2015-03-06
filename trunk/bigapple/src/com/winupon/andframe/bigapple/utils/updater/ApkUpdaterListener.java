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

    public void downloadProgress(int progress, StateEnum stateEnum);

    /**
     * 下载状态枚举
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2015-3-6 下午5:37:18 $
     */
    public enum StateEnum {
        DOWNLOAD_ING, DOWNLOAD_FINISH, DOWNLOAD_STOP, DOWNLOAD_ERROR;
    }

}
