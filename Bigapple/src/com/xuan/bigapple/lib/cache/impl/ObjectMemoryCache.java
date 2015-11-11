package com.xuan.bigapple.lib.cache.impl;

import com.xuan.bigapple.lib.bitmap.core.cache.LruMemoryCache;
import com.xuan.bigapple.lib.cache.Cache;

/**
 * 对象缓存，使用了bitmap中的缓存，可设置缓存的过期值
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午8:42:45 $
 */
public class ObjectMemoryCache implements Cache<String, Object> {
	private static LruMemoryCache<String, Object> objectMemoryCache;

	public ObjectMemoryCache(int size) {
		objectMemoryCache = new LruMemoryCache<String, Object>(size);
	}

	@Override
	public Object put(String key, Object value) {
		return put(key, value, Long.MAX_VALUE);
	}

	@Override
	public Object put(String key, Object value, long expiryTimestamp) {
		return objectMemoryCache.put(key, value, expiryTimestamp);
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
