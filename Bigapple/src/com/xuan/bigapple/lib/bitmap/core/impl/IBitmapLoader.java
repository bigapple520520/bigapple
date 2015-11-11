package com.xuan.bigapple.lib.bitmap.core.impl;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.xuan.bigapple.lib.bitmap.BitmapDisplayConfig;
import com.xuan.bigapple.lib.bitmap.BitmapGlobalConfig;
import com.xuan.bigapple.lib.bitmap.listeners.ClearCacheListener;

/**
 * 图片加载通用接口
 * 
 * @author xuan
 */
public interface IBitmapLoader {

	/**
	 * 设置默认全局配置
	 * 
	 * @param globalConfig
	 */
	IBitmapLoader setDefaultBitmapGlobalConfig(BitmapGlobalConfig globalConfig);

	/**
	 * 获取默认显示配置
	 * 
	 * @return
	 */
	BitmapDisplayConfig getDefaultBitmapDisplayConfig();

	/**
	 * 获取默认全局配置
	 * 
	 * @return
	 */
	BitmapGlobalConfig getDefaultBitmapGlobalConfig();

	/**
	 * 设置默认显示配置
	 * 
	 * @param displayConfig
	 */
	IBitmapLoader setDefaultBitmapDisplayConfig(
			BitmapDisplayConfig displayConfig);

	/**
	 * 显示图片
	 * 
	 * @param imageView
	 *            显示图片控件
	 * @param uri
	 *            图片地址
	 * @param displayConfig
	 *            图片显示规格
	 */
	void display(ImageView imageView, String uri,
			BitmapDisplayConfig displayConfig);

	/**
	 * 从缓存中获取图片
	 * 
	 * @param uri
	 * @param config
	 * @return
	 */
	Bitmap getBitmapFromCache(String uri, BitmapDisplayConfig config);

	/**
	 * 清理所有缓存
	 * 
	 * @param callback
	 *            清理后回调
	 */
	void clearCacheAll(ClearCacheListener callback);

	/**
	 * 清理指定uri的缓存
	 * 
	 * @param uri
	 *            图片地址
	 * @param callback
	 *            清理后回调
	 */
	void clearCache(String uri, ClearCacheListener callback);

	/**
	 * 关闭缓存
	 * 
	 * @param callback
	 */
	void closeCache(ClearCacheListener callback);

	/**
	 * 暂停加载
	 * 
	 * @throws Exception
	 */
	void pauseTasks();

	/**
	 * 重新加载
	 */
	void resumeTasks();

}
