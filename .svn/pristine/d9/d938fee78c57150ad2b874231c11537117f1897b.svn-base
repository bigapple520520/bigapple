/* 
 * @(#)BitmapCacheManager.java    Created on 2013-12-31
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap.core;

import android.os.AsyncTask;

import com.winupon.andframe.bigapple.bitmap.AfterClearCacheListener;
import com.winupon.andframe.bigapple.bitmap.BitmapDisplayConfig;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * BitmapCache管理
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-12-31 上午9:21:47 $
 */
public class BitmapCacheManager {
	private final BitmapCache bitmapCache;

	private BitmapCacheManager(BitmapCache bitmapCache) {
		this.bitmapCache = bitmapCache;
	}

	public static BitmapCacheManager getInstance(BitmapCache bitmapCache) {
		if (null == bitmapCache) {
			throw new NullPointerException("bitmapCache不能为空！");
		}

		return new BitmapCacheManager(bitmapCache);
	}

	private class BitmapCacheManagementTask extends
			AsyncTask<Object, Void, Integer> {
		private AfterClearCacheListener afterClearCacheListener;

		public static final int MESSAGE_INIT_MEMORY_CACHE = 0;
		public static final int MESSAGE_INIT_DISK_CACHE = 1;
		public static final int MESSAGE_FLUSH = 2;
		public static final int MESSAGE_CLOSE = 3;
		public static final int MESSAGE_CLEAR = 4;
		public static final int MESSAGE_CLEAR_MEMORY = 5;
		public static final int MESSAGE_CLEAR_DISK = 6;
		public static final int MESSAGE_CLEAR_BY_KEY = 7;
		public static final int MESSAGE_CLEAR_MEMORY_BY_KEY = 8;
		public static final int MESSAGE_CLEAR_DISK_BY_KEY = 9;

		@Override
		protected Integer doInBackground(Object... params) {
			Integer type = (Integer) params[0];
			if (null != params[3]) {
				afterClearCacheListener = (AfterClearCacheListener) params[3];
			}

			try {
				switch (type) {
				case MESSAGE_INIT_MEMORY_CACHE:
					bitmapCache.initMemoryCache();
					break;
				case MESSAGE_INIT_DISK_CACHE:
					bitmapCache.initDiskCache();
					break;
				case MESSAGE_FLUSH:
					bitmapCache.clearMemoryCache();
					bitmapCache.flush();
					break;
				case MESSAGE_CLOSE:
					bitmapCache.clearMemoryCache();
					bitmapCache.close();
				case MESSAGE_CLEAR:
					bitmapCache.clearCache();
					break;
				case MESSAGE_CLEAR_MEMORY:
					bitmapCache.clearMemoryCache();
					break;
				case MESSAGE_CLEAR_DISK:
					bitmapCache.clearDiskCache();
					break;
				case MESSAGE_CLEAR_BY_KEY:
					bitmapCache.clearCache(String.valueOf(params[1]),
							(BitmapDisplayConfig) params[2]);
					break;
				case MESSAGE_CLEAR_MEMORY_BY_KEY:
					bitmapCache.clearMemoryCache(String.valueOf(params[1]),
							(BitmapDisplayConfig) params[2]);
					break;
				case MESSAGE_CLEAR_DISK_BY_KEY:
					bitmapCache.clearDiskCache(String.valueOf(params[1]));
					break;
				}
			} catch (Exception e) {
				LogUtils.e("", e);
			}

			return type;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);

			if (null != afterClearCacheListener) {
				afterClearCacheListener.afterClearCache(result);
			}
		}
	}

	public void initMemoryCache(AfterClearCacheListener listener) {
		new BitmapCacheManagementTask().execute(
				BitmapCacheManagementTask.MESSAGE_INIT_MEMORY_CACHE, null,
				null, listener);
	}

	public void initDiskCache(AfterClearCacheListener listener) {
		new BitmapCacheManagementTask().execute(
				BitmapCacheManagementTask.MESSAGE_INIT_DISK_CACHE, null, null,
				listener);
	}

	public void clearCache(AfterClearCacheListener listener) {
		new BitmapCacheManagementTask().execute(
				BitmapCacheManagementTask.MESSAGE_CLEAR, null, null, listener);
	}

	public void clearMemoryCache(AfterClearCacheListener listener) {
		new BitmapCacheManagementTask().execute(
				BitmapCacheManagementTask.MESSAGE_CLEAR_MEMORY, null, null,
				listener);
	}

	public void clearDiskCache(AfterClearCacheListener listener) {
		new BitmapCacheManagementTask().execute(
				BitmapCacheManagementTask.MESSAGE_CLEAR_DISK, null, null,
				listener);
	}

	public void clearCache(String uri, BitmapDisplayConfig config,
			AfterClearCacheListener listener) {
		new BitmapCacheManagementTask().execute(
				BitmapCacheManagementTask.MESSAGE_CLEAR_BY_KEY, uri, config,
				listener);
	}

	public void clearMemoryCache(String uri, BitmapDisplayConfig config,
			AfterClearCacheListener listener) {
		new BitmapCacheManagementTask().execute(
				BitmapCacheManagementTask.MESSAGE_CLEAR_MEMORY_BY_KEY, uri,
				config, listener);
	}

	public void clearDiskCache(String uri, AfterClearCacheListener listener) {
		new BitmapCacheManagementTask().execute(
				BitmapCacheManagementTask.MESSAGE_CLEAR_DISK_BY_KEY, uri, null,
				listener);
	}

	public void flushCache(AfterClearCacheListener listener) {
		new BitmapCacheManagementTask().execute(
				BitmapCacheManagementTask.MESSAGE_FLUSH, null, null, listener);
	}

	public void closeCache(AfterClearCacheListener listener) {
		new BitmapCacheManagementTask().execute(
				BitmapCacheManagementTask.MESSAGE_CLOSE, null, null, listener);
	}

}
