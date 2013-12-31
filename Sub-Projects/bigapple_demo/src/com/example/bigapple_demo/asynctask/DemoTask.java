/* 
 * @(#)DemoTask.java    Created on 2013-11-15
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.example.bigapple_demo.asynctask;

import android.content.Context;

import com.winupon.andframe.bigapple.asynctask.AbstractTask;
import com.winupon.andframe.bigapple.asynctask.helper.Result;

/**
 * 耗时操作类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-11-15 下午12:49:22 $
 */
public class DemoTask extends AbstractTask<String> {
    public DemoTask(Context context) {
        super(context);
    }

    @Override
    protected Result<String> doHttpRequest(Object... args) {
        String name = (String) args[0];

        try {
            Thread.sleep(3000);// 模拟耗时操作
        }
        catch (Exception e) {
        }

        return new Result<String>(true, null, "响应成功" + name);
    }
}
