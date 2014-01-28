/* 
 * @(#)AnBitmapUtilsDemo.java    Created on 2013-9-5
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.example.bigapple_demo.bitmap;

import android.content.Context;
import android.widget.ImageView;

import com.winupon.andframe.bigapple.bitmap.AfterClearCacheListener;
import com.winupon.andframe.bigapple.bitmap.AnBitmapUtils;
import com.winupon.andframe.bigapple.utils.ToastUtils;

/**
 * 从网络加载图片demo
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-5 下午6:49:46 $
 */
public abstract class AnBitmapUtilsDemo {

    public static AnBitmapUtils defaultAnBitmapUtils;

    public static AnBitmapUtils getDeFaultAnBitmapUtils(Context context) {
        if (null == defaultAnBitmapUtils) {
            defaultAnBitmapUtils = new AnBitmapUtils(context);
        }

        return defaultAnBitmapUtils;
    }

    /**
     * 最简单的默认配置使用
     * 
     * @param context
     * @param imageView
     */
    public static void loadBitmapDefault(Context context, ImageView imageView) {
        getDeFaultAnBitmapUtils(context).display(imageView, "http://img7.9158.com/200709/01/11/53/200709018758949.jpg");
        // getDeFaultAnBitmapUtils(context)
        // .display(
        // imageView,
        // "http://g.hiphotos.baidu.com/album/w%3D2048/sign=8d0488b10eb30f24359aeb03fcadd343/b151f8198618367a2c395c0d2f738bd4b21ce5aa.jpg");
    }

    /**
     * 清理所有缓存，包括内存和磁盘
     */
    public static void clearCache(final Context context) {
        getDeFaultAnBitmapUtils(context).clearCache(new AfterClearCacheListener() {
            @Override
            public void afterClearCache(int arg0) {
                ToastUtils.displayTextShort(context, "我清理了哦");
            }
        });
    }

}
