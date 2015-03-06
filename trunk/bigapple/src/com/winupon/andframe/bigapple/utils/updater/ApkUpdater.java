/* 
 * @(#)Updater.java    Created on 2015-3-6
 * Copyright (c) 2015 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.updater;

import android.content.Context;
import android.util.Log;

import com.winupon.andframe.bigapple.utils.Validators;

/**
 * 主要可以用来下载更新APK
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2015-3-6 下午5:12:16 $
 */
public abstract class ApkUpdater {
    private static final String TAG = "ApkUpdater";

    /**
     * 下载APK
     * 
     * @param apkUrl
     *            apk网络地址
     * @param saveFileName
     *            apk本地保存地址
     * @param apkUpdaterListener
     *            下载监听
     */
    public static void download(String apkUrl, String saveFileName, ApkUpdaterListener apkUpdaterListener) {
        ApkUpdaterHelper apkUpdaterHelper = new ApkUpdaterHelper();
        apkUpdaterHelper.downloadApk(apkUrl, saveFileName, apkUpdaterListener);
    }

    /**
     * 下载APK
     * 
     * @param apkUrl
     *            apk网络地址
     * @param saveFileName
     *            apk本地保存地址
     */
    public static void download(String apkUrl, String saveFileName) {
        download(apkUrl, saveFileName, null);
    }

    /**
     * 安装Apk
     * 
     * @param context
     *            上下文
     * @param fileName
     *            本地apk地址
     */
    public static void install(Context context, String fileName) {
        if (Validators.isEmpty(fileName)) {
            Log.e(TAG, "FileName Can not be empty");
        }

        ApkUpdaterHelper apkUpdaterHelper = new ApkUpdaterHelper();
        if ("http".startsWith(fileName)) {

        }
        else {
            // 如果是本地就直接安装
            apkUpdaterHelper.installApk(context, fileName);
        }
    }

}
