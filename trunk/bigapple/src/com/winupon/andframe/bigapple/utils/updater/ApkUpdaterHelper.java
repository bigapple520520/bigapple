/* 
 * @(#)ApkUpdaterUtils.java    Created on 2015-3-6
 * Copyright (c) 2015 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.updater;

import java.io.File;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

/**
 * 下载更新APK工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2015-3-6 下午5:17:06 $
 */
public class ApkUpdaterHelper {
    /** 下载器 */
    private DownloadHelper mDownloadHelper;
    private Handler handler = new Handler(Looper.getMainLooper());

    public ApkUpdaterHelper(DownloadHelper downloadHelper) {
        this.mDownloadHelper = downloadHelper;
    }

    /**
     * 安装Apk
     * 
     * @param context
     * @param fileName
     *            本地APK地址
     * @return
     */
    public static boolean installApk(Context context, String fileName) {
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
     * 下载APK，或显示进度，或自动安装，根据config设定
     * 
     * @param context
     * @param apkUrl
     * @param saveFileName
     * @param downloadListener
     * @param config
     */
    public void download(final Context context, final String apkUrl, final String saveFileName,
            final DownloadListener downloadListener, final ApkUpdateConfig config) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ProgressDialog temp = null;
                if (config.canShowProgress) {
                    // 如果需要显示进度条，就初始化进度条
                    temp = new ProgressDialog(context);
                    temp.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    temp.setTitle(config.progressTitle);
                    temp.setCancelable(config.progressCancelable);
                    temp.setCanceledOnTouchOutside(false);
                    temp.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            boolean isUserDone = false;
                            if (null != config.getProgressListener()) {
                                isUserDone = config.getProgressListener().cancel(dialogInterface);
                            }
                            if (!isUserDone) {
                                mDownloadHelper.stopDownload();// 默认取消进度条就是终止下载
                            }
                        }
                    });
                }

                final ProgressDialog pd = temp;
                mDownloadHelper.downloadApk(apkUrl, saveFileName, new DownloadListener() {
                    @Override
                    public void downloadStart() {
                        if (null != downloadListener) {
                            downloadListener.downloadStart();
                        }
                        if (null != pd) {
                            pd.show();
                        }
                    }

                    @Override
                    public void downloadStop(String saveFilename) {
                        if (null != downloadListener) {
                            downloadListener.downloadStop(saveFileName);
                        }
                        if (null != pd) {
                            pd.dismiss();
                        }
                    }

                    @Override
                    public void downloadProgress(int progress) {
                        if (null != downloadListener) {
                            downloadListener.downloadProgress(progress);
                        }
                        if (null != pd) {
                            pd.setProgress(progress);
                        }
                    }

                    @Override
                    public void downloadFinish(String saveFilename) {
                        if (null != downloadListener) {
                            downloadListener.downloadFinish(saveFileName);
                        }
                        if (null != pd) {
                            pd.dismiss();
                        }
                        if (config.isAutoInstall) {
                            // 自动安装
                            installApk(context, saveFileName);
                        }
                    }

                    @Override
                    public void downloadError(Throwable e, String message) {
                        if (null != downloadListener) {
                            downloadListener.downloadError(e, message);
                        }
                        if (null != pd) {
                            pd.dismiss();
                        }
                    }
                });
            }
        });
    }

    /**
     * 下载APK，或显示确认框
     * 
     * @param context
     *            上下文
     * @param apkUrl
     *            apk网络地址
     * @param saveFileName
     *            apk本地保存地址
     * @param apkUpdaterListener
     *            下载监听
     * @param config
     *            配置参数
     */
    public void downloadWithConfirm(final Context context, final String apkUrl, final String saveFileName,
            final DownloadListener apkUpdaterListener, final ApkUpdateConfig config) {
        if (config.canShowAlertDialog) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    // 是否需要下载安装提示
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setPositiveButton(config.alertDialogOkBtnText, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    boolean isUserDone = false;
                                    if (null != config.getAlertDialogListener()) {
                                        isUserDone = config.getAlertDialogListener().onChoiceOk();
                                    }

                                    if (!isUserDone) {
                                        download(context, apkUrl, saveFileName, apkUpdaterListener, config);
                                    }
                                }
                            })
                            .setNegativeButton(config.alertDialogCancelBtnText, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    if (null != config.getAlertDialogListener()) {
                                        config.getAlertDialogListener().onChoiceCancel();
                                    }
                                }
                            }).create();
                    alertDialog.setTitle(config.alertDialogTitle);
                    alertDialog.setMessage(config.alertDialogMessage);
                    alertDialog.setCancelable(config.alertDialogCancelable);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            });
        }
        else {
            // 不提示，直接下载
            download(context, apkUrl, saveFileName, apkUpdaterListener, config);
        }

    }

    /**
     * 停止任务
     */
    public void stopDownload() {
        mDownloadHelper.stopDownload();
    }

}
