/* 
 * @(#)ObjectCache.java    Created on 2013-9-17
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.cache.impl;

import com.winupon.andframe.bigapple.cache.Cache;
import com.winupon.andframe.bigapple.cache.core.LruCache;

/**
 * 对象缓存
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午8:42:45 $
 */
public class ObjectMemoryCache implements Cache<String, Object> {
    private static LruCache<String, Object> objectMemoryCache;

    public ObjectMemoryCache(int size) {
        objectMemoryCache = new LruCache<String, Object>(size);
    }

    @Override
    public Object put(String key, Object value) {
        return objectMemoryCache.put(key, value);
    }

    @Override
    public Object get(String key) {
        return objectMemoryCache.get(key);
    };

    @Override
    public Object remove(String key) {
        return objectMemoryCache.remove(key);
    };

    @Override
    public void removeAll() {
        objectMemoryCache.evictAll();
    }

    @Override
    public void destroy() {
        objectMemoryCache.evictAll();
        objectMemoryCache = null;
    }

    @Override
    public int maxSize() {
        return objectMemoryCache.maxSize();
    }

    @Override
    public int size() {
        return objectMemoryCache.size();
    }

}
