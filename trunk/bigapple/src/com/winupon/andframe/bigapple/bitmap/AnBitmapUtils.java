package com.winupon.andframe.bigapple.bitmap;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.winupon.andframe.bigapple.bitmap.core.BitmapCacheManager;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 网络图片加载工具类<br>
 * 注意： <br>
 * 1、使用者在使用时请保持单例，这样内存缓存设置的最大阀值才能被限制住<br>
 * 2、创建AnBitmapUtils实例所用的Context请使用Application对象，不要用Activity对象，防止Activity内存泄露<br>
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-9 下午4:48:21 $
 */
public class AnBitmapUtils {
	private boolean pauseTask = false;
	private final Object pauseTaskLock = new Object();

	private final Context application;
	private BitmapGlobalConfig globalConfig;
	private BitmapDisplayConfig defaultDisplayConfig;
	private final BitmapCacheManager bitmapCacheManager;

	// ///////////////////////////////
	// 创建实例，用户使用时可自行保持单例/////////////////////////////////////////////
	public AnBitmapUtils(Context application) {
		this.application = application;
		globalConfig = new BitmapGlobalConfig(application);
		defaultDisplayConfig = new BitmapDisplayConfig();
		bitmapCacheManager = BitmapCacheManager.getInstance(globalConfig
				.getBitmapCache());
	}

	// ///////////////////////////////默认BitmapDisplayConfig参数配置，即默认显示方式/////////////////////////////////////////////////
	public BitmapDisplayConfig getDefaultDisplayConfig() {
		return defaultDisplayConfig;
	}

	public void setDefaultDisplayConfig(BitmapDisplayConfig defaultDisplayConfig) {
		this.defaultDisplayConfig = defaultDisplayConfig;
	}

	// ////////////////////////////////////////globalConfig参数配置，即默认参数配制/////////////////////////////////////////////////////
	public BitmapGlobalConfig getGlobalConfig() {
		return globalConfig;
	}

	public void setGlobalConfig(BitmapGlobalConfig globalConfig) {
		this.globalConfig = globalConfig;
	}

	// /////////////////////////////// 加载展示图片
	// ////////////////////////////////////////////////////////////////////
	public void display(ImageView imageView, String uri) {
		display(imageView, uri, null);
	}

	public void display(ImageView imageView, String uri,
			BitmapDisplayConfig displayConfig) {
		if (null == imageView) {
			LogUtils.d("图片加载不处理，原因：图片显示控件imageView为空");
			return;
		}

		if (null == displayConfig) {
			displayConfig = defaultDisplayConfig;
		}

		if (TextUtils.isEmpty(uri)) {
			displayConfig.getImageLoadCallBack().loadFailed(imageView,
					displayConfig.getLoadFailedBitmap());
			return;
		}

		// 内存缓存中取
		Bitmap bitmap = globalConfig.getBitmapCache().getBitmapFromMemCache(
				uri, displayConfig);

		if (null != bitmap) {
			// 内存缓存命中
			displayConfig.getImageLoadCallBack().loadCompleted(imageView,
					bitmap, displayConfig);
		} else if (!bitmapLoadTaskExist(imageView, uri)) {
			// 启动任务类：从网络下载或者从磁盘中获取
			final BitmapLoadTask loadTask = new BitmapLoadTask(imageView,
					displayConfig);

			final AsyncBitmapDrawable asyncBitmapDrawable = new AsyncBitmapDrawable(
					application.getResources(),
					displayConfig.getLoadingBitmap(), loadTask);
			imageView.setImageDrawable(asyncBitmapDrawable);// 设置下载任务资源

			loadTask.executeOnExecutor(globalConfig.getBitmapLoadExecutor(),
					uri);
		}
	}

	// ////////////////////////////////////////缓存清理/////////////////////////////////////////////////////////////////
	public void clearCache(AfterClearCacheListener listener) {
		bitmapCacheManager.clearCache(listener);
	}

	public void clearCache() {
		clearCache(null);
	}

	public void clearMemoryCache(AfterClearCacheListener listener) {
		bitmapCacheManager.clearMemoryCache(listener);
	}

	public void clearMemoryCache() {
		clearMemoryCache(null);
	}

	public void clearDiskCache(AfterClearCacheListener listener) {
		bitmapCacheManager.clearDiskCache(listener);
	}

	public void clearDiskCache() {
		bitmapCacheManager.clearDiskCache(null);
	}

	public void clearCache(String uri, BitmapDisplayConfig displayConfig,
			AfterClearCacheListener listener) {
		if (null == displayConfig) {
			displayConfig = defaultDisplayConfig;
		}

		bitmapCacheManager.clearCache(uri, displayConfig, listener);
	}

	public void clearCache(String uri, BitmapDisplayConfig displayConfig) {
		clearCache(uri, displayConfig, null);
	}

	public void clearMemoryCache(String uri, BitmapDisplayConfig displayConfig,
			AfterClearCacheListener listener) {
		if (null == displayConfig) {
			displayConfig = defaultDisplayConfig;
		}

		bitmapCacheManager.clearMemoryCache(uri, displayConfig, listener);
	}

	public void clearMemoryCache(String uri, BitmapDisplayConfig displayConfig) {
		clearMemoryCache(uri, displayConfig, null);
	}

	public void clearDiskCache(String uri, AfterClearCacheListener listener) {
		bitmapCacheManager.clearDiskCache(uri, listener);
	}

	public void clearDiskCache(String uri) {
		clearDiskCache(uri, null);
	}

	public void flushCache(AfterClearCacheListener listener) {
		bitmapCacheManager.flushCache(listener);
	}

	public void flushCache() {
		flushCache(null);
	}

	public void closeCache(AfterClearCacheListener listener) {
		bitmapCacheManager.closeCache(listener);
	}

	public void closeCache() {
		closeCache(null);
	}

	/**
	 * 从缓存中获取图片，如果没有，返回null
	 * 
	 * @param uri
	 * @return
	 */
	public Bitmap getBitmapFromCache(String uri,
			BitmapDisplayConfig displayConfig) {
		if (null == displayConfig) {
			displayConfig = defaultDisplayConfig;
		}

		Bitmap bitmap = globalConfig.getBitmapCache().getBitmapFromMemCache(
				uri, displayConfig);
		if (null == bitmap) {
			bitmap = globalConfig.getBitmapCache().getBitmapFromDiskCache(uri,
					displayConfig);
		}

		return bitmap;
	}

	// //////////////////////////////////任务暂定开始操作///////////////////////////////////////////////////////////////
	public void resumeTasks() {
		pauseTask = false;
	}

	public void pauseTasks() {
		pauseTask = true;
		flushCache(null);
	}

	/**
	 * 一般退出程序时可以调用，用来释放，所有被暂定的任务
	 */
	public void stopTasks() {
		pauseTask = true;
		synchronized (pauseTaskLock) {
			pauseTaskLock.notifyAll();
		}
	}

	// ///////////////////////////////判断获取ImageView的下载任务///////////////////////////////////////////////////////
	private static BitmapLoadTask getBitmapTaskFromImageView(ImageView imageView) {
		if (imageView != null) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncBitmapDrawable) {
				final AsyncBitmapDrawable asyncBitmapDrawable = (AsyncBitmapDrawable) drawable;
				return asyncBitmapDrawable.getBitmapWorkerTask();
			}
		}
		return null;
	}

	private static boolean bitmapLoadTaskExist(ImageView imageView, String uri) {
		final BitmapLoadTask oldLoadTask = getBitmapTaskFromImageView(imageView);

		if (oldLoadTask != null) {
			final String oldUri = oldLoadTask.uri;
			if (TextUtils.isEmpty(oldUri) || !oldUri.equals(uri)) {
				oldLoadTask.cancel(true);
			} else {
				// 同一个线程已经在执行
				return true;
			}
		}
		return false;
	}

	// ///////////////////////////////////内部类定义////////////////////////////////////////////////////////////////////
	/**
	 * 包含加载任务的图片资源
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午2:44:24 $
	 */
	private class AsyncBitmapDrawable extends BitmapDrawable {
		private final WeakReference<BitmapLoadTask> bitmapLoadTaskReference;

		public AsyncBitmapDrawable(Resources res, Bitmap bitmap,
				BitmapLoadTask bitmapWorkerTask) {
			super(res, bitmap);
			bitmapLoadTaskReference = new WeakReference<BitmapLoadTask>(
					bitmapWorkerTask);
		}

		public BitmapLoadTask getBitmapWorkerTask() {
			return bitmapLoadTaskReference.get();
		}
	}

	/**
	 * 图片加载任务
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午2:45:22 $
	 */
	private class BitmapLoadTask extends
			CompatibleAsyncTask<Object, Void, Bitmap> {
		private String uri;
		private final WeakReference<ImageView> targetImageViewReference;
		private final BitmapDisplayConfig displayConfig;

		public BitmapLoadTask(ImageView imageView, BitmapDisplayConfig config) {
			targetImageViewReference = new WeakReference<ImageView>(imageView);
			displayConfig = config;
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			if (params != null && params.length > 0) {
				uri = (String) params[0];
			} else {
				return null;
			}

			Bitmap bitmap = null;
			synchronized (pauseTaskLock) {
				while (pauseTask && !this.isCancelled()) {
					try {
						pauseTaskLock.wait();
					} catch (InterruptedException e) {
					}
				}
			}

			// 从磁盘缓存获取图片
			if (!pauseTask && !this.isCancelled()
					&& this.getTargetImageView() != null) {
				bitmap = globalConfig.getBitmapCache().getBitmapFromDiskCache(
						uri, displayConfig);
			}

			// 下载图片
			if (bitmap == null && !pauseTask && !this.isCancelled()
					&& this.getTargetImageView() != null) {
				bitmap = globalConfig.getBitmapCache().downloadBitmap(uri,
						displayConfig);
			}

			return bitmap;
		}

		// 获取图片任务完成
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled() || pauseTask) {
				bitmap = null;
			}

			final ImageView imageView = this.getTargetImageView();
			if (imageView != null) {
				if (bitmap != null) {
					// 显示图片
					displayConfig.getImageLoadCallBack().loadCompleted(
							imageView, bitmap, displayConfig);
				} else {
					// 显示获取错误图片
					displayConfig.getImageLoadCallBack().loadFailed(imageView,
							displayConfig.getLoadFailedBitmap());
				}
			}
		}

		@Override
		protected void onCancelled(Bitmap bitmap) {
			super.onCancelled(bitmap);
			synchronized (pauseTaskLock) {
				pauseTaskLock.notifyAll();
			}
		}

		/**
		 * 获取线程匹配的imageView,防止出现闪动的现象
		 * 
		 * @return
		 */
		private ImageView getTargetImageView() {
			final ImageView imageView = targetImageViewReference.get();
			final BitmapLoadTask bitmapWorkerTask = getBitmapTaskFromImageView(imageView);

			if (this == bitmapWorkerTask) {
				return imageView;
			}

			return null;
		}
	}

}
