/* 
 * @(#)ContextUtils.java    Created on 2012-5-7
 * Copyright (c) 2012 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * 判断网络或者SD等之类的工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-3-25 上午9:22:02 $
 */
public abstract class ContextUtils {

    /**
     * 判断是否存在网络连接
     * 
     * @param context
     * @return
     */
    public static boolean hasNetwork(Context context) {
        ConnectivityManager connectManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectManager.getActiveNetworkInfo();

        return null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    /**
     * 判断GPS是否打开
     * 
     * @param context
     * @return
     */
    public static boolean isGpsEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * SD卡是否可用
     * 
     * @return
     */
    public static boolean hasSdCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取SD的根目录
     * 
     * @return
     */
    public static String getSdCardPath() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 获取手机本身的内置存储，一般SD卡不存在的时候使用。 /data/data/程序包名/cache
     * 
     * @param context
     * @return
     */
    public static String getCacheDirPath(Context context) {
        return context.getCacheDir().getPath();
    }

    /**
     * 获取手机本身的内置存储。 /data/data/程序包名/files
     * 
     * @param context
     * @return
     */
    public static String getFileDirPath(Context context) {
        return context.getFilesDir().getPath();
    }

    /**
     * 获取SD默认缓存路径：/Android/data/程序包名/cache/
     * 
     * @param context
     * @return
     */
    public static String getExternalCacheDirPath(Context context) {
        return context.getExternalCacheDir().getPath();
    }

}
