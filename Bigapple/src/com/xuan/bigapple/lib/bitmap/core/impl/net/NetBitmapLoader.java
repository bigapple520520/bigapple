package com.xuan.bigapple.lib.bitmap.core.impl.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.xuan.bigapple.lib.bitmap.BitmapDisplayConfig;
import com.xuan.bigapple.lib.bitmap.BitmapGlobalConfig;
import com.xuan.bigapple.lib.bitmap.core.impl.IBitmapLoader;
import com.xuan.bigapple.lib.bitmap.listeners.ClearCacheListener;

/**
 * AnBitmapUtils接口门面，使其保持单例，谢谢，提供给使用者，使用更加方便
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午4:59:56 $
 */
public class NetBitmapLoader implements IBitmapLoader {
	private static NetBitmapLoader instance;

	private final NetBitmapLoaderImpl netBitmapLoaderImpl;

	private NetBitmapLoader(NetBitmapLoaderImpl netBitmapLoaderImpl) {
		this.netBitmapLoaderImpl = netBitmapLoaderImpl;
	}

	public static NetBitmapLoader getInstance() {
		if (null == instance) {
			throw new NullPointerException(
					"Instance is null. Call NetBitmapLoader.init(application) first.");
		}

		return instance;
	}

	/**
	 * 初始化，请在程序起来的时候初始化，即Application启动，可以多次调用最终只初始化一次
	 * 
	 * @param context
	 */
	public synchronized static NetBitmapLoader init(Context application) {
		if (null == application) {
			throw new NullPointerException("Application is null.");
		}

		if (null == instance) {
			instance = new NetBitmapLoader(new NetBitmapLoaderImpl(application));
		}
		return instance;
	}

	/**
	 * 显示图片
	 * 
	 * @param imageView
	 * @param uri
	 */
	public void display(ImageView imageView, String uri) {
		netBitmapLoaderImpl.display(imageView, uri);
	}

	/**
	 * 显示图片
	 * 
	 * @param imageView
	 * @param uri
	 * @param displayConfig
	 *            显示自定义参数
	 */
	@Override
	public void display(ImageView imageView, String uri,
			BitmapDisplayConfig displayConfig) {
		netBitmapLoaderImpl.display(imageView, uri, displayConfig);
	}

	/**
	 * 从缓存中获取图片，如果没有就返回null
	 * 
	 * @param uri
	 * @param displayConfig
	 *            图片显示规格，如果使用默认参数就设置成null
	 * @return
	 */
	@Override
	public Bitmap getBitmapFromCache(String uri,
			BitmapDisplayConfig displayConfig) {
		return netBitmapLoaderImpl.getBitmapFromCache(uri, displayConfig);
	}

	/**
	 * 清理指定缓
	 * 
	 * @param uri
	 * @param clearCacheListener
	 *            缓存清理成功后的回调，如果不想处理，设置成null也可
	 */
	@Override
	public void clearCache(String uri, ClearCacheListener clearCacheListener) {
		netBitmapLoaderImpl.clearCache(uri, clearCacheListener);
	}

	/**
	 * 清理所有缓存
	 * 
	 * @param afterClearCacheListener
	 *            缓存清理成功后的回调，如果不想处理，设置成null也可
	 */
	@Override
	public void clearCacheAll(ClearCacheListener afterClearCacheListener) {
		netBitmapLoaderImpl.clearCacheAll(afterClearCacheListener);
	}

	@Override
	public IBitmapLoader setDefaultBitmapGlobalConfig(
			BitmapGlobalConfig globalConfig) {
		return netBitmapLoaderImpl.setDefaultBitmapGlobalConfig(globalConfig);
	}

	@Override
	public BitmapDisplayConfig getDefaultBitmapDisplayConfig() {
		return netBitmapLoaderImpl.getDefaultBitmapDisplayConfig();
	}

	@Override
	public BitmapGlobalConfig getDefaultBitmapGlobalConfig() {
		return netBitmapLoaderImpl.getDefaultBitmapGlobalConfig();
	}

	@Override
	public IBitmapLoader setDefaultBitmapDisplayConfig(
			BitmapDisplayConfig displayConfig) {
		return netBitmapLoaderImpl.setDefaultBitmapDisplayConfig(displayConfig);
	}

	@Override
	public void closeCache(ClearCacheListener callback) {
		netBitmapLoaderImpl.closeCache(callback);
	}

	@Override
	public void pauseTasks() {
		netBitmapLoaderImpl.pauseTasks();
	}

	@Override
	public void resumeTasks() {
		netBitmapLoaderImpl.resumeTasks();
	}

}
