package com.xuan.bigapple.lib.bitmap.core.impl.local;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.xuan.bigapple.lib.bitmap.BitmapDisplayConfig;
import com.xuan.bigapple.lib.bitmap.BitmapGlobalConfig;
import com.xuan.bigapple.lib.bitmap.core.impl.IBitmapLoader;
import com.xuan.bigapple.lib.bitmap.listeners.ClearCacheListener;

/**
 * LocalImageLoader的单例封装
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-10-13 下午6:58:59 $
 */
public class LocalBitmapLoader implements IBitmapLoader {
	/** 单例子 */
	private static LocalBitmapLoader instance;

	private final LocalBitmapLoaderImpl localBitmapLoaderImpl;

	private LocalBitmapLoader(LocalBitmapLoaderImpl localBitmapLoaderImpl) {
		this.localBitmapLoaderImpl = localBitmapLoaderImpl;
	}

	/**
	 * 返回单例
	 * 
	 * @return
	 */
	public static LocalBitmapLoader getInstance() {
		if (null == instance) {
			throw new NullPointerException(
					"Instance is null. Call LocalBitmapLoader.init(application) first.");
		}

		return instance;
	}

	/**
	 * 初始化单例
	 * 
	 * @param application
	 */
	public synchronized static LocalBitmapLoader init(Context application) {
		if (null == application) {
			throw new NullPointerException("Application is null.");
		}

		if (null == instance) {
			instance = new LocalBitmapLoader(new LocalBitmapLoaderImpl(
					application));
		}

		return instance;
	}

	/**
	 * 显示
	 * 
	 * @param imageView
	 * @param filePath
	 */
	public void display(ImageView imageView, String filePath) {
		localBitmapLoaderImpl.display(imageView, filePath, null);
	}

	/**
	 * 显示
	 * 
	 * @param imageView
	 * @param filePath
	 */
	@Override
	public void display(ImageView imageView, String filePath,
			BitmapDisplayConfig config) {
		localBitmapLoaderImpl.display(imageView, filePath, config);
	}

	/**
	 * 清理所有缓存
	 * 
	 * @param callback
	 */
	@Override
	public void clearCacheAll(ClearCacheListener callback) {
		localBitmapLoaderImpl.clearCacheAll(callback);
	}

	/**
	 * 清理缓存，指定缓存
	 * 
	 * @param key
	 * @param callback
	 */
	@Override
	public void clearCache(String key, ClearCacheListener callback) {
		localBitmapLoaderImpl.clearCache(key, callback);
	}

	@Override
	public IBitmapLoader setDefaultBitmapGlobalConfig(
			BitmapGlobalConfig globalConfig) {
		return localBitmapLoaderImpl.setDefaultBitmapGlobalConfig(globalConfig);
	}

	@Override
	public BitmapDisplayConfig getDefaultBitmapDisplayConfig() {
		return localBitmapLoaderImpl.getDefaultBitmapDisplayConfig();
	}

	@Override
	public BitmapGlobalConfig getDefaultBitmapGlobalConfig() {
		return localBitmapLoaderImpl.getDefaultBitmapGlobalConfig();
	}

	@Override
	public IBitmapLoader setDefaultBitmapDisplayConfig(
			BitmapDisplayConfig displayConfig) {
		return localBitmapLoaderImpl
				.setDefaultBitmapDisplayConfig(displayConfig);
	}

	@Override
	public Bitmap getBitmapFromCache(String uri, BitmapDisplayConfig config) {
		return localBitmapLoaderImpl.getBitmapFromCache(uri, config);
	}

	@Override
	public void closeCache(ClearCacheListener callback) {
		localBitmapLoaderImpl.closeCache(callback);
	}

	@Override
	public void pauseTasks() {
		localBitmapLoaderImpl.pauseTasks();
	}

	@Override
	public void resumeTasks() {
		localBitmapLoaderImpl.resumeTasks();
	}

}
