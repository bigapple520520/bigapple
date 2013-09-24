/* 
 * @(#)LRUPlusCacheFactory.java    Created on 2013-3-14
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.cache;

import java.util.HashMap;

/**
 * LRUPlusCache的工厂类<br>
 * 被CacheFactory所取代
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-3-14 下午6:23:07 $
 */
@Deprecated
public abstract class LRUPlusCacheFactory {
    private static final int DEFAULT_CACHE_SIZE = 20;// 默认的缓存容量

    private static final LRUPlusCache defaultCache = new LRUPlusCache(DEFAULT_CACHE_SIZE);
    private static final HashMap<String, LRUPlusCache> cachePool = new HashMap<String, LRUPlusCache>();

    /**
     * 获取默认容量的缓存
     * 
     * @return
     */
    public static LRUPlusCache getDefaultCache() {
        return defaultCache;
    }

    /**
     * 初始化一个缓存到缓存池中
     * 
     * @param size
     * @param cacheId
     */
    public static void initCache(int size, String cacheId) {
        cachePool.put(cacheId, new LRUPlusCache(size));
    }

    /**
     * 从缓存池中获取一个缓存，如果没有就新建默认大小的
     * 
     * @param cacheId
     * @return
     */
    public static LRUPlusCache getCache(String cacheId) {
        LRUPlusCache cache = cachePool.get(cacheId);

        if (null == cache) {
            cache = new LRUPlusCache(DEFAULT_CACHE_SIZE);
            cachePool.put(cacheId, cache);
        }

        return cache;
    }

    /**
     * 判断缓存是否存在缓存池中
     * 
     * @param cacheId
     * @return
     */
    public static boolean isCacheExits(String cacheId) {
        return cachePool.containsKey(cacheId);
    }

    /**
     * 清空缓存池
     */
    public static void clearCachePool() {
        cachePool.clear();
    }

    /**
     * 从缓存池中删除指定缓存
     * 
     * @param cacheId
     */
    public static void removeCache(String cacheId) {
        cachePool.remove(cacheId);
    }

}
