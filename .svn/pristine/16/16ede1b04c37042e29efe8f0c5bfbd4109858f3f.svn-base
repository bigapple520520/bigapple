/* 
 * @(#)CacheManager.java    Created on 2013-8-20
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.cache;

import java.util.HashMap;
import java.util.Map.Entry;

import android.annotation.TargetApi;
import android.graphics.Bitmap;

/**
 * 缓存管理类，里面维护里一个缓存池
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-20 上午9:07:41 $
 */
@Deprecated
@TargetApi(12)
public abstract class AnCacheManager {
    private static final int DEFAULT_OBJECT_CACHE_SIZE = 20;// 默认可放20个单位
    private static final int DEFAULT_BITMAP_CACHE_SIZE = 3 * 1024 * 1024;// 默认可放3M

    private static LruCache<String, Object> defalutObjectCache;
    private static LruCache<String, Bitmap> defalutBitmapCache;

    private static final HashMap<String, LruCache<String, Object>> objectCachePool = new HashMap<String, LruCache<String, Object>>();
    private static final HashMap<String, LruCache<String, Bitmap>> bitmapCachePool = new HashMap<String, LruCache<String, Bitmap>>();

    /**
     * 默认CacheObject对象缓存，可放20单位
     * 
     * @return
     */
    public static LruCache<String, Object> getDefalutObjectCache() {
        if (null == defalutObjectCache) {
            defalutObjectCache = new LruCache<String, Object>(DEFAULT_OBJECT_CACHE_SIZE);
        }

        return defalutObjectCache;
    }

    /**
     * 默认BitMap对象缓存，可放3M容量
     * 
     * @return
     */
    public static LruCache<String, Bitmap> getDefalutBitmapCache() {
        if (null == defalutBitmapCache) {
            defalutBitmapCache = new LruCache<String, Bitmap>(DEFAULT_BITMAP_CACHE_SIZE) {
                @Override
                protected int sizeOf(String key, Bitmap value) {
                    return value.getByteCount();
                }
            };
        }

        return defalutBitmapCache;
    }

    /**
     * 自定义初始化CacheObject缓存
     * 
     * @param key
     * @param size
     */
    public static void initObjectCache(String key, int size) {
        LruCache<String, Object> cache = new LruCache<String, Object>(size);
        objectCachePool.put(key, cache);
    }

    /**
     * 获取自定义CacheObject缓存
     * 
     * @param key
     * @return
     */
    public static LruCache<String, Object> getObjectCache(String key) {
        return objectCachePool.get(key);
    }

    /**
     * 自定义初始化Bitmap缓存
     * 
     * @param key
     * @param size
     */
    public static void initBitmapCache(String key, int size) {
        LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(size) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

        bitmapCachePool.put(key, cache);
    }

    /**
     * 获取自定义Bitmap缓存
     * 
     * @param key
     * @return
     */
    public static LruCache<String, Bitmap> getBitmapCache(String key) {
        return bitmapCachePool.get(key);
    }

    /**
     * 清理所有缓存
     */
    public static void clear() {
        if (null != defalutObjectCache) {
            defalutObjectCache.evictAll();
        }

        if (null != defalutBitmapCache) {
            defalutObjectCache.evictAll();
        }

        for (Entry<String, LruCache<String, Object>> entry : objectCachePool.entrySet()) {
            entry.getValue().evictAll();
        }

        for (Entry<String, LruCache<String, Bitmap>> entry : bitmapCachePool.entrySet()) {
            entry.getValue().evictAll();
        }
    }

    /**
     * 销毁缓存，销毁之后，所有的缓存都要初始化了的
     */
    public static void destroy() {
        if (null != defalutObjectCache) {
            defalutObjectCache = null;
        }

        if (null != defalutBitmapCache) {
            defalutObjectCache = null;
        }

        objectCachePool.clear();
        bitmapCachePool.clear();
    }

}
