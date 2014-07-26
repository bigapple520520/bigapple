/* 
 * @(#)AnBitmapUtilsDemo.java    Created on 2013-9-5
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.zzdemo.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.winupon.andframe.bigapple.bitmap.AfterClearCacheListener;
import com.winupon.andframe.bigapple.bitmap.AnBitmapUtils;
import com.winupon.andframe.bigapple.bitmap.BitmapDisplayConfig;

/**
 * 从网络加载图片demo,封装单例的AnBitmapUtils实例，类似于一个门面模式，应用程序在使用时，可以屏蔽底层代码
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-5 下午6:49:46 $
 */
public abstract class AnBitmapUtilsFace {
	public static AnBitmapUtils defaultAnBitmapUtils;

	public static AnBitmapUtils getDeFaultAnBitmapUtils(Context context) {
		if (null == defaultAnBitmapUtils) {
			defaultAnBitmapUtils = new AnBitmapUtils(context);
		}

		return defaultAnBitmapUtils;
	}

	/**
	 * 显示图片
	 * 
	 * @param context
	 * @param imageView
	 */
	public static void display(Context context, ImageView imageView, String uri) {
		getDeFaultAnBitmapUtils(context).display(imageView,
				"http://img7.9158.com/200709/01/11/53/200709018758949.jpg");
	}

	/**
	 * 可灵活配制一些自定义的显示参数
	 * 
	 * @param context
	 * @param imageView
	 * @param uri
	 * @param loading
	 * @param fail
	 */
	public static void display(Context context, ImageView imageView,
			String uri, Bitmap loading, Bitmap fail) {
		BitmapDisplayConfig bitmapDisplayConfig = new BitmapDisplayConfig(
				context);
		bitmapDisplayConfig.setLoadingBitmap(loading);
		bitmapDisplayConfig.setLoadFailedBitmap(fail);

		getDeFaultAnBitmapUtils(context).display(imageView,
				"http://img7.9158.com/200709/01/11/53/200709018758949.jpg",
				bitmapDisplayConfig);
	}

	// ///////////////////////////////////////////常用清理缓存部分///////////////////////////////////////////////////////////
	/**
	 * 清理所有缓存，包括内存和磁盘
	 * 
	 * @param context
	 */
	public static void clear(final Context context) {
		getDeFaultAnBitmapUtils(context).clearCache();
	}

	/**
	 * 清理所有的缓存，包括内存和磁盘的，可设置自己的回调
	 * 
	 * @param context
	 * @param afterClearCacheListener
	 */
	public static void clear(final Context context,
			AfterClearCacheListener afterClearCacheListener) {
		getDeFaultAnBitmapUtils(context).clearCache(afterClearCacheListener);
	}

	/**
	 * 清理指定的缓存目标，包括内存的和磁盘的
	 * 
	 * @param context
	 * @param cacheKey
	 */
	public static void clearBykey(final Context context, String cacheKey) {
		getDeFaultAnBitmapUtils(context).clearCache(cacheKey, null);
	}

	/**
	 * 清理指定的缓存目标，包括内存的和磁盘的
	 * 
	 * @param context
	 * @param cacheKey
	 * @param afterClearCacheListener
	 */
	public static void clearByKey(final Context context, String cacheKey,
			AfterClearCacheListener afterClearCacheListener) {
		getDeFaultAnBitmapUtils(context).clearCache(cacheKey, null,
				afterClearCacheListener);
	}

}
