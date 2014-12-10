/* 
 * @(#)ToastUtils.java    Created on 2011-5-31
 * Copyright (c) 2011 ZDSoft Networks, Inc. All rights reserved.
 * $Id: ToastUtils.java 31799 2012-10-25 04:59:34Z xuan $
 */
package com.winupon.andframe.bigapple.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

/**
 * 吐司信息工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-3-25 下午7:40:05 $
 */
public abstract class ToastUtils {
    /**
     * 显示吐司信息（较长时间）
     * 
     * @param context
     *            上下文
     * @param text
     *            要提示的文本
     */
    public static void displayTextLong(final Context context, final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * 显示吐司信息（较短时间），可以在任意的线程中调用
     * 
     * @param context
     *            上下文
     * @param text
     *            要提示的文本
     */
    public static void displayTextShort(final Context context, final String text) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 显示吐司信息交给handler处理（较长时间）<br>
     * 废弃：可以使用displayTextLong方法替代，displayTextLong方法可以在任何线程中调用
     * 
     * @param context
     * @param text
     * @param handler
     */
    @Deprecated
    public static void displayTextLong2Handler(final Context context, final String text, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.displayTextLong(context, text);
            }
        });
    }

    /**
     * 显示吐司信息交给handler处理（较短时间）<br>
     * 废弃：可以使用displayTextShort方法替代，displayTextShort方法可以在任何线程中调用
     * 
     * @param context
     * @param text
     * @param handler
     */
    @Deprecated
    public static void displayTextShort2Handler(final Context context, final String text, Handler handler) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ToastUtils.displayTextShort(context, text);
            }
        });
    }

}
