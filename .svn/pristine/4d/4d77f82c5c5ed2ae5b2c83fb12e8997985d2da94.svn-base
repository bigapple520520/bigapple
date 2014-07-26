package com.winupon.andframe.bigapple.bitmap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.app.ActivityManager;
import android.content.Context;
import android.text.TextUtils;

import com.winupon.andframe.bigapple.bitmap.core.BitmapCache;
import com.winupon.andframe.bigapple.bitmap.core.BitmapCacheManager;
import com.winupon.andframe.bigapple.bitmap.core.BitmapCommonUtils;
import com.winupon.andframe.bigapple.bitmap.download.Downloader;
import com.winupon.andframe.bigapple.bitmap.download.SimpleDownloader;

/**
 * 图片加载的全局配置，包括了缓存管理等一些参数
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午4:24:49 $
 */
public class BitmapGlobalConfig {
    private String diskCachePath;
    public final static int MIN_MEMORY_CACHE_SIZE = 1024 * 1024 * 2; // 2M
    private int memoryCacheSize = 1024 * 1024 * 8; // 8MB
    public final static int MIN_DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10M
    private int diskCacheSize = 1024 * 1024 * 50; // 50M

    private boolean memoryCacheEnabled = true;
    private boolean diskCacheEnabled = true;

    private Downloader downloader;
    private static BitmapCache bitmapCache;

    private int threadPoolSize = 5;
    private boolean _dirty_params_bitmapLoadExecutor = true;
    private ExecutorService bitmapLoadExecutor;

    private long defaultCacheExpiry = 1000L * 60 * 60 * 24 * 30; // 默认30天过期

    private final Context application;

    public BitmapGlobalConfig(Context application) {
        this.application = application;
        initBitmapCache();
    }

    private void initBitmapCache() {
        BitmapCacheManager bitmapCacheManager = BitmapCacheManager.getInstance(getBitmapCache());
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
            diskCachePath = BitmapCommonUtils.getDiskCacheDir(application, "anBitmapCache");
        }
        return diskCachePath;
    }

    /**
     * 设置磁盘缓存路径
     * 
     * @param diskCachePath
     */
    public void setDiskCachePath(String diskCachePath) {
        this.diskCachePath = diskCachePath;
    }

    // ///////////////////////////////////////////设置Downloader下载器//////////////////////////////////////////////////
    /**
     * 获取加载器
     * 
     * @return
     */
    public Downloader getDownloader() {
        if (null == downloader) {
            downloader = new SimpleDownloader();
            downloader.setDefaultExpiry(getDefaultCacheExpiry());
        }
        return downloader;
    }

    /**
     * 设置加载器
     * 
     * @param downloader
     */
    public void setDownloader(Downloader downloader) {
        this.downloader = downloader;
        this.downloader.setDefaultExpiry(getDefaultCacheExpiry());
    }

    // ///////////////////////////////////////////缓存过期时间//////////////////////////////////////////////////////////
    /**
     * 获取缓存操作时间
     * 
     * @return
     */
    public long getDefaultCacheExpiry() {
        return defaultCacheExpiry;
    }

    /**
     * 设置缓存超时时间
     * 
     * @param defaultCacheExpiry
     */
    public void setDefaultCacheExpiry(long defaultCacheExpiry) {
        this.defaultCacheExpiry = defaultCacheExpiry;
        this.getDownloader().setDefaultExpiry(defaultCacheExpiry);
    }

    // ///////////////////////////////////////////获取缓存模块///////////////////////////////////////////////////////////
    public BitmapCache getBitmapCache() {
        if (bitmapCache == null) {
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
    public void setMemoryCacheSize(int memoryCacheSize) {
        if (memoryCacheSize >= MIN_MEMORY_CACHE_SIZE) {
            this.memoryCacheSize = memoryCacheSize;
            if (bitmapCache != null) {
                bitmapCache.setMemoryCacheSize(this.memoryCacheSize);
            }
        }
        else {
            this.setMemCacheSizePercent(0.3f);// 设置默认的内存缓存大小
        }
    }

    /**
     * 设置内存缓存大小，按百分比设置
     * 
     * @param percent
     *            在 0.05 和 0.8 之间(不包括两端)
     */
    public void setMemCacheSizePercent(float percent) {
        if (percent < 0.05f || percent > 0.8f) {
            throw new IllegalArgumentException("非法参数错误，原因：内存缓存的阀值必须在0.05和0.8之间,不包括0.05和0.8");
        }
        this.memoryCacheSize = Math.round(percent * getMemoryClass() * 1024 * 1024);
        if (bitmapCache != null) {
            bitmapCache.setMemoryCacheSize(this.memoryCacheSize);
        }
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
    public void setDiskCacheSize(int diskCacheSize) {
        if (diskCacheSize >= MIN_DISK_CACHE_SIZE) {
            this.diskCacheSize = diskCacheSize;
            if (bitmapCache != null) {
                bitmapCache.setDiskCacheSize(this.diskCacheSize);
            }
        }
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
    public void setThreadPoolSize(int threadPoolSize) {
        if (threadPoolSize != this.threadPoolSize) {
            _dirty_params_bitmapLoadExecutor = true;
            this.threadPoolSize = threadPoolSize;
        }
    }

    // ///////////////////////////////////////////加载图片的线程池////////////////////////////////////////////////////////
    /**
     * 获取线程池
     * 
     * @return
     */
    public ExecutorService getBitmapLoadExecutor() {
        if (_dirty_params_bitmapLoadExecutor || bitmapLoadExecutor == null) {
            bitmapLoadExecutor = Executors.newFixedThreadPool(getThreadPoolSize(), new ThreadFactory() {
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
    public void setMemoryCacheEnabled(boolean memoryCacheEnabled) {
        this.memoryCacheEnabled = memoryCacheEnabled;
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
    public void setDiskCacheEnabled(boolean diskCacheEnabled) {
        this.diskCacheEnabled = diskCacheEnabled;
    }

    // ////////////////////////////////////////////内存信息查看//////////////////////////////////////
    private int getMemoryClass() {
        return ((ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

}
