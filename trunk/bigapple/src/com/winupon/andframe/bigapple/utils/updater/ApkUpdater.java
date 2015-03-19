/* 
 * @(#)Updater.java    Created on 2015-3-6
 * Copyright (c) 2015 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.updater;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.winupon.andframe.bigapple.utils.Validators;

/**
 * 主要可以用来下载更新APK
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2015-3-6 下午5:12:16 $
 */
public class ApkUpdater {
    private static final String TAG = "ApkUpdater";
    private ApkUpdaterHelper mApkUpdaterHelper;

    /** 默认保存的apk路径 */
    public static String DEFUALT_SAVE_FILENAME = Environment.getExternalStorageDirectory().getPath()
            + "/bigapple/bigapple-default.apk";

    private ApkUpdater(ApkUpdaterHelper apkUpdaterHelper) {
        this.mApkUpdaterHelper = apkUpdaterHelper;
    }

    /**
     * 更新APK
     * 
     * @param context
     * @param apkUrl
     *            下载地址
     * @param saveFileName
     *            本地保存地址
     * @param downloadListener
     *            下载监听
     * @param config
     *            配置参数
     * @return
     */
    public static ApkUpdater update(Context context, String apkUrl, String saveFileName,
            DownloadListener downloadListener, ApkUpdateConfig config) {
        if (null == context) {
            Log.e(TAG, "Context Can not be null.");
            return null;
        }

        if (Validators.isEmpty(apkUrl)) {
            Log.e(TAG, "SaveFileName Can not be empty.");
            return null;
        }

        if (Validators.isEmpty(saveFileName)) {
            saveFileName = DEFUALT_SAVE_FILENAME;
        }

        if (null == config) {
            config = new ApkUpdateConfig();
        }

        ApkUpdater apkUpdater = new ApkUpdater(new ApkUpdaterHelper(new DownloadHelper()));
        apkUpdater.mApkUpdaterHelper.downloadWithConfirm(context, apkUrl, saveFileName, downloadListener, config);
        return apkUpdater;
    }

    /**
     * 更新APK
     * 
     * @param context
     * @param apkUrl
     *            下载地址
     * @param saveFileName
     *            本地保存地址
     * @param apkUpdaterListener
     *            下载监听
     * @return
     */
    public ApkUpdater update(Context context, String apkUrl, String saveFileName, DownloadListener downloadListener) {
        return ApkUpdater.update(context, apkUrl, saveFileName, downloadListener, null);
    }

    /**
     * 更新APK
     * 
     * @param context
     * @param apkUrl
     *            下载地址
     * @param saveFileName
     *            本地保存地址
     * @param config
     *            配置参数
     * @return
     */
    public ApkUpdater update(Context context, String apkUrl, String saveFileName, ApkUpdateConfig config) {
        return ApkUpdater.update(context, apkUrl, saveFileName, null, config);
    }

    /**
     * 更新APK
     * 
     * @param context
     * @param apkUrl
     *            下载地址
     * @param saveFileName
     *            本地保存地址
     * @return
     */
    public ApkUpdater update(Context context, String apkUrl, String saveFileName) {
        return ApkUpdater.update(context, apkUrl, saveFileName, null, null);
    }

    /**
     * 更新APK
     * 
     * @param context
     * @param apkUrl
     *            下载地址
     * @param saveFileName
     *            本地保存地址
     * @return
     */
    public ApkUpdater update(Context context, String apkUrl) {
        return ApkUpdater.update(context, apkUrl, null, null, null);
    }

    /**
     * 停止下载，他停止后不允许再恢复
     */
    public void stopDownload() {
        mApkUpdaterHelper.stopDownload();
    }

}
