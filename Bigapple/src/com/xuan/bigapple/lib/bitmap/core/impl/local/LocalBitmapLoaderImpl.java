package com.xuan.bigapple.lib.bitmap.core.impl.local;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.xuan.bigapple.lib.asynctask.helper.CompatibleAsyncTask;
import com.xuan.bigapple.lib.bitmap.BitmapDisplayConfig;
import com.xuan.bigapple.lib.bitmap.BitmapGlobalConfig;
import com.xuan.bigapple.lib.bitmap.core.cache.LruMemoryCache;
import com.xuan.bigapple.lib.bitmap.core.impl.IBitmapLoader;
import com.xuan.bigapple.lib.bitmap.core.utils.BitmapCommonUtils;
import com.xuan.bigapple.lib.bitmap.core.utils.BitmapDecoder;
import com.xuan.bigapple.lib.bitmap.listeners.ClearCacheListener;
import com.xuan.bigapple.lib.utils.Validators;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 本地图片加载器
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-10-13 下午6:02:26 $
 */
public class LocalBitmapLoaderImpl implements IBitmapLoader {
	private Context application;
	private LruMemoryCache<String, Bitmap> cache;// 缓存
	private BitmapGlobalConfig defaultBitmapGlobalConfig;// 全局配置
	private BitmapDisplayConfig defaultBitmapDisplayConfig;// 显示配置

	private boolean pauseTask = false;// 用来暂停任务的，特别是ListView快速滑动时需要暂定，不然会卡顿
	private final Object pauseTaskLock = new Object();// 暂停锁，暂时时，线程就用这个锁锁住

	public LocalBitmapLoaderImpl(Context application) {
		this.application = application;
		defaultBitmapGlobalConfig = new BitmapGlobalConfig(application);
		defaultBitmapGlobalConfig.setDiskCacheEnabled(false);// 不需要磁盘缓存
		defaultBitmapDisplayConfig = new BitmapDisplayConfig();

		// 基于LRU算法缓存
		cache = new LruMemoryCache<String, Bitmap>(
				defaultBitmapGlobalConfig.getMemoryCacheSize()) {
			@Override
			protected int sizeOf(String key, Bitmap bitmap) {
				return BitmapCommonUtils.getBitmapSize(bitmap);
			}
		};
	}

	@Override
	public IBitmapLoader setDefaultBitmapGlobalConfig(
			BitmapGlobalConfig globalConfig) {
		this.defaultBitmapGlobalConfig = globalConfig;
		return this;
	}

	@Override
	public BitmapGlobalConfig getDefaultBitmapGlobalConfig() {
		return this.defaultBitmapGlobalConfig;
	}

	@Override
	public IBitmapLoader setDefaultBitmapDisplayConfig(
			BitmapDisplayConfig displayConfig) {
		this.defaultBitmapDisplayConfig = displayConfig;
		return this;
	}

	@Override
	public BitmapDisplayConfig getDefaultBitmapDisplayConfig() {
		return this.defaultBitmapDisplayConfig;
	}

	@Override
	public void display(ImageView imageView, String filePath,
			BitmapDisplayConfig config) {
		if (null == imageView) {
			return;
		}

		if (null == config) {
			config = defaultBitmapDisplayConfig;
		}

		if (Validators.isEmpty(filePath)) {
			config.getDisplayImageListener().loadFailed(imageView, config);
			return;
		}

		// 缓存是否命中
		if (defaultBitmapGlobalConfig.isMemoryCacheEnabled()) {
			String cacheKey = defaultBitmapGlobalConfig
					.getMakeCacheKeyListener().makeCacheKey(filePath);
			Bitmap bitmap = cache.get(cacheKey + config.toString());
			if (null != bitmap) {
				LogUtils.d("yes!!!cache is shot!!!");
				config.getDisplayImageListener().loadCompleted(imageView,
						bitmap, config);
				return;
			} else {
				LogUtils.d("no!!!cache is miss,i need get bitmap from disk!!!");
			}
		}

		// 异步从磁盘中获取
		if (!bitmapLoadTaskExist(imageView, filePath)) {
			// 启动任务类：从网络下载或者从磁盘中获取
			final BitmapLoadTask loadTask = new BitmapLoadTask(imageView);
			final AsyncBitmapDrawable asyncBitmapDrawable = new AsyncBitmapDrawable(
					application.getResources(), config.getLoadingBitmap(),
					loadTask);
			imageView.setImageDrawable(asyncBitmapDrawable);// 设置下载任务资源
			loadTask.executeOnExecutor(
					defaultBitmapGlobalConfig.getBitmapLoadExecutor(),
					filePath, config);
		}
	}

	/**
	 * 清理缓存
	 */
	@Override
	public void clearCacheAll(ClearCacheListener callback) {
		if (null != cache) {
			cache.evictAll();
		}
		if (null != callback) {
			callback.afterClearCache(5, null);// 表示只清理了指定的内存缓存
		}
	}

	@Override
	public void clearCache(String uri, ClearCacheListener callback) {
		if (null != cache) {
			cache.remove(uri);
		}
		if (null != callback) {
			callback.afterClearCache(8, uri);// 8表示只清理了所有内存缓存
		}
	}

	/**
	 * 从缓存中获取已缓存的图片，如果没有，返回null
	 * 
	 * @param uri
	 *            图片地址
	 * @return
	 */
	@Override
	public Bitmap getBitmapFromCache(String uri, BitmapDisplayConfig config) {
		if (Validators.isEmpty(uri)) {
			return null;
		}

		if (null == config) {
			config = defaultBitmapDisplayConfig;
		}

		Bitmap bitmap = cache.get(uri + config.toString());
		if (null != bitmap) {
			LogUtils.d("yes!!!cache is shot!!!");
			return bitmap;
		} else {
			LogUtils.d("no!!!cache is miss,i need get bitmap from disk!!!");
		}
		return null;// 缓存中取不到
	}

	/**
	 * 重启加载任务
	 */
	@Override
	public void resumeTasks() {
		pauseTask = false;
		synchronized (pauseTaskLock) {
			pauseTaskLock.notifyAll();
		}
	}

	/**
	 * 暂停加载任务
	 */
	@Override
	public void pauseTasks() {
		pauseTask = true;
	}

	@Override
	public void closeCache(ClearCacheListener callback) {
		clearCacheAll(callback);
	}

	// ////////////////////////////////////内部辅助方法///////////////////////////////////////////////
	private static boolean bitmapLoadTaskExist(ImageView imageView, String uri) {
		final BitmapLoadTask oldLoadTask = getBitmapTaskFromImageView(imageView);

		if (null != oldLoadTask) {
			final String oldUri = oldLoadTask.filePath;
			if (TextUtils.isEmpty(oldUri) || !oldUri.equals(uri)) {
				oldLoadTask.cancel(true);// 取消原先在下载的地址任务
			} else {
				return true;
			}
		}
		return false;
	}

	private static BitmapLoadTask getBitmapTaskFromImageView(ImageView imageView) {
		if (null != imageView) {
			final Drawable drawable = imageView.getDrawable();
			if (drawable instanceof AsyncBitmapDrawable) {
				final AsyncBitmapDrawable asyncBitmapDrawable = (AsyncBitmapDrawable) drawable;
				return asyncBitmapDrawable.getBitmapLoadTask();
			}
		}
		return null;
	}

	// //////////////////////////////////////////内部类//////////////////////////////////////////////////////
	/**
	 * 图片加载任务
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午2:45:22 $
	 */
	private class BitmapLoadTask extends
			CompatibleAsyncTask<Object, Void, Bitmap> {
		private final WeakReference<ImageView> targetImageViewReference;
		private BitmapDisplayConfig config;
		public String filePath;

		public BitmapLoadTask(ImageView imageView) {
			targetImageViewReference = new WeakReference<ImageView>(imageView);
		}

		@Override
		protected Bitmap doInBackground(Object... params) {
			filePath = (String) params[0];
			config = (BitmapDisplayConfig) params[1];

			// 判断是否需要暂停
			synchronized (pauseTaskLock) {
				while (pauseTask && !this.isCancelled()) {
					try {
						// 这里是如果调用者设置了暂定，并这个任务没有被取消，那么就停留在这里等待
						// 这里设置10S超时，防止线程一直被锁死
						pauseTaskLock.wait(10000);
					} catch (InterruptedException e) {
						// Ignore
					}
				}
			}

			// 从磁盘中读取图片
			if (!isCancelled() && null != getTargetImageView()) {
				Bitmap bitmap = null;
				try {
					if (config.isShowOriginal()) {
						bitmap = BitmapFactory.decodeFile(filePath);
					} else {
						bitmap = BitmapDecoder.decodeSampledBitmapFromFile(
								filePath, config.getBitmapMaxWidth(),
								config.getBitmapMaxHeight(),
								config.getBitmapConfig());
					}

					if (defaultBitmapGlobalConfig.isMemoryCacheEnabled()) {
						String cacheKey = defaultBitmapGlobalConfig
								.getMakeCacheKeyListener().makeCacheKey(
										filePath);
						cache.put(cacheKey + config.toString(), bitmap);
					}
				} catch (Exception e) {
					LogUtils.e(e.getMessage(), e);
				}
				return bitmap;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			final ImageView imageView = this.getTargetImageView();
			if (null != imageView && null != bitmap) {
				imageView.setImageBitmap(bitmap);
				config.getDisplayImageListener().loadCompleted(imageView,
						bitmap, config);
			}
		}

		@Override
		protected void onCancelled(Bitmap bitmap) {
			super.onCancelled(bitmap);
			// 如果这个任务类被取消，那么解锁这个线程
			synchronized (pauseTaskLock) {
				pauseTaskLock.notifyAll();
			}
		}

		// 获取线程匹配的imageView,防止出现闪动的现象
		private ImageView getTargetImageView() {
			final ImageView imageView = targetImageViewReference.get();
			final BitmapLoadTask bitmapWorkerTask = getBitmapTaskFromImageView(imageView);

			if (this == bitmapWorkerTask) {
				return imageView;
			}

			return null;
		}
	}

	/**
	 * 包含加载任务的图片资源
	 * 
	 * @author xuan
	 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午2:44:24 $
	 */
	private class AsyncBitmapDrawable extends BitmapDrawable {
		private final WeakReference<BitmapLoadTask> bitmapLoadTask;

		public AsyncBitmapDrawable(Resources res, Bitmap bitmap,
				BitmapLoadTask loadTask) {
			super(res, bitmap);
			bitmapLoadTask = new WeakReference<BitmapLoadTask>(loadTask);
		}

		public BitmapLoadTask getBitmapLoadTask() {
			return bitmapLoadTask.get();
		}
	}

	public LruMemoryCache<String, Bitmap> getCache() {
		return cache;
	}

	public void setCache(LruMemoryCache<String, Bitmap> cache) {
		this.cache = cache;
	}

	/**
	 * 清理缓存
	 */
	public void clearCache(String key) {
		if (null != cache) {
			cache.remove(key);
		}
	}

}
