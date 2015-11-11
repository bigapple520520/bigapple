package com.xuan.bigapple.lib.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.xuan.bigapple.lib.bitmap.core.impl.IBitmapLoader;
import com.xuan.bigapple.lib.bitmap.core.impl.local.LocalBitmapLoader;
import com.xuan.bigapple.lib.bitmap.core.impl.net.NetBitmapLoader;
import com.xuan.bigapple.lib.bitmap.listeners.ClearCacheListener;
import com.xuan.bigapple.lib.utils.Validators;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 图片加载器，门面设计模式
 * 
 * @author xuan
 */
public class BPBitmapLoader implements IBitmapLoader {
	private static BPBitmapLoader instance;

	/**
	 * 初始化单例
	 * 
	 * @param application
	 */
	public static void init(Context application) {
		if (null == application) {
			throw new NullPointerException("Application is null.");
		}

		instance = new BPBitmapLoader();

		NetBitmapLoader.init(application);
		LocalBitmapLoader.init(application);
	}

	public static BPBitmapLoader getInstance() {
		return instance;
	}

	/**
	 * 显示
	 * 
	 * @param imageView
	 * @param filePath
	 */
	@Override
	public void display(ImageView imageView, String url,
			BitmapDisplayConfig config) {
		if (Validators.isEmpty(url)) {
			return;
		}

		if (url.startsWith("http")) {
			// 网络图片
			NetBitmapLoader.getInstance().display(imageView, url, config);
		} else if (Validators.isNumber(url)) {
			// 资源图片
			imageView.setImageResource(Integer.valueOf(url));
		} else {
			// 本地图片
			LocalBitmapLoader.getInstance().display(imageView, url, config);
		}
	}

	/**
	 * 显示
	 * 
	 * @param imageView
	 * @param url
	 */
	public void display(ImageView imageView, String url) {
		display(imageView, url, null);
	}

	/**
	 * 清理所有缓存
	 * 
	 * @param callback
	 */
	@Override
	public void clearCacheAll(ClearCacheListener callback) {
		LocalBitmapLoader.getInstance().clearCacheAll(null);
		NetBitmapLoader.getInstance().clearCacheAll(callback);
	}

	/**
	 * 清理缓存，指定缓存
	 * 
	 * @param key
	 * @param callback
	 */
	@Override
	public void clearCache(String key, ClearCacheListener callback) {
		if (key.startsWith("http")) {
			// 网络图片
			NetBitmapLoader.getInstance().clearCache(key, callback);
		} else {
			// 本地图片
			LocalBitmapLoader.getInstance().clearCache(key, callback);
		}
	}

	@Override
	public IBitmapLoader setDefaultBitmapGlobalConfig(
			BitmapGlobalConfig globalConfig) {
		NetBitmapLoader.getInstance()
				.setDefaultBitmapGlobalConfig(globalConfig);
		LocalBitmapLoader.getInstance().setDefaultBitmapGlobalConfig(
				globalConfig);
		return this;
	}

	@Override
	public BitmapDisplayConfig getDefaultBitmapDisplayConfig() {
		return NetBitmapLoader.getInstance().getDefaultBitmapDisplayConfig();
	}

	@Override
	public BitmapGlobalConfig getDefaultBitmapGlobalConfig() {
		return NetBitmapLoader.getInstance().getDefaultBitmapGlobalConfig();
	}

	@Override
	public IBitmapLoader setDefaultBitmapDisplayConfig(
			BitmapDisplayConfig displayConfig) {
		NetBitmapLoader.getInstance().setDefaultBitmapDisplayConfig(
				displayConfig);
		LocalBitmapLoader.getInstance().setDefaultBitmapDisplayConfig(
				displayConfig);
		return this;
	}

	@Override
	public Bitmap getBitmapFromCache(String uri,
			BitmapDisplayConfig displayConfig) {
		if (uri.startsWith("http")) {
			// 网络图片
			return NetBitmapLoader.getInstance().getBitmapFromCache(uri,
					displayConfig);
		} else {
			// 本地图片
			return LocalBitmapLoader.getInstance().getBitmapFromCache(uri,
					displayConfig);
		}
	}

	@Override
	public void closeCache(ClearCacheListener callback) {
		LocalBitmapLoader.getInstance().closeCache(null);
		NetBitmapLoader.getInstance().closeCache(callback);
	}

	@Override
	public void pauseTasks() {
		LogUtils.e("Not support!!!");
	}

	@Override
	public void resumeTasks() {
		LogUtils.e("Not support!!!");
	}

}
