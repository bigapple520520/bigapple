/* 
 * @(#)LocalImageLoaderFace.java    Created on 2014-10-13
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap.local;

import android.content.Context;
import android.widget.ImageView;

import com.winupon.andframe.bigapple.bitmap.BitmapDisplayConfig;

/**
 * LocalImageLoader的单例封装
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-10-13 下午6:58:59 $
 */
public abstract class LocalImageLoaderFace {
    private static LocalImageLoader instance;

    /**
     * 返回单例
     * 
     * @return
     */
    public static LocalImageLoader instance() {
        return instance;
    }

    /**
     * 初始化单例
     * 
     * @param application
     */
    public synchronized static void init(Context application) {
        if (null == instance) {
            instance = new LocalImageLoader(application);
        }
    }

    /**
     * 显示
     * 
     * @param imageView
     * @param filePath
     */
    public static void display(ImageView imageView, String filePath) {
        instance.display(imageView, filePath, null);
    }

    /**
     * 显示
     * 
     * @param imageView
     * @param filePath
     */
    public static void display(ImageView imageView, String filePath, BitmapDisplayConfig config) {
        instance.display(imageView, filePath, config);
    }

    /**
     * 清理缓存
     */
    public static void clearCacheAll() {
        instance.clearCacheAll();
    }

    /**
     * 清理缓存
     */
    public static void clearCache(String key) {
        instance.clearCache(key);
    }

}
