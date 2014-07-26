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
import com.winupon.andframe.bigapple.utils.Validators;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 图片缓存对象，打包了内存缓存和磁盘缓存
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
                // Ignore
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
    public void setMemoryCacheSize(int maxSize) {
        if (mMemoryCache != null) {
            mMemoryCache.setMaxSize(maxSize);
        }
    }

    public void setDiskCacheSize(int maxSize) {
        if (mDiskLruCache != null) {
            mDiskLruCache.setMaxSize(maxSize);
        }
    }

    // ////////////////////////////////////////下载图片，会保存在磁盘///////////////////////////////////////////////////
    public Bitmap downloadBitmap(String uri, BitmapDisplayConfig config) {
        BitmapMeta bitmapMeta = new BitmapMeta();

        OutputStream outputStream = null;
        LruDiskCache.Snapshot snapshot = null;
        try {
            // 如果有开启磁盘缓存，下载到磁盘
            if (globalConfig.isDiskCacheEnabled()) {
                synchronized (mDiskCacheLock) {
                    // Wait for disk cache to initialize
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

            // 从网络中下载，内容直接下载到内存中
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
     * 图片放到内存缓存里
     * 
     * @param uri
     * @param config
     * @param bitmapMeta
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
                // 从本地磁盘中读取图片
                if (config.isShowOriginal()) {
                    bitmap = BitmapFactory.decodeFileDescriptor(bitmapMeta.inputStream.getFD());
                }
                else {
                    bitmap = BitmapDecoder.decodeSampledBitmapFromDescriptor(bitmapMeta.inputStream.getFD(),
                            config.getBitmapMaxWidth(), config.getBitmapMaxHeight(), config.getBitmapConfig());
                }
            }
            else if (bitmapMeta.data != null) {
                // 从内存中读取图片
                if (config.isShowOriginal()) {
                    bitmap = BitmapFactory.decodeByteArray(bitmapMeta.data, 0, bitmapMeta.data.length);
                }
                else {
                    bitmap = BitmapDecoder.decodeSampledBitmapFromByteArray(bitmapMeta.data,
                            config.getBitmapMaxWidth(), config.getBitmapMaxHeight(), config.getBitmapConfig());
                }
            }
            else {
                bitmap = null;
            }
        }
        catch (OutOfMemoryError e) {
            LogUtils.e("读取图片内存溢出，原因：" + e);
        }

        if (bitmap == null) {
            return null;
        }

        // 添加到内存缓存
        String key = uri + config.toString();
        if (globalConfig.isMemoryCacheEnabled() && mMemoryCache != null) {
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
     * @param config
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
     * 获取硬盘缓存
     * 
     * @param uri
     * @param config
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
                            LogUtils.e("解析图片内存溢出OOM错误，原因：" + e);
                        }

                        // add to memory cache
                        String key = uri + config.toString();
                        if (globalConfig.isMemoryCacheEnabled() && mMemoryCache != null && bitmap != null) {
                            mMemoryCache.put(key, new SoftReference<Bitmap>(bitmap),
                                    mDiskLruCache.getExpiryTimestamp(uri));
                        }

                        return bitmap;
                    }
                }
                catch (final IOException e) {
                    LogUtils.e(e.getMessage(), e);
                }
                finally {
                    IOUtils.closeQuietly(snapshot);
                }
            }
            return null;
        }
    }

    // ///////////////////////////////////////////清理缓存部分/////////////////////////////////////////////////////////
    public void clearCache() {
        clearMemoryCache();
        clearDiskCache();
    }

    public void clearMemoryCache() {
        if (mMemoryCache != null) {
            mMemoryCache.evictAll();
            uri2keyListMap.clear();
        }
    }

    public void clearDiskCache() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
                try {
                    mDiskLruCache.delete();
                }
                catch (IOException e) {
                    LogUtils.e(e.getMessage(), e);
                }
                mDiskLruCache = null;
                isDiskCacheReadied = false;
            }
        }
        initDiskCache();
    }

    public void clearCache(String uri) {
        clearMemoryCache(uri);
        clearDiskCache(uri);
    }

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

    public void clearDiskCache(String uri) {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null && !mDiskLruCache.isClosed()) {
                try {
                    mDiskLruCache.remove(uri);
                }
                catch (IOException e) {
                    LogUtils.e(e.getMessage(), e);
                }
            }
        }
    }

    public void flush() {
        synchronized (mDiskCacheLock) {
            if (mDiskLruCache != null) {
                try {
                    mDiskLruCache.flush();
                }
                catch (IOException e) {
                    LogUtils.e(e.getMessage(), e);
                }
            }
        }
    }

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
                    LogUtils.e(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * 图片封装
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-17 下午3:10:29 $
     */
    private class BitmapMeta {
        /**
         * 图片的内容，要么从inputStream中获取（使用本地缓存），要么从data中获取（不使用缓存，直接从网络下载到内存中）
         */
        public FileInputStream inputStream;
        public byte[] data;
        public long expiryTimestamp;
    }

}
