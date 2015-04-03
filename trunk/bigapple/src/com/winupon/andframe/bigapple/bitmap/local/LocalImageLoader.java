/* 
 * @(#)LocalImageLoader.java    Created on 2014-10-13
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.bitmap.local;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.ImageView;

import com.winupon.andframe.bigapple.bitmap.BitmapDisplayConfig;
import com.winupon.andframe.bigapple.bitmap.CompatibleAsyncTask;
import com.winupon.andframe.bigapple.bitmap.cache.LruMemoryCache;
import com.winupon.andframe.bigapple.bitmap.core.BitmapCommonUtils;
import com.winupon.andframe.bigapple.bitmap.core.BitmapDecoder;
import com.winupon.andframe.bigapple.utils.Validators;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 本地图片加载器
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-10-13 下午6:02:26 $
 */
public class LocalImageLoader {
    private Context application;
    private LruMemoryCache<String, CacheBean> cache;// 缓存
    private LocalImageLoaderConfig localImageLoaderConfig;// 全局配置
    private BitmapDisplayConfig defaultConfig;// 显示配置

    private boolean pauseTask = false;// 用来暂停任务的，特别是ListView快速滑动时需要暂定，不然会卡顿
    private final Object pauseTaskLock = new Object();// 暂停锁，暂时时，线程就用这个锁锁住

    public LocalImageLoader(Context application) {
        this.application = application;
        localImageLoaderConfig = new LocalImageLoaderConfig(application);
        defaultConfig = new BitmapDisplayConfig();

        // 基于LRU算法缓存
        cache = new LruMemoryCache<String, CacheBean>(localImageLoaderConfig.getMemoryCacheSize()) {
            @Override
            protected int sizeOf(String key, CacheBean cacheBean) {
                return BitmapCommonUtils.getBitmapSize(cacheBean.getBitmap());
            }
        };
    }

    /**
     * 显示图片
     * 
     * @param imageView
     * @param filePath
     * @param maxWidth
     * @param maxHeight
     */
    public void display(ImageView imageView, String filePath, BitmapDisplayConfig config) {
        if (null == imageView) {
            return;
        }

        if (null == config) {
            config = defaultConfig;
        }

        if (Validators.isEmpty(filePath)) {
            config.getImageLoadCallBack().loadFailed(imageView, config);
            return;
        }

        // 缓存是否命中
        if (localImageLoaderConfig.isMemoryCacheEnabled()) {
            CacheBean cacheBean = cache.get(filePath + config.toString());
            if (null != cacheBean) {
                Bitmap b = cacheBean.getBitmap();
                if (null != b) {
                    LogUtils.d("yes!!!cache is shot!!!");
                    config.getImageLoadCallBack().loadCompleted(imageView, b, config);
                    return;
                }
                else {
                    LogUtils.d("damn it!!!bitmap is recyle by VM!!!");
                }
            }
            else {
                LogUtils.d("no!!!cache is miss,i need get bitmap from disk!!!");
            }
        }

        // 异步从磁盘中获取
        if (!bitmapLoadTaskExist(imageView, filePath)) {
            // 启动任务类：从网络下载或者从磁盘中获取
            final BitmapLoadTask loadTask = new BitmapLoadTask(imageView);
            final AsyncBitmapDrawable asyncBitmapDrawable = new AsyncBitmapDrawable(application.getResources(),
                    config.getLoadingBitmap(), loadTask);
            imageView.setImageDrawable(asyncBitmapDrawable);// 设置下载任务资源
            loadTask.executeOnExecutor(localImageLoaderConfig.getBitmapLoadExecutor(), filePath, config);
        }
    }

    // ////////////////////////////////////内部辅助方法///////////////////////////////////////////////
    private static boolean bitmapLoadTaskExist(ImageView imageView, String uri) {
        final BitmapLoadTask oldLoadTask = getBitmapTaskFromImageView(imageView);

        if (null != oldLoadTask) {
            final String oldUri = oldLoadTask.filePath;
            if (TextUtils.isEmpty(oldUri) || !oldUri.equals(uri)) {
                oldLoadTask.cancel(true);// 取消原先在下载的地址任务
            }
            else {
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
    private class BitmapLoadTask extends CompatibleAsyncTask<Object, Void, Bitmap> {
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
                    }
                    catch (InterruptedException e) {
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
                    }
                    else {
                        bitmap = BitmapDecoder.decodeSampledBitmapFromFile(filePath, config.getBitmapMaxWidth(),
                                config.getBitmapMaxHeight(), config.getBitmapConfig());
                    }

                    if (localImageLoaderConfig.isMemoryCacheEnabled()) {
                        CacheBean cacheBean = makeCacheBean();
                        cacheBean.setBitmap(bitmap);
                        cache.put(filePath + config.toString(), cacheBean);
                    }
                }
                catch (Exception e) {
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
                config.getImageLoadCallBack().loadCompleted(imageView, bitmap, config);
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

        public AsyncBitmapDrawable(Resources res, Bitmap bitmap, BitmapLoadTask loadTask) {
            super(res, bitmap);
            bitmapLoadTask = new WeakReference<BitmapLoadTask>(loadTask);
        }

        public BitmapLoadTask getBitmapLoadTask() {
            return bitmapLoadTask.get();
        }
    }

    public LruMemoryCache<String, CacheBean> getCache() {
        return cache;
    }

    public void setCache(LruMemoryCache<String, CacheBean> cache) {
        this.cache = cache;
    }

    public LocalImageLoaderConfig getLocalImageLoaderConfig() {
        return localImageLoaderConfig;
    }

    public void setLocalImageLoaderConfig(LocalImageLoaderConfig localImageLoaderConfig) {
        this.localImageLoaderConfig = localImageLoaderConfig;
    }

    public BitmapDisplayConfig getDefaultConfig() {
        return defaultConfig;
    }

    public void setDefaultConfig(BitmapDisplayConfig defaultConfig) {
        this.defaultConfig = defaultConfig;
    }

    /**
     * 清理缓存
     */
    public void clearCacheAll() {
        if (null != cache) {
            cache.evictAll();
        }
    }

    /**
     * 清理缓存
     */
    public void clearCache(String key) {
        if (null != cache) {
            cache.remove(key);
        }
    }

    /**
     * 可以复写这个方法来实现自己更加灵活的缓存引用，默认使用了软引用
     * 
     * @return
     */
    protected CacheBean makeCacheBean() {
        return new SoftReferenceCacheBean();
    }

    /**
     * 从缓存中获取已缓存的图片，如果没有，返回null
     * 
     * @param uri
     *            图片地址
     * @return
     */
    public Bitmap getBitmapFromCache(String uri, BitmapDisplayConfig config) {
        if (Validators.isEmpty(uri)) {
            return null;
        }

        if (null == config) {
            config = defaultConfig;
        }

        CacheBean cacheBean = cache.get(uri + config.toString());
        if (null != cacheBean) {
            Bitmap b = cacheBean.getBitmap();
            if (null != b) {
                LogUtils.d("yes!!!cache is shot!!!");
                return b;
            }
            else {
                LogUtils.d("damn it!!!bitmap is recyle by VM!!!");
            }
        }
        else {
            LogUtils.d("no!!!cache is miss,i need get bitmap from disk!!!");
        }
        return null;// 缓存中取不到
    }

    // //////////////////////////////////任务暂定开始操作///////////////////////////////////////////////////////////////
    /**
     * 重启加载任务
     */
    public void resumeTasks() {
        pauseTask = false;
        synchronized (pauseTaskLock) {
            pauseTaskLock.notifyAll();
        }
    }

    /**
     * 暂停加载任务
     */
    public void pauseTasks() {
        pauseTask = true;
    }

}
