package com.winupon.andframe.bigapple.bitmap;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.winupon.andframe.bigapple.bitmap.callback.ImageLoadCallBack;
import com.winupon.andframe.bigapple.bitmap.download.Downloader;

/**
 * 网络图片加载工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-9 下午4:48:21 $
 */
public class AnBitmapUtils {
    private boolean pauseTask = false;
    private final Object pauseTaskLock = new Object();

    private final Context context;
    private BitmapGlobalConfig globalConfig;
    private BitmapDisplayConfig defaultDisplayConfig;

    // /////////////////////////////// 创建实例，用户使用时可自行保持单例/////////////////////////////////////////////
    public AnBitmapUtils(Context context, String diskCachePath) {
        this.context = context;
        globalConfig = new BitmapGlobalConfig(context, diskCachePath);
        defaultDisplayConfig = new BitmapDisplayConfig(context);
    }

    public AnBitmapUtils(Context context) {
        this(context, null);
    }

    public AnBitmapUtils(Context context, String diskCachePath, int memoryCacheSize) {
        this(context, diskCachePath);
        globalConfig.setMemoryCacheSize(memoryCacheSize);
    }

    public AnBitmapUtils(Context context, String diskCachePath, int memoryCacheSize, int diskCacheSize) {
        this(context, diskCachePath);
        globalConfig.setMemoryCacheSize(memoryCacheSize);
        globalConfig.setDiskCacheSize(diskCacheSize);
    }

    public AnBitmapUtils(Context context, String diskCachePath, float memoryCachePercent) {
        this(context, diskCachePath);
        globalConfig.setMemCacheSizePercent(memoryCachePercent);
    }

    public AnBitmapUtils(Context context, String diskCachePath, float memoryCachePercent, int diskCacheSize) {
        this(context, diskCachePath);
        globalConfig.setMemCacheSizePercent(memoryCachePercent);
        globalConfig.setDiskCacheSize(diskCacheSize);
    }

    // ///////////////////////////////对外可配置参数///////////////////////////////////////////////////////////////////
    public AnBitmapUtils configDefaultLoadingImage(Bitmap bitmap) {
        defaultDisplayConfig.setLoadingBitmap(bitmap);
        return this;
    }

    public AnBitmapUtils configDefaultLoadingImage(int resId) {
        defaultDisplayConfig.setLoadingBitmap(BitmapFactory.decodeResource(context.getResources(), resId));
        return this;
    }

    public AnBitmapUtils configDefaultLoadFailedImage(Bitmap bitmap) {
        defaultDisplayConfig.setLoadFailedBitmap(bitmap);
        return this;
    }

    public AnBitmapUtils configDefaultLoadFailedImage(int resId) {
        defaultDisplayConfig.setLoadFailedBitmap(BitmapFactory.decodeResource(context.getResources(), resId));
        return this;
    }

    public AnBitmapUtils configDefaultBitmapMaxWidth(int bitmapWidth) {
        defaultDisplayConfig.setBitmapMaxWidth(bitmapWidth);
        return this;
    }

    public AnBitmapUtils configDefaultBitmapMaxHeight(int bitmapHeight) {
        defaultDisplayConfig.setBitmapMaxHeight(bitmapHeight);
        return this;
    }

    public AnBitmapUtils configDefaultImageLoadAnimation(Animation animation) {
        defaultDisplayConfig.setAnimation(animation);
        return this;
    }

    public AnBitmapUtils configDefaultImageLoadCallBack(ImageLoadCallBack imageLoadCallBack) {
        defaultDisplayConfig.setImageLoadCallBack(imageLoadCallBack);
        return this;
    }

    public AnBitmapUtils configDefaultShowOriginal(boolean showOriginal) {
        defaultDisplayConfig.setShowOriginal(showOriginal);
        return this;
    }

    public AnBitmapUtils configDefaultBitmapConfig(Bitmap.Config config) {
        defaultDisplayConfig.setBitmapConfig(config);
        return this;
    }

    public AnBitmapUtils configDefaultDisplayConfig(BitmapDisplayConfig displayConfig) {
        defaultDisplayConfig = displayConfig;
        return this;
    }

    public AnBitmapUtils configDownloader(Downloader downloader) {
        globalConfig.setDownloader(downloader);
        return this;
    }

    /**
     * 设置默认的缓存过期时间。如果http请求返回了过期时间，使用请求返回的时间。否则按设置的来
     * 
     * @param defaultExpiry
     * @return
     */
    public AnBitmapUtils configDefaultCacheExpiry(long defaultExpiry) {
        globalConfig.setDefaultCacheExpiry(defaultExpiry);
        return this;
    }

    public AnBitmapUtils configThreadPoolSize(int poolSize) {
        globalConfig.setThreadPoolSize(poolSize);
        return this;
    }

    public AnBitmapUtils configMemoryCacheEnabled(boolean enabled) {
        globalConfig.setMemoryCacheEnabled(enabled);
        return this;
    }

    public AnBitmapUtils configDiskCacheEnabled(boolean enabled) {
        globalConfig.setDiskCacheEnabled(enabled);
        return this;
    }

    public AnBitmapUtils configAfterClearCacheListener(AfterClearCacheListener afterClearCacheListener) {
        globalConfig.setAfterClearCacheListener(afterClearCacheListener);
        return this;
    }

    public AnBitmapUtils configGlobalConfig(BitmapGlobalConfig globalConfig) {
        this.globalConfig = globalConfig;
        return this;
    }

    // /////////////////////////////// 加载展示图片 ////////////////////////////////////////////////////////////////////
    public void display(ImageView imageView, String uri) {
        display(imageView, uri, null);
    }

    public void display(ImageView imageView, String uri, BitmapDisplayConfig displayConfig) {
        if (imageView == null) {
            return;
        }

        if (displayConfig == null) {
            displayConfig = defaultDisplayConfig;
        }

        if (TextUtils.isEmpty(uri)) {
            displayConfig.getImageLoadCallBack().loadFailed(imageView, displayConfig.getLoadFailedBitmap());
            return;
        }

        Bitmap bitmap = null;
        bitmap = globalConfig.getBitmapCache().getBitmapFromMemCache(uri, displayConfig);// 缓存中取

        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        }
        else if (!bitmapLoadTaskExist(imageView, uri)) {
            final BitmapLoadTask loadTask = new BitmapLoadTask(imageView, displayConfig);

            // 设置下载任务资源
            final AsyncBitmapDrawable asyncBitmapDrawable = new AsyncBitmapDrawable(context.getResources(),
                    displayConfig.getLoadingBitmap(), loadTask);
            imageView.setImageDrawable(asyncBitmapDrawable);

            // 执行从网络上获取磁盘中加载图片
            loadTask.executeOnExecutor(globalConfig.getBitmapLoadExecutor(), uri);
        }
    }

    // ////////////////////////////////////////缓存清理/////////////////////////////////////////////////////////////////
    public void clearCache() {
        globalConfig.clearCache();
    }

    public void clearMemoryCache() {
        globalConfig.clearMemoryCache();
    }

    public void clearDiskCache() {
        globalConfig.clearDiskCache();
    }

    public void clearCache(String uri, BitmapDisplayConfig config) {
        if (config == null) {
            config = defaultDisplayConfig;
        }
        globalConfig.clearCache(uri, config);
    }

    public void clearMemoryCache(String uri, BitmapDisplayConfig config) {
        if (config == null) {
            config = defaultDisplayConfig;
        }
        globalConfig.clearMemoryCache(uri, config);
    }

    public void clearDiskCache(String uri) {
        globalConfig.clearDiskCache(uri);
    }

    public void flushCache() {
        globalConfig.flushCache();
    }

    public void closeCache() {
        globalConfig.closeCache();
    }

    /**
     * 从内存缓存中获取图片对象
     * 
     * @param uri
     * @param displayConfig
     * @return
     */
    public Bitmap getBitmapFromMemCache(String uri, BitmapDisplayConfig displayConfig) {
        if (displayConfig == null) {
            displayConfig = defaultDisplayConfig;
        }

        return globalConfig.getBitmapCache().getBitmapFromMemCache(uri, displayConfig);
    }

    // //////////////////////////////////任务暂定开始操作///////////////////////////////////////////////////////////////
    public void resumeTasks() {
        pauseTask = false;
    }

    public void pauseTasks() {
        pauseTask = true;
        flushCache();
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
            }
            else {
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

        public AsyncBitmapDrawable(Resources res, Bitmap bitmap, BitmapLoadTask bitmapWorkerTask) {
            super(res, bitmap);
            bitmapLoadTaskReference = new WeakReference<BitmapLoadTask>(bitmapWorkerTask);
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
    private class BitmapLoadTask extends CompatibleAsyncTask<Object, Void, Bitmap> {
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
            }
            else {
                return null;
            }

            Bitmap bitmap = null;
            synchronized (pauseTaskLock) {
                while (pauseTask && !this.isCancelled()) {
                    try {
                        pauseTaskLock.wait();
                    }
                    catch (InterruptedException e) {
                    }
                }
            }

            // 从磁盘缓存获取图片
            if (!pauseTask && !this.isCancelled() && this.getTargetImageView() != null) {
                bitmap = globalConfig.getBitmapCache().getBitmapFromDiskCache(uri, displayConfig);
            }

            // 下载图片
            if (bitmap == null && !pauseTask && !this.isCancelled() && this.getTargetImageView() != null) {
                bitmap = globalConfig.getBitmapCache().downloadBitmap(uri, displayConfig);
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
                if (bitmap != null) {// 显示图片
                    displayConfig.getImageLoadCallBack().loadCompleted(imageView, bitmap, displayConfig);
                }
                else {// 显示获取错误图片
                    displayConfig.getImageLoadCallBack().loadFailed(imageView, displayConfig.getLoadFailedBitmap());
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
