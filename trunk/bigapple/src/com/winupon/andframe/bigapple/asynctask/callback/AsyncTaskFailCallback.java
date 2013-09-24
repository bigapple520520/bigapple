/* 
 * @(#)AsyncTaskFailCallback.java    Created on 2013-2-21
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.asynctask.callback;

import com.winupon.andframe.bigapple.asynctask.helper.Result;

/**
 * 耗时任务失败回调接口
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-2-21 下午12:28:39 $
 */
public interface AsyncTaskFailCallback<T> {
    /**
     * 执行方法
     * 
     * @param result
     */
    public void failCallback(Result<T> result);
}
