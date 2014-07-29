/* 
 * @(#)CancelEvent.java    Created on 2013-9-12
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.update;

/**
 * UpdateCancelListener取消监听中的取消事件情况
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-12 下午7:10:52 $
 */
public class CancelEvent {
    public static final int USER_CANCEL = 1;// 用户取消
    public static final int SDCARD_DISABLED = 2;// SD卡不可用
    public static final int DOWNLOAD_FAIL = 3;// 下载失败
    public static final int DOWNLOAD_ING_CANCEL = 4;// 下载中用户取消
    public static final int INSTALL_FAIL = 5;// 安装失败

    public CancelEvent(int event) {
        this.event = event;
    }

    private int event;// 取消事件状态

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

}
