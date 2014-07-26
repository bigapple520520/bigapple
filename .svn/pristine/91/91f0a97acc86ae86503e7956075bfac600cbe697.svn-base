/* 
 * @(#)NetAbstractTask.java    Created on 2014-5-20
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.asynctask;

import android.content.Context;

import com.winupon.andframe.bigapple.asynctask.callback.AsyncTaskResultNullCallback;
import com.winupon.andframe.bigapple.asynctask.helper.Result;
import com.winupon.andframe.bigapple.utils.ContextUtils;
import com.winupon.andframe.bigapple.utils.ToastUtils;

/**
 * 在请求http前，先判断网络是否存在
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-5-20 下午3:12:25 $
 */
public abstract class NetAbstractTask<T> extends AbstractTask<T> {
    public NetAbstractTask(final Context context) {
        super(context);
        setAsyncTaskResultNullCallback(new AsyncTaskResultNullCallback() {
            @Override
            public void resultNullCallback() {
                ToastUtils.displayTextShort(context, "无网络连接");
            }
        });
    }

    public NetAbstractTask(final Context context, boolean isShow) {
        super(context, isShow);
        setAsyncTaskResultNullCallback(new AsyncTaskResultNullCallback() {
            @Override
            public void resultNullCallback() {
                ToastUtils.displayTextShort(context, "无网络连接");
            }
        });
    }

    @Override
    protected Result<T> doHttpRequest(Object... params) {
        if (!ContextUtils.hasNetwork(context)) {
            return null;
        }

        // 正真的网络操作
        return onHttpRequest(params);
    }

    protected abstract Result<T> onHttpRequest(Object... params);

}
