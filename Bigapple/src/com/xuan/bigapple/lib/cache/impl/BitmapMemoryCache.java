package com.xuan.bigapple.lib.cache.impl;

import android.graphics.Bitmap;

import com.xuan.bigapple.lib.bitmap.core.cache.LruMemoryCache;
import com.xuan.bigapple.lib.cache.Cache;

/**
 * 安卓 Bitmap图片缓存
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午8:45:33 $
 */
public class BitmapMemoryCache implements Cache<String, Bitmap> {
	private static LruMemoryCache<String, Bitmap> bitmapMemoryCache;

	public BitmapMemoryCache(int size) {
		bitmapMemoryCache = new LruMemoryCache<String, Bitmap>(size) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				if (null == bitmap) {
					return 0;
				}

				return bitmap.getRowBytes() * bitmap.getHeight();
			}
		};
	}

	@Override
	public Bitmap put(String key, Bitmap bitmap) {
		return put(key, bitmap, Long.MAX_VALUE);
	}

	@Override
	public Bitmap put(String key, Bitmap bitmap, long expiryTimestamp) {
		return bitmapMemoryCache.put(key, bitmap, expiryTimestamp);
	}

	@Override
	public Bitmap get(String key) {
		return bitmapMemoryCache.get(key);
	}

	@Override
	public Bitmap remove(String key) {
		return bitmapMemoryCache.remove(key);
	}

	@Override
	public void removeAll() {
		bitmapMemoryCache.evictAll();
	}

	@Override
	public void destroy() {
		bitmapMemoryCache.evictAll();
		bitmapMemoryCache = null;
	}

	@Override
	public int maxSize() {
		return bitmapMemoryCache.maxSize();
	}

	@Override
	public int size() {
		return bitmapMemoryCache.size();
	}

}
