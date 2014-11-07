/* 
 * @(#)AnBitmapUtilsFactory.java    Created on 2013-9-17
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.winupon.andframe.bigapple.bitmap.callback.ImageLoadCallBack;
import com.winupon.andframe.bigapple.bitmap.core.GlobalPolicy;
import com.winupon.andframe.bigapple.bitmap.download.Downloader;
import com.winupon.andframe.bigapple.bitmap.local.BitmapCacheBean;
import com.winupon.andframe.bigapple.bitmap.local.CacheBean;

/**
 * AnBitmapUtils接口门面，使其保持单例，谢谢，提供给使用者，使用更加方便
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午4:59:56 $
 */
public class AnBitmapUtilsFace {
    private AnBitmapUtils anBitmapUtils;
    private static AnBitmapUtilsFace instance;// 单例模式

    private AnBitmapUtilsFace(Context application) {
        anBitmapUtils = new AnBitmapUtils(application);
        anBitmapUtils.getGlobalConfig().setGlobalPolicy(new GlobalPolicy() {
            @Override
            public CacheBean makeCacheBean() {
                return new BitmapCacheBean();
            }
        });
    }

    /**
     * 初始化，请在程序起来的时候初始化，即Application启动，可以多次调用最终只初始化一次
     * 
     * @param context
     */
    public synchronized static AnBitmapUtilsFace init(Context context) {
        if (null == context) {
            throw new NullPointerException("初始化context不能为空");
        }

        Application application = null;
        if (context instanceof Activity) {
            application = ((Activity) context).getApplication();
        }
        else if (context instanceof Application) {
            application = (Application) context;
        }

        if (null == application) {
            throw new RuntimeException("初始化异常，原因：application为空");
        }

        if (null == instance) {
            instance = new AnBitmapUtilsFace(application);
        }

        return instance;
    }

    /**
     * 获取单例
     * 
     * @return
     */
    public static AnBitmapUtilsFace getInstance() {
        instanceNullCheck();
        return AnBitmapUtilsFace.instance;
    }

    /**
     * 获取其中核心的实例
     * 
     * @return
     */
    public AnBitmapUtils getAnBitmapUtils() {
        instanceNullCheck();
        return AnBitmapUtilsFace.instance.anBitmapUtils;
    }

    // 判断实例是否是空
    private static void instanceNullCheck() {
        if (null == AnBitmapUtilsFace.instance) {
            throw new RuntimeException("请先初始化AnBitmapUtils实例，方法：在程序启动的时候调用AnBitmapUtilsFace.init方法！");
        }
    }

    // ====================================配置信息================================================
    /**
     * 设置是否开启磁盘缓存
     * 
     * @param diskCacheEnabled
     * @return
     */
    public AnBitmapUtilsFace configDiskCacheEnabled(boolean diskCacheEnabled) {
        getAnBitmapUtils().getGlobalConfig().setDiskCacheEnabled(diskCacheEnabled);
        return this;
    }

    /**
     * 设置缓存存放磁盘路径
     * 
     * @param diskCachePath
     * @return
     */
    public AnBitmapUtilsFace configDiskCachePath(String diskCachePath) {
        getAnBitmapUtils().getGlobalConfig().setDiskCachePath(diskCachePath);
        return this;
    }

    /**
     * 设置磁盘缓存大小
     * 
     * @param diskCacheSize
     * @return
     */
    public AnBitmapUtilsFace configDiskCacheSize(int diskCacheSize) {
        getAnBitmapUtils().getGlobalConfig().setDiskCacheSize(diskCacheSize);
        return getInstance();
    }

    /**
     * 设置是否开启内存缓存
     * 
     * @param memoryCacheEnabled
     * @return
     */
    public AnBitmapUtilsFace configMemoryCacheEnabled(boolean memoryCacheEnabled) {
        getAnBitmapUtils().getGlobalConfig().setMemoryCacheEnabled(memoryCacheEnabled);
        return getInstance();
    }

    /**
     * 设置内存缓存的大小
     * 
     * @param memoryCacheSize
     * @return
     */
    public AnBitmapUtilsFace configMemoryCacheSize(int memoryCacheSize) {
        getAnBitmapUtils().getGlobalConfig().setMemoryCacheSize(memoryCacheSize);
        return getInstance();
    }

    /**
     * 设置内存缓存的大小（按当前可用内存分配）
     * 
     * @param percent
     * @return
     */
    public AnBitmapUtilsFace configMemCacheSizePercent(float percent) {
        getAnBitmapUtils().getGlobalConfig().setMemCacheSizePercent(percent);
        return getInstance();
    }

    /**
     * 设置缓存超时时间
     * 
     * @param defaultCacheExpiry
     * @return
     */
    public AnBitmapUtilsFace configDefaultCacheExpiry(long defaultCacheExpiry) {
        getAnBitmapUtils().getGlobalConfig().setDefaultCacheExpiry(defaultCacheExpiry);
        return getInstance();
    }

    /**
     * 设置自定义下载器
     * 
     * @param downloader
     * @return
     */
    public AnBitmapUtilsFace configDownloader(Downloader downloader) {
        getAnBitmapUtils().getGlobalConfig().setDownloader(downloader);
        return this;
    }

    /**
     * 设置下载图片的线程池的大小
     * 
     * @param threadPoolSize
     * @return
     */
    public AnBitmapUtilsFace configThreadPoolSize(int threadPoolSize) {
        getAnBitmapUtils().getGlobalConfig().setThreadPoolSize(threadPoolSize);
        return this;
    }

    /**
     * 设置是否显示原图
     * 
     * @param showOriginal
     * @return
     */
    public AnBitmapUtilsFace configShowOriginal(boolean showOriginal) {
        getAnBitmapUtils().getDefaultDisplayConfig().setShowOriginal(showOriginal);
        return this;
    }

    /**
     * 设置最大宽度
     * 
     * @param bitmapMaxWidth
     * @return
     */
    public AnBitmapUtilsFace configBitmapMaxWidth(int bitmapMaxWidth) {
        getAnBitmapUtils().getDefaultDisplayConfig().setBitmapMaxWidth(bitmapMaxWidth);
        return this;
    }

    /**
     * 设置最大高度
     * 
     * @param bitmapMaxHeight
     * @return
     */
    public AnBitmapUtilsFace configBitmapMaxHeight(int bitmapMaxHeight) {
        getAnBitmapUtils().getDefaultDisplayConfig().setBitmapMaxHeight(bitmapMaxHeight);
        return this;
    }

    /**
     * 设置加载成功后的显示动画
     * 
     * @param animation
     * @return
     */
    public AnBitmapUtilsFace configAnimation(Animation animation) {
        getAnBitmapUtils().getDefaultDisplayConfig().setAnimation(animation);
        return this;
    }

    /**
     * 设置正在加载中的过渡图片
     * 
     * @param loadingBitmap
     * @return
     */
    public AnBitmapUtilsFace configLoadingBitmap(Bitmap loadingBitmap) {
        getAnBitmapUtils().getDefaultDisplayConfig().setLoadingBitmap(loadingBitmap);
        return this;
    }

    /**
     * 设置正在加载失败后的图片
     * 
     * @param loadFailedBitmap
     * @return
     */
    public AnBitmapUtilsFace configLoadFailedBitmap(Bitmap loadFailedBitmap) {
        getAnBitmapUtils().getDefaultDisplayConfig().setLoadFailedBitmap(loadFailedBitmap);
        return this;
    }

    /**
     * 设置如果需要从网络上加载图片可以监控到的回调
     * 
     * @param imageLoadCallBack
     * @return
     */
    public AnBitmapUtilsFace configImageLoadCallBack(ImageLoadCallBack imageLoadCallBack) {
        getAnBitmapUtils().getDefaultDisplayConfig().setImageLoadCallBack(imageLoadCallBack);
        return this;
    }

    /**
     * 设置图片的质量参数
     * 
     * @param bitmapConfig
     * @return
     */
    public AnBitmapUtilsFace configBitmapConfig(Bitmap.Config bitmapConfig) {
        getAnBitmapUtils().getDefaultDisplayConfig().setBitmapConfig(bitmapConfig);
        return this;
    }

    /**
     * 设置图片圆角的幅度
     * 
     * @param roundPx
     * @return
     */
    public AnBitmapUtilsFace configRoundPx(float roundPx) {
        getAnBitmapUtils().getDefaultDisplayConfig().setRoundPx(roundPx);
        return this;
    }

    // ====================================显示图片================================================
    /**
     * 显示图片
     * 
     * @param imageView
     * @param uri
     */
    public void display(ImageView imageView, String uri) {
        getAnBitmapUtils().display(imageView, uri);
    }

    /**
     * 显示图片
     * 
     * @param imageView
     * @param uri
     * @param displayConfig
     *            显示自定义参数
     */
    public void display(ImageView imageView, String uri, BitmapDisplayConfig displayConfig) {
        getAnBitmapUtils().display(imageView, uri, displayConfig);
    }

    // =====================================从缓存中获取图片=============================================
    /**
     * 从缓存中获取图片，如果没有就返回null
     * 
     * @param uri
     * @param displayConfig
     *            图片显示规格，如果使用默认参数就设置成null
     * @return
     */
    public Bitmap getBitmapFromCache(String uri, BitmapDisplayConfig displayConfig) {
        return getAnBitmapUtils().getBitmapFromCache(uri, displayConfig);
    }

    // ===================================清理缓存部分===================================================
    /**
     * 清理指定缓
     * 
     * @param uri
     * @param afterClearCacheListener
     *            缓存清理成功后的回调，如果不想处理，设置成null也可
     */
    public void clearCache(String uri, AfterClearCacheListener afterClearCacheListener) {
        getAnBitmapUtils().clearCache(uri, afterClearCacheListener);
    }

    /**
     * 清理指定缓
     * 
     * @param uri
     */
    public void clearCache(String uri) {
        getAnBitmapUtils().clearCache(uri, (AfterClearCacheListener) null);
    }

    /**
     * 清理所有缓存
     * 
     * @param afterClearCacheListener
     *            缓存清理成功后的回调，如果不想处理，设置成null也可
     */
    public void clearCacheAll(AfterClearCacheListener afterClearCacheListener) {
        getAnBitmapUtils().clearCache(afterClearCacheListener);
    }

    /**
     * 清理所有缓存
     * 
     */
    public void clearCacheAll() {
        getAnBitmapUtils().clearCache((AfterClearCacheListener) null);
    }

    /**
     * 关闭缓存，一般可以在退出时使用
     * 
     * @param listener
     */
    public void closeCache(AfterClearCacheListener listener) {
        getAnBitmapUtils().closeCache(listener);
    }

    /**
     * 关闭缓存，一般可以在退出时使用
     * 
     * @param listener
     */
    public void closeCache() {
        getAnBitmapUtils().closeCache((AfterClearCacheListener) null);
    }

}
