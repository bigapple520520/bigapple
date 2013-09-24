/* 
 * @(#)UpdateCancelListener.java    Created on 2013-9-12
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.update;

/**
 * 用户点击了取消更新，或者当前是最新版本了，通知改事件
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-12 下午6:07:28 $
 */
public interface UpdateCancelListener {

    /**
     * 取消更新操作后回调
     * 
     * @param isUserCancel
     *            枚举了何种方式取消更新
     */
    public void updateCancel(CancelEvent cancelEvent);

}
