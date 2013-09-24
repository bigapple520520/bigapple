/* 
 * @(#)UpdateOkListener.java    Created on 2013-9-12
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.update;

/**
 * 在更新提示中，用户点击OK进行更新前的事件监听，可以在这里做一些更新前的收尾工作
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-12 下午5:59:00 $
 */
public interface UpdateOkListener {

    /**
     * 返回true去更新，返回false就不会去更新
     * 
     * @return
     */
    public boolean updateOk();
}
