/* 
 * @(#)AnBitmapUtilsFactory.java    Created on 2013-9-17
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

/**
 * AnBitmapUtils接口门面，使其保持单例，谢谢，提供给使用者，使用更加方便
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午4:59:56 $
 */
public abstract class AnBitmapUtilsFace {
    private static AnBitmapUtils instance;

    /**
     * 初始化，请在程序起来的时候初始化，即Application启动
     * 
     * @param application
     */
    public static void init(Context application) {
        if (null == instance) {
            // 默认不显示原图
            instance = new AnBitmapUtils(application);
        }
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static AnBitmapUtils getInstance() {
        if (null == instance) {
            throw new RuntimeException("请先初始化AnBitmapUtils实例，方法：在程序启动的时候调用AnBitmapUtilsFace.init方法！");
        }

        return instance;
    }

    /**
     * 显示图片
     * 
     * @param imageView
     * @param uri
     */
    public static void display(ImageView imageView, String uri) {
        getInstance().display(imageView, uri);
    }

    /**
     * 显示图片
     * 
     * @param imageView
     * @param uri
     * @param displayConfig
     */
    public static void display(ImageView imageView, String uri, BitmapDisplayConfig displayConfig) {
        getInstance().display(imageView, uri, displayConfig);
    }

    /**
     * 从缓存中获取图片，如果没有就返回null
     * 
     * @param uri
     * @param displayConfig
     *            图片显示规格，如果使用默认参数就设置成null
     * @return
     */
    public static Bitmap getBitmapFromCache(String uri, BitmapDisplayConfig displayConfig) {
        return getInstance().getBitmapFromCache(uri, displayConfig);
    }

    /**
     * 清理指定缓
     * 
     * @param uri
     * @param afterClearCacheListener
     *            缓存清理成功后的回调，如果不想处理，设置成null也可
     */
    public static void clearCache(String uri, AfterClearCacheListener afterClearCacheListener) {
        getInstance().clearCache(uri, afterClearCacheListener);
    }

    /**
     * 清理所有缓存
     * 
     * @param afterClearCacheListener
     *            缓存清理成功后的回调，如果不想处理，设置成null也可
     */
    public static void clearCache(AfterClearCacheListener afterClearCacheListener) {
        getInstance().clearCache(afterClearCacheListener);
    }

}
