/* 
 * @(#)ApkUpdaterUtils.java    Created on 2015-3-6
 * Copyright (c) 2015 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.winupon.andframe.bigapple.utils.updater.ApkUpdaterListener.StateEnum;

/**
 * 下载更新APK工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2015-3-6 下午5:17:06 $
 */
public class ApkUpdaterHelper {
    public static final boolean DEBUG = true;
    private static final String TAG = "ApkUpdaterUtils";
    private static final int BUFFER_SIZE = 1024;

    private volatile boolean stopFlag = false;// 停止下载标记
    private volatile boolean pauseFlag = false;// 暂停下载标记

    /**
     * 安装Apk
     * 
     * @param context
     * @param fileName
     *            本地APK地址
     * @return
     */
    public boolean installApk(Context context, String fileName) {
        File apkfile = new File(fileName);
        if (!apkfile.exists()) {
            return false;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
        return true;
    }

    /**
     * 下载APK
     * 
     * @param apkUrl
     *            网络下载地址
     * @param apkFilename
     *            本地存储地址
     * @param apkUpdaterListener
     *            下载监听
     */
    public void downloadApk(final String apkUrl, final String saveFileName, final ApkUpdaterListener apkUpdaterListener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpGet getMethod = new HttpGet(apkUrl);
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(getMethod);
                    HttpEntity httpEntity = response.getEntity();
                    InputStream inputStream = httpEntity.getContent();
                    long length = httpEntity.getContentLength();

                    // 创建件文件夹
                    File apkFile = new File(saveFileName);
                    File parentFile = apkFile.getParentFile();
                    if (!parentFile.exists()) {
                        boolean success = parentFile.mkdirs();
                        if (!success) {
                            if (DEBUG) {
                                Log.e(TAG, "Mkdirs failed");
                            }
                        }
                    }

                    // 创建文件
                    if (!apkFile.exists()) {
                        apkFile.createNewFile();
                    }

                    FileOutputStream fos = new FileOutputStream(apkFile);

                    // 从输入流中读取字节数据，写到文件中
                    int count = 0;
                    byte buf[] = new byte[BUFFER_SIZE];
                    int progress = 0;
                    do {
                        int numread = inputStream.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);
                        if (null != apkUpdaterListener) {
                            apkUpdaterListener.downloadProgress(progress, StateEnum.DOWNLOAD_ING);
                        }

                        if (numread <= 0) {
                            // 下载完成
                            apkUpdaterListener.downloadProgress(progress, StateEnum.DOWNLOAD_FINISH);
                            break;
                        }

                        fos.write(buf, 0, numread);
                    }
                    while (!stopFlag);

                    // 取消下载
                    if (stopFlag) {
                        apkUpdaterListener.downloadProgress(progress, StateEnum.DOWNLOAD_STOP);
                        stopFlag = false;
                    }

                    fos.close();
                    inputStream.close();
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    apkUpdaterListener.downloadProgress(-1, StateEnum.DOWNLOAD_ERROR);
                }
            }
        }).start();
    }

    /**
     * 停止下载，他停止后不允许再恢复
     */
    public void stopDownload() {
        this.stopFlag = true;
    }

    /**
     * 暂停下载
     */
    public void pauseDownload() {
        this.pauseFlag = true;
    }

    /**
     * 重新开始开在
     */
    public void restartDownload() {
        this.pauseFlag = false;
    }

}
