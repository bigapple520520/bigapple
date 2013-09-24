/* 
 * @(#)AnCacheUtils.java    Created on 2013-9-17
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.cache;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.winupon.andframe.bigapple.cache.impl.BitmapMemoryCache;
import com.winupon.andframe.bigapple.cache.impl.ObjectMemoryCache;

/**
 * 作为一个缓存工厂存在
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午8:46:35 $
 */
public abstract class AnCacheUtils {
    private static final int DEFAULT_OBJECT_CACHE_SIZE = 20;// 默认可放20个单位的缓存
    private static final int DEFAULT_BITMAP_CACHE_SIZE = 1024 * 1024 * 3;// 默认3M
    private static final float DEFAULT_BITMAP_CACHE_PERCENT = 0.3f;// 默认按照系统可以内存的0.3系数，所以会根据不同手机调整

    private static int objectCacheSize = DEFAULT_OBJECT_CACHE_SIZE;
    private static int bitmapCacheSize = DEFAULT_BITMAP_CACHE_SIZE;
    private static float bitmapCachePercent = DEFAULT_BITMAP_CACHE_PERCENT;

    private static Cache<String, Object> objectMemoryCache;
    private static Cache<String, Bitmap> bitmapMemoryCache;

    /**
     * 默认CacheObject对象缓存，可放20单位
     * 
     * @return
     */
    public static Cache<String, Object> getObjectMemoryCache() {
        if (null == objectMemoryCache) {
            objectMemoryCache = new ObjectMemoryCache(objectCacheSize);
        }

        return objectMemoryCache;
    }

    /**
     * 默认BitMap对象缓存，可放3M容量
     * 
     * @return
     */
    public static Cache<String, Bitmap> getBitmapMemoryCache() {
        if (null == bitmapMemoryCache) {
            bitmapMemoryCache = new BitmapMemoryCache(bitmapCacheSize);
        }

        return bitmapMemoryCache;
    }

    /**
     * 默认BitMap对象缓存，更具系统本身可用内存的0.3系数计算
     * 
     * @return
     */
    public static Cache<String, Bitmap> getBitmapMemoryCache(Context context) {
        if (null == bitmapMemoryCache) {
            int maxSize = (int) (bitmapCachePercent * getMemoryClass(context));
            bitmapMemoryCache = new BitmapMemoryCache(maxSize);
        }

        return bitmapMemoryCache;
    }

    /**
     * 重新配置缓存大小时，会销毁原有缓存
     * 
     * @param size
     */
    public static void configObjectCacheSize(int size) {
        objectCacheSize = size;
        closeObjectCache();
    }

    /**
     * 重新配置缓存大小时，会销毁原有缓存
     * 
     * @param size
     */
    public static void configBitmapCacheSize(int size) {
        bitmapCacheSize = size;
        closeBitmapCache();
    }

    /**
     * 重新配置缓存大小时，会销毁原有缓存
     * 
     * @param size
     */
    public static void configBitmapCacheSize(float percent) {
        bitmapCachePercent = percent;
        closeBitmapCache();
    }

    /**
     * 关闭之后，再get的时候会新初始化缓存
     */
    public static void closeObjectCache() {
        if (null != objectMemoryCache) {
            objectMemoryCache.removeAll();
            objectMemoryCache = null;
        }
    }

    /**
     * 关闭之后，再get的时候会新初始化缓存
     */
    public static void closeBitmapCache() {
        if (null != bitmapMemoryCache) {
            bitmapMemoryCache.removeAll();
            bitmapMemoryCache = null;
        }
    }

    /**
     * 同时关掉了对象缓存和bitmap缓存
     */
    public static void closeAll() {
        closeObjectCache();
        closeBitmapCache();
    }

    private static int getMemoryClass(Context context) {
        return ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

}
