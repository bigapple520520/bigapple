package com.xuan.bigapple.lib.bitmap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.xuan.bigapple.lib.bitmap.core.cache.BitmapCache;
import com.xuan.bigapple.lib.bitmap.core.cache.BitmapCacheManager;
import com.xuan.bigapple.lib.bitmap.core.utils.BitmapCommonUtils;
import com.xuan.bigapple.lib.bitmap.listeners.DownloaderListener;
import com.xuan.bigapple.lib.bitmap.listeners.MakeCacheKeyListener;
import com.xuan.bigapple.lib.bitmap.listeners.impl.DefaultDownloaderListener;
import com.xuan.bigapple.lib.bitmap.listeners.impl.DefaultMakeCacheKeyListener;

/**
 * 图片加载的全局配置，包括了缓存管理等一些参数
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午4:24:49 $
 */
public class BitmapGlobalConfig {
	/** 磁盘缓存地址 */
	private String diskCachePath;

	/** 最小内存缓存阀值：2M */
	public final static int MIN_MEMORY_CACHE_SIZE = 1024 * 1024 * 2;
	/** 内存缓存大小：默认8M */
	private int memoryCacheSize = 1024 * 1024 * 8;

	/** 最小磁盘缓存阀值：10M */
	public final static int MIN_DISK_CACHE_SIZE = 1024 * 1024 * 10;
	/** 磁盘缓存大小：默认50M */
	private int diskCacheSize = 1024 * 1024 * 50;

	/** 是否开启内存缓存 */
	private boolean memoryCacheEnabled = true;
	/** 是否开启磁盘缓存 */
	private boolean diskCacheEnabled = true;

	/** 图片缓存 */
	private BitmapCache bitmapCache;

	/** 处理线程数 */
	private int threadPoolSize = 5;
	/** 标记是否需要重新初始化线程池 */
	private boolean _dirty_params_bitmapLoadExecutor = true;
	/** 线程池 */
	private ExecutorService bitmapLoadExecutor;

	/** 上下文 */
	private final Context application;

	/** cacheKey生成监听 */
	private MakeCacheKeyListener makeCacheKeyListener;
	/** 图片下载器 */
	private DownloaderListener downloaderListener;

	public BitmapGlobalConfig(Context application) {
		this.application = application;
		// 初始化缓存
		initBitmapCache();
	}

	/** 初始化缓存 */
	private void initBitmapCache() {
		BitmapCacheManager bitmapCacheManager = BitmapCacheManager
				.getInstance(getBitmapCache());
		bitmapCacheManager.initMemoryCache();
		bitmapCacheManager.initDiskCache();
	}

	// ///////////////////////////////////////////缓存磁盘路劲///////////////////////////////////////////////////////
	/**
	 * 获取磁盘缓存路径
	 * 
	 * @return
	 */
	public String getDiskCachePath() {
		if (TextUtils.isEmpty(diskCachePath)) {
			diskCachePath = BitmapCommonUtils.getDiskCacheDir(application,
					"anBitmapCache");
		}
		return diskCachePath;
	}

	/**
	 * 设置磁盘缓存路径
	 * 
	 * @param diskCachePath
	 */
	public BitmapGlobalConfig setDiskCachePath(String diskCachePath) {
		this.diskCachePath = diskCachePath;
		return this;
	}

	// ///////////////////////////////////////////获取缓存模块///////////////////////////////////////////////////////////
	public BitmapCache getBitmapCache() {
		if (null == bitmapCache) {
			bitmapCache = new BitmapCache(this);
		}
		return bitmapCache;
	}

	// ///////////////////////////////////////////设置内存缓存大小//////////////////////////////////////////////////////
	/**
	 * 获取内存缓存大小
	 * 
	 * @return
	 */
	public int getMemoryCacheSize() {
		return memoryCacheSize;
	}

	/**
	 * 设置内存缓存大小
	 * 
	 * @param memoryCacheSize
	 */
	public BitmapGlobalConfig setMemoryCacheSize(int memoryCacheSize) {
		if (memoryCacheSize >= MIN_MEMORY_CACHE_SIZE) {
			this.memoryCacheSize = memoryCacheSize;
			if (null != bitmapCache) {
				bitmapCache.setMemoryCacheSize(this.memoryCacheSize);
			}
		} else {
			this.setMemCacheSizePercent(0.3f);// 设置默认的内存缓存大小
		}
		return this;
	}

	/**
	 * 设置内存缓存大小，按百分比设置
	 * 
	 * @param percent
	 *            在 0.05 和 0.8 之间(不包括两端)
	 */
	public BitmapGlobalConfig setMemCacheSizePercent(float percent) {
		if (percent < 0.05f || percent > 0.8f) {
			throw new IllegalArgumentException(
					"非法参数错误，原因：内存缓存的阀值必须在0.05和0.8之间,不包括0.05和0.8");
		}
		this.memoryCacheSize = Math.round(percent * getMemoryClass() * 1024
				* 1024);
		if (bitmapCache != null) {
			bitmapCache.setMemoryCacheSize(this.memoryCacheSize);
		}
		return this;
	}

	// ///////////////////////////////////////////磁盘缓存大小///////////////////////////////////////////////////////
	/**
	 * 获取磁盘缓存大小
	 * 
	 * @return
	 */
	public int getDiskCacheSize() {
		return diskCacheSize;
	}

	/**
	 * 设置磁盘缓存大小
	 * 
	 * @param diskCacheSize
	 */
	public BitmapGlobalConfig setDiskCacheSize(int diskCacheSize) {
		if (diskCacheSize >= MIN_DISK_CACHE_SIZE) {
			this.diskCacheSize = diskCacheSize;
			if (bitmapCache != null) {
				bitmapCache.setDiskCacheSize(this.diskCacheSize);
			}
		}
		return this;
	}

	// ///////////////////////////////////////////设置线程池数量/////////////////////////////////////////////////////////
	/**
	 * 获取加载线程池数
	 * 
	 * @return
	 */
	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	/**
	 * 设置加载线程池数
	 * 
	 * @param threadPoolSize
	 */
	public BitmapGlobalConfig setThreadPoolSize(int threadPoolSize) {
		if (threadPoolSize != this.threadPoolSize) {
			_dirty_params_bitmapLoadExecutor = true;
			this.threadPoolSize = threadPoolSize;
		}
		return this;
	}

	// ///////////////////////////////////////////加载图片的线程池////////////////////////////////////////////////////////
	/**
	 * 获取线程池
	 * 
	 * @return
	 */
	public ExecutorService getBitmapLoadExecutor() {
		if (_dirty_params_bitmapLoadExecutor || bitmapLoadExecutor == null) {
			bitmapLoadExecutor = Executors.newFixedThreadPool(
					getThreadPoolSize(), new ThreadFactory() {
						@Override
						public Thread newThread(Runnable r) {
							Thread t = new Thread(r);
							t.setPriority(Thread.NORM_PRIORITY - 1);
							return t;
						}
					});
			_dirty_params_bitmapLoadExecutor = false;
		}
		return bitmapLoadExecutor;
	}

	// ///////////////////////////////////////////是否开启内存缓存////////////////////////////////////////////////////////
	/**
	 * 判断是否开启内存缓存
	 * 
	 * @return
	 */
	public boolean isMemoryCacheEnabled() {
		return memoryCacheEnabled;
	}

	/**
	 * 设置是否开启内存缓存
	 * 
	 * @param memoryCacheEnabled
	 */
	public BitmapGlobalConfig setMemoryCacheEnabled(boolean memoryCacheEnabled) {
		this.memoryCacheEnabled = memoryCacheEnabled;
		return this;
	}

	// ///////////////////////////////////////////是否开启磁盘缓存//////////////////////////////////////////////////////
	/**
	 * 判断是否开启磁盘缓存
	 * 
	 * @return
	 */
	public boolean isDiskCacheEnabled() {
		return diskCacheEnabled;
	}

	/**
	 * 设置是否开启磁盘缓存
	 * 
	 * @param diskCacheEnabled
	 */
	public BitmapGlobalConfig setDiskCacheEnabled(boolean diskCacheEnabled) {
		this.diskCacheEnabled = diskCacheEnabled;
		return this;
	}

	// ////////////////////////////////////////////内存信息查看//////////////////////////////////////
	/** 获取可用内存信息 */
	private int getMemoryClass() {
		return ((ActivityManager) application
				.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
	}

	public MakeCacheKeyListener getMakeCacheKeyListener() {
		if (null == makeCacheKeyListener) {
			makeCacheKeyListener = new DefaultMakeCacheKeyListener();
		}

		return makeCacheKeyListener;
	}

	public void setMakeCacheKeyListener(
			MakeCacheKeyListener makeCacheKeyListener) {
		this.makeCacheKeyListener = makeCacheKeyListener;
	}

	public DownloaderListener getDownloaderListener() {
		if (null == downloaderListener) {
			downloaderListener = new DefaultDownloaderListener();
		}

		return downloaderListener;
	}

	public void setDownloaderListener(DownloaderListener downloaderListener) {
		this.downloaderListener = downloaderListener;
	}

}
