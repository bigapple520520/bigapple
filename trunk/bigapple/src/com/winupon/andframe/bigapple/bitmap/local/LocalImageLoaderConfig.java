/* 
 * @(#)LocalImageLoaderConfig.java    Created on 2014-10-14
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap.local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import android.app.ActivityManager;
import android.content.Context;

/**
 * 本地加载器的全局配置
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-10-14 下午5:09:34 $
 */
public class LocalImageLoaderConfig {
    private int memoryCacheSize = 1024 * 1024 * 8; // 8MB
    private boolean memoryCacheEnabled = true;

    private int threadPoolSize = 10;
    private boolean _dirty_params_bitmapLoadExecutor = true;
    private ExecutorService bitmapLoadExecutor;

    private final Context application;

    public LocalImageLoaderConfig(Context application) {
        this.application = application;
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
        this.memoryCacheSize = memoryCacheSize;
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
    }

    // //////////////////////////////////////////线程池配置/////////////////////////////////////////////////////////
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

    // ////////////////////////////////////////////内存信息查看//////////////////////////////////////
    private int getMemoryClass() {
        return ((ActivityManager) application.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

}
