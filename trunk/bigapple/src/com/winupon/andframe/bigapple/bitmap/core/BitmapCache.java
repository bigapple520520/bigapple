package com.winupon.andframe.bigapple.bitmap.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.winupon.andframe.bigapple.bitmap.BitmapDisplayConfig;
import com.winupon.andframe.bigapple.bitmap.BitmapGlobalConfig;
import com.winupon.andframe.bigapple.bitmap.cache.LruDiskCache;
import com.winupon.andframe.bigapple.bitmap.cache.LruMemoryCache;
import com.winupon.andframe.bigapple.io.IOUtils;
import com.winupon.andframe.bigapple.utils.BitmapUtils;
import com.winupon.andframe.bigapple.utils.Validators;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 图片缓存对象，组合了内存缓存和磁盘缓存的操作。
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午6:20:12 $
 */
public class BitmapCache {
    private static final int DISK_CACHE_INDEX = 0;

    private static LruDiskCache mDiskLruCache;
    private static LruMemoryCache<String, SoftReference<Bitmap>> mMemoryCache;
    private static HashMap<String, ArrayList<String>> uri2keyListMap = new HashMap<String, ArrayList<String>>();// 辅助内存缓存

    private final Object mDiskCacheLock = new Object();// 操作磁盘缓存锁
    private boolean isDiskCacheReadied = false;// 标识磁盘是否可已读

    private final BitmapGlobalConfig globalConfig;

    public BitmapCache(BitmapGlobalConfig config) {
        this.globalConfig = config;
    }

    // ////////////////////////////////////////初始化缓存///////////////////////////////////////////////////////////////
    /**
     * 初始化内存缓存
     */
    public void initMemoryCache() {
        if (!globalConfig.isMemoryCacheEnabled()) {
            return;
        }

        if (mMemoryCache != null) {
            try {
                clearMemoryCache();
            }
            catch (Exception e) {
                LogUtils.e("清理内存缓存异常，原因：" + e.getMessage(), e);
            }
        }
        mMemoryCache = new LruMemoryCache<String, SoftReference<Bitmap>>(globalConfig.getMemoryCacheSize()) {
            @Override
            protected int sizeOf(String key, SoftReference<Bitmap> bitmapRef) {
                return BitmapCommonUtils.getBitmapSize(bitmapRef.get());
            }
        };
    }

    /**
     * 初始化磁盘缓存
     */
    public void initDiskCache() {
        if (!globalConfig.isDiskCacheEnabled()) {
            return;
        }

        synchronized (mDiskCacheLock) {
            if (null == mDiskLruCache || mDiskLruCache.isClosed()) {
                File diskCacheDir = new File(globalConfig.getDiskCachePath());
                if (!diskCacheDir.exists()) {
                    diskCacheDir.mkdirs();
                }

                long availableSpace = BitmapCommonUtils.getAvailableSpace(diskCacheDir);
                long diskCacheSize = globalConfig.getDiskCacheSize();
                diskCacheSize = availableSpace > diskCacheSize ? diskCacheSize : availableSpace;

                try {
                    mDiskLruCache = LruDiskCache.open(diskCacheDir, 1, 1, diskCacheSize);
                }
                catch (final IOException e) {
                    mDiskLruCache = null;
                    LogUtils.e(e.getMessage(), e);
                }
            }
            isDiskCacheReadied = true;
            mDiskCacheLock.notifyAll();
        }
    }

    // ////////////////////////////////////////缓存设置调整//////////////////////////////////////////////////////////
    /**
     * 设置内存缓存大小
     * 
     * @param maxSize
     *            缓存大小。单位：字节
     */
    public void setMemoryCacheSize(int maxSize) {
        if (mMemoryCache != null) {
            mMemoryCache.setMaxSize(maxSize);
        }
    }

    /**
     * 设置磁盘缓存大小
     * 
     * @param maxSize
     *            缓存大小。单位：字节
     */
    public void setDiskCacheSize(int maxSize) {
        if (mDiskLruCache != null) {
            mDiskLruCache.setMaxSize(maxSize);
        }
    }

    // ////////////////////////////////////////下载图片，会保存在磁盘///////////////////////////////////////////////////
    /**
     * 从地址中加载图片，并缓存到内存或者磁盘
     * 
     * @param uri
     *            图片地址，可以是网络地址或者磁盘地址（'/'开头）
     * @param config
     *            加载回调配置
     * @return
     */
    public Bitmap downloadBitmap(String uri, BitmapDisplayConfig config) {
        BitmapMeta bitmapMeta = new BitmapMeta();

        OutputStream outputStream = null;
        LruDiskCache.Snapshot snapshot = null;
        try {
            /* 如果有开启磁盘缓存，下载到磁盘 */
            if (globalConfig.isDiskCacheEnabled()) {
                synchronized (mDiskCacheLock) {
                    // 等待直到磁盘缓存被初始化完毕
                    while (!isDiskCacheReadied) {
                        try {
                            mDiskCacheLock.wait();
                        }
                        catch (InterruptedException e) {
                            // 被中断可继续往下操作
                        }
                    }

                    if (null != mDiskLruCache) {
                        snapshot = mDiskLruCache.get(uri);
                        if (null == snapshot) {
                            LruDiskCache.Editor editor = mDiskLruCache.edit(uri);
                            if (null != editor) {
                                outputStream = editor.newOutputStream(DISK_CACHE_INDEX);
                                bitmapMeta.expiryTimestamp = globalConfig.getDownloader().downloadToStream(uri,
                                        outputStream, config.getDownloaderCallBack());
                                if (bitmapMeta.expiryTimestamp < 0) {
                                    editor.abort();
                                    return null;// 下载失败
                                }
                                else {
                                    editor.setEntryExpiryTimestamp(bitmapMeta.expiryTimestamp);
                                    editor.commit();
                                }
                                snapshot = mDiskLruCache.get(uri);
                            }
                        }

                        if (null != snapshot) {
                            bitmapMeta.inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
                        }
                    }
                }
            }

            /* 如果磁盘缓存没有开启，图片就直接下载到内存中 */
            if (!globalConfig.isDiskCacheEnabled() || mDiskLruCache == null || bitmapMeta.inputStream == null) {
                outputStream = new ByteArrayOutputStream();
                bitmapMeta.expiryTimestamp = globalConfig.getDownloader().downloadToStream(uri, outputStream,
                        config.getDownloaderCallBack());
                if (bitmapMeta.expiryTimestamp < 0) {
                    return null;
                }
                else {
                    bitmapMeta.data = ((ByteArrayOutputStream) outputStream).toByteArray();
                }
            }

            return addBitmapToMemoryCache(uri, config, bitmapMeta);
        }
        catch (Exception e) {
            LogUtils.e(e.getMessage(), e);
        }
        finally {
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(snapshot);
        }

        return null;
    }

    /**
     * 把图片缓存到内存缓存里
     * 
     * @param uri
     *            缓存图片地址
     * @param config
     *            显示图片规格配置
     * @param bitmapMeta
     *            图片内容
     * @return
     * @throws IOException
     */
    private Bitmap addBitmapToMemoryCache(String uri, BitmapDisplayConfig config, BitmapMeta bitmapMeta)
            throws IOException {
        if (uri == null || bitmapMeta == null) {
            return null;
        }

        Bitmap bitmap = null;
        try {
            if (bitmapMeta.inputStream != null) {
                /* 表示开启了磁盘缓存，然后网络上的图片直接下载到了本地磁盘，故这里保存的是输入流 */
                if (config.isShowOriginal()) {
                    bitmap = BitmapFactory.decodeFileDescriptor(bitmapMeta.inputStream.getFD());
                }
                else {
                    bitmap = BitmapDecoder.decodeSampledBitmapFromDescriptor(bitmapMeta.inputStream.getFD(),
                            config.getBitmapMaxWidth(), config.getBitmapMaxHeight(), config.getBitmapConfig());
                }
            }
            else if (bitmapMeta.data != null) {
                /* 表示没有开启磁盘缓存，开启了内存缓存，所以图片直接下载到了data内存中，故图片从data内存里获取 */
                if (config.isShowOriginal()) {
                    bitmap = BitmapFactory.decodeByteArray(bitmapMeta.data, 0, bitmapMeta.data.length);
                }
                else {
                    bitmap = BitmapDecoder.decodeSampledBitmapFromByteArray(bitmapMeta.data,
                            config.getBitmapMaxWidth(), config.getBitmapMaxHeight(), config.getBitmapConfig());
                }
            }
            else {
                /* 表示磁盘缓存和内存缓存都没有开启，不建议这样 */
                bitmap = null;
            }
        }
        catch (OutOfMemoryError e) {
            LogUtils.e("读取图片内存溢出，原因：" + e.getMessage(), e);
        }

        if (null == bitmap) {
            return null;
        }

        /* 如果图片要进行圆角处理，就先进行圆角处理 */
        if (config.getRoundPx() > 0) {
            bitmap = BitmapUtils.getRoundedCornerBitmap(bitmap, config.getRoundPx());
        }

        /* 把图片添加到内存缓存中去 */
        String key = uri + config.toString();
        if (globalConfig.isMemoryCacheEnabled() && null != mMemoryCache) {
            // 保存同一下载地址的不同内存缓存key，供刷新缓存使用
            ArrayList<String> keyList = uri2keyListMap.get(uri);
            if (null == keyList) {
                keyList = new ArrayList<String>();
                uri2keyListMap.put(uri, keyList);
            }
            keyList.add(key);

            mMemoryCache.put(key, new SoftReference<Bitmap>(bitmap), bitmapMeta.expiryTimestamp);
        }

        return bitmap;
    }

    /**
     * 从内存缓存中获取图片
     * 
     * @param uri
     *            缓存图片地址
     * @param config
     *            显示规格配置
     * @return
     */
    public Bitmap getBitmapFromMemCache(String uri, BitmapDisplayConfig config) {
        String key = uri + config.toString();
        if (mMemoryCache != null) {
            SoftReference<Bitmap> softRef = mMemoryCache.get(key);
            return softRef == null ? null : softRef.get();
        }
        return null;
    }

    /**
     * 从磁盘中读取图片，注意磁盘中存放的图片都是原图，在读取到内存中时才会根据config参数进行压缩处理
     * 
     * @param uri
     *            缓存图片地址
     * @param config
     *            显示规格配置
     * @return
     */
    public Bitmap getBitmapFromDiskCache(String uri, BitmapDisplayConfig config) {
    	if (!globalConfig.isDiskCacheEnabled()) {
            return null;
        }

        synchronized (mDiskCacheLock) {
            while (!isDiskCacheReadied) {
                try {
                    mDiskCacheLock.wait();
                }
                catch (InterruptedException e) {
                }
            }

            if (mDiskLruCache != null) {
                LruDiskCache.Snapshot snapshot = null;
                try {
                    snapshot = mDiskLruCache.get(uri);
                    if (snapshot != null) {

                        Bitmap bitmap = null;
                        try {
                            if (config.isShowOriginal()) {
                                bitmap = BitmapFactory.decodeFileDescriptor(snapshot.getInputStream(DISK_CACHE_INDEX)
                                        .getFD());
                            }
                            else {
                                bitmap = BitmapDecoder.decodeSampledBitmapFromDescriptor(
                                        snapshot.getInputStream(DISK_CACHE_INDEX).getFD(), config.getBitmapMaxWidth(),
                                        config.getBitmapMaxHeight(), config.getBitmapConfig());
                            }
                        }
                        catch (OutOfMemoryError e) {
                            LogUtils.e("解析图片内存溢出OOM错误，原因：" + e.getMessage(), e);
                        }

                        /* 图片再磁盘缓存读取不到，直接返回null */
                        if (null == bitmap) {
                            return null;
                        }

                        /* 如果图片显示需要圆角处理，进行圆角处理 */
                        if (config.getRoundPx() > 0) {
                            bitmap = BitmapUtils.getRoundedCornerBitmap(bitmap, config.getRoundPx());
                        }

                        /* 如果开启了内存缓存，读到磁盘图片时缓存到内存 */
                        String key = uri + config.toString();
                        if (globalConfig.isMemoryCacheEnabled() && null != mMemoryCache) {
                            mMemoryCache.put(key, new SoftReference<Bitmap>(bitmap),
                                    mDiskLruCache.getExpiryTimestamp(uri));
                        }

                        return bitmap;
                    }
                }
                catch (final IOException e) {
                    LogUtils.e("读取磁盘缓存图片IO异常，原因：" + e.getMessage(), e);
                }
                finally {
                    IOUtils.closeQuietly(snapshot);
                }
            }
            return null;
        }
    }

    // ///////////////////////////////////////////清理缓存部分/////////////////////////////////////////////////////////
    /**
     * 清理所有缓存
     */
    public void clearCache() {
        clearMemoryCache();
        clearDiskCache();
    }

    /**
     * 清理所有内存缓存
     */
    public void clearMemoryCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
            uri2keyListMap.clear();
        }
    }

    /**
     * 清理所有磁盘缓存
     */
    public void clearDiskCache() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
                try {
                    mDiskLruCache.delete();
                }
                catch (IOException e) {
                    LogUtils.e("清理磁盘缓存异常，原因：" + e.getMessage(), e);
                }
                mDiskLruCache = null;
                isDiskCacheReadied = false;
            }
        }
        initDiskCache();
    }

    /**
     * 清理指定的内存缓存和磁盘缓存
     * 
     * @param uri
     *            缓存的图片地址
     */
    public void clearCache(String uri) {
        clearMemoryCache(uri);
        clearDiskCache(uri);
    }

    /**
     * 清理指定的内存缓存
     * 
     * @param uri
     *            缓存的图片地址
     */
    public void clearMemoryCache(String uri) {
        ArrayList<String> keyList = uri2keyListMap.get(uri);
        if (Validators.isEmpty(keyList)) {
            return;
        }

        if (null == mMemoryCache) {
            return;
        }

        // 遍历删除该地址下的所有缓存
        for (String key : keyList) {
            mMemoryCache.remove(key);
        }
        uri2keyListMap.remove(uri);
    }

    /**
     * 清理指定的磁盘缓存
     * 
     * @param uri
     *            缓存的图片地址
     */
    public void clearDiskCache(String uri) {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
                try {
                    mDiskLruCache.remove(uri);
                }
                catch (IOException e) {
                    LogUtils.e("清理[" + uri + "]磁盘缓存IO异常，原因：" + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * flush磁盘缓存
     */
    public void flush() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    mDiskLruCache.flush();
                }
                catch (IOException e) {
                    LogUtils.e("flush磁盘缓存IO异常，原因：" + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 关闭内存缓存和磁盘缓存，关闭后，缓存要重新初始化，否则不可用
     */
    public void close() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    if (!mDiskLruCache.isClosed()) {
                        mDiskLruCache.close();
                        mDiskLruCache = null;
                    }
                }
                catch (IOException e) {
                    LogUtils.e("关闭缓存IO异常，原因：" + e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 图片内容封装<br>
     * 如果启用了磁盘缓存，那么从网络上下载图片是直接下载到磁盘上的，所有保存的是inputStream<br>
     * 如果没有启用磁盘缓存，只启用了内存缓存，那么从网络上下载图片是直接下载到内存中，所有保存的是data数据
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-17 下午3:10:29 $
     */
    private class BitmapMeta {
        public FileInputStream inputStream;
        public byte[] data;
        public long expiryTimestamp;
    }

}
