package com.xuan.bigapple.lib.bitmap.listeners.impl;

import com.xuan.bigapple.lib.bitmap.listeners.MakeCacheKeyListener;

/**
 * 默认产生cacheKey
 * 
 * @author xuan
 */
public class DefaultMakeCacheKeyListener implements MakeCacheKeyListener {
	@Override
	public String makeCacheKey(String url) {
		return url;
	}

}
