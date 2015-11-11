package com.xuan.bigapple.lib.cache;

import android.content.Context;

import com.xuan.bigapple.lib.cache.core.BPACache;

/**
 * 一个很简单的缓存工具
 * 
 * @author xuan
 */
public class BPACacheManager {
	private static BPACache defaultCache;

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	public static void initDefaultCache(Context context) {
		defaultCache = BPACache.get(context);
	}

	/**
	 * 获取单例
	 * 
	 * @return
	 */
	public static BPACache getDefaultCache() {
		if (null == defaultCache) {
			throw new NullPointerException(
					"Call BPACacheManager.initDefaultCache first!");
		}

		return defaultCache;
	}

}
