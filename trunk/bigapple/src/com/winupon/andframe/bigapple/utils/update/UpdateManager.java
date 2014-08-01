/* 
 * @(#)UpdateManager.java    Created on 2011-12-20
 * Copyright (c) 2011 ZDSoft Networks, Inc. All rights reserved.
 * $Id: UpdateManager.java 35745 2013-03-12 01:20:28Z xuan $
 */
package com.winupon.andframe.bigapple.utils.update;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.winupon.andframe.bigapple.utils.update.helper.UpdateConfig;

/**
 * 更新应用的工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-3-25 上午9:28:20 $
 */
public class UpdateManager {
    private static final String TAG = "bigapple.UpdateManager";

    private static final int BUFFER_SIZE = 1024;

    @Deprecated
    private NotifyCanGotoListener notifyCanGotoListener;

    private UpdateOkListener updateOkListener;// 确定更新事件
    private UpdateCancelListener updateCancelListener;// 取消更新事件
    private DownloadProgressListener downloadProgressListener;
    private DownloadFinishListener downloadFinishListener;

    private static final int DOWN_UPDATE = 1;// 正在下载标识
    private static final int DOWN_OVER = 2;// 下载完成标识
    private static final int DOWN_CANCEL = 3;// 取消下载

    private final Context context;

    private UpdateConfig updateConfig;// 下载配置参数
    private ProgressDialog updateProgress;// 更新进度条
    private int progress;// 进度值

    private boolean interceptFlag = false;// 是否取消下载

    private final Handler handler = new Handler() {// 更新下载进度条的handler
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case DOWN_UPDATE:
                updateProgress.setProgress(progress);
                if (null != downloadProgressListener) {
                    downloadProgressListener.onProgress(progress);
                }
                break;
            case DOWN_OVER:
                updateProgress.dismiss();
                if (null != downloadFinishListener) {
                    downloadFinishListener.downloadFinish(updateConfig.getSaveFileName());
                }

                if (updateConfig.isAutoInstall()) {
                    installApk();
                }
                break;
            case DOWN_CANCEL:
                updateProgress.dismiss();
                if (null != updateCancelListener) {
                    updateCancelListener.updateCancel(new CancelEvent(CancelEvent.DOWNLOAD_ING_CANCEL));
                }
                break;
            }
        };
    };

    public UpdateManager(Context context) {
        this.context = context;
    }

    @Deprecated
    public UpdateManager(Context context, UpdateConfig updateConfig) {
        this.context = context;
        this.updateConfig = updateConfig;
    }

    // /////////////////////////////////////////提示用户确定或者取消更新/////////////////////////////////////////////////
    /**
     * 提示更新
     * 
     * @param apkUrl
     *            apk下载地址
     * @param saveFileName
     *            apk保存路径
     */
    public void doUpdate(String apkUrl, String saveFileName) {
        doUpdate(apkUrl, saveFileName, null);
    }

    /**
     * 提示更新
     * 
     * @param apkUrl
     *            apk下载地址
     * @param saveFileName
     *            apk保存路径
     * @param updateText
     *            提示框内容
     */
    public void doUpdate(String apkUrl, String saveFileName, String updateText) {
        if (TextUtils.isEmpty(apkUrl)) {
            return;
        }

        UpdateConfig updateConfig = new UpdateConfig();
        updateConfig.setApkUrl(apkUrl);

        if (!TextUtils.isEmpty(saveFileName)) {
            updateConfig.setSaveFileName(saveFileName);
        }

        if (!TextUtils.isEmpty(updateText)) {
            updateConfig.setUpdateText(updateText);
        }

        doUpdate(updateConfig);
    }

    /**
     * 提示更新
     * 
     * @param updateConfig
     *            配置参数
     */
    public void doUpdate(UpdateConfig updateConfig) {
        if (null == updateConfig) {
            return;
        }

        this.updateConfig = updateConfig;
        showNoticeDialog();
    }

    @Deprecated
    public void doUpdate() {
        showNoticeDialog();
    }

    // /////////////////////////////////////////直接下载不用安装//////////////////////////////////////////
    public void doDownload(String apkUrl, String saveFileName) {
        if (TextUtils.isEmpty(apkUrl)) {
            return;
        }

        UpdateConfig updateConfig = new UpdateConfig();
        updateConfig.setApkUrl(apkUrl);
        updateConfig.setAutoInstall(false);

        if (!TextUtils.isEmpty(saveFileName)) {
            updateConfig.setSaveFileName(saveFileName);
        }

        doDownloadInstall(updateConfig);
    }

    // /////////////////////////////////////////直接下载安装/////////////////////////////////////////////////
    /**
     * 下载安装
     * 
     * @param apkUrl
     *            apk下载地址
     * @param saveFileName
     *            apk保存路径
     */
    public void doDownloadInstall(String apkUrl, String saveFileName) {
        if (TextUtils.isEmpty(apkUrl)) {
            return;
        }

        UpdateConfig updateConfig = new UpdateConfig();
        updateConfig.setApkUrl(apkUrl);

        if (!TextUtils.isEmpty(saveFileName)) {
            updateConfig.setSaveFileName(saveFileName);
        }

        doDownloadInstall(updateConfig);
    }

    /**
     * 下载安装
     * 
     * @param updateConfig
     *            配置参数
     */
    public void doDownloadInstall(UpdateConfig updateConfig) {
        if (null == updateConfig) {
            return;
        }

        this.updateConfig = updateConfig;
        update();
    }

    @Deprecated
    public void doDownload() {
        update();
    }

    /**
     * 取消下载
     */
    public void cancel() {
        interceptFlag = true;
    }

    // //////////////////////////////////////内部辅助方法////////////////////////////////////////////////////////////////
    private void showNoticeDialog() {
        AlertDialog.Builder builder = new Builder(context);
        builder.setTitle(updateConfig.getUpdateTitle());
        builder.setMessage(updateConfig.getUpdateText());
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
                if (null != notifyCanGotoListener) {
                    notifyCanGotoListener.notifyCanGoto();
                }

                // 用户取消
                if (null != updateCancelListener) {
                    updateCancelListener.updateCancel(new CancelEvent(CancelEvent.USER_CANCEL));
                }
            }
        });
        builder.setPositiveButton(updateConfig.getPositiveBtnText(), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                boolean isUpdate = true;
                if (null != updateOkListener) {
                    isUpdate = updateOkListener.updateOk();
                }

                if (isUpdate) {
                    update();// 操作更新
                }
            }
        }).setNegativeButton(updateConfig.getNegativeBtnText(), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (null != notifyCanGotoListener) {
                    notifyCanGotoListener.notifyCanGoto();
                }

                // 用户取消
                if (null != updateCancelListener) {
                    updateCancelListener.updateCancel(new CancelEvent(CancelEvent.USER_CANCEL));
                }
            }
        });

        builder.create().show();
    }

    // 实际的更新操作
    public void update() {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Toast.makeText(context, "SD卡不可用，无法下载，请安装SD卡后再试。", Toast.LENGTH_SHORT).show();
            if (null != notifyCanGotoListener) {
                notifyCanGotoListener.notifyCanGoto();
            }

            // SD卡不可用
            if (null != updateCancelListener) {
                updateCancelListener.updateCancel(new CancelEvent(CancelEvent.SDCARD_DISABLED));
            }
            return;
        }

        updateProgress = new ProgressDialog(context);
        updateProgress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        updateProgress.setTitle(updateConfig.getProgressText());
        updateProgress.setCancelable(updateConfig.isCanCancel());
        updateProgress.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                cancel();
            }
        });
        updateProgress.show();

        downloadApk();
    }

    // 启动线程下载apk
    private void downloadApk() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpGet getMethod = new HttpGet(updateConfig.getApkUrl());
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpResponse response = httpClient.execute(getMethod);
                    HttpEntity httpEntity = response.getEntity();
                    InputStream inputStream = httpEntity.getContent();
                    long length = httpEntity.getContentLength();

                    // 创建件文件夹
                    File apkFile = new File(updateConfig.getSaveFileName());
                    File parentFile = apkFile.getParentFile();
                    if (!parentFile.exists()) {
                        boolean success = parentFile.mkdirs();
                        if (!success) {
                            Log.e(TAG, "mkdirs failed");
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
                    do {
                        int numread = inputStream.read(buf);
                        count += numread;
                        progress = (int) (((float) count / length) * 100);

                        // 更新进度
                        handler.sendEmptyMessage(DOWN_UPDATE);

                        if (numread <= 0) {
                            // 下载完成通知安装
                            handler.sendEmptyMessage(DOWN_OVER);
                            break;
                        }

                        fos.write(buf, 0, numread);
                    }
                    while (!interceptFlag);

                    // 取消下载
                    if (interceptFlag) {
                        handler.sendEmptyMessage(DOWN_CANCEL);
                        interceptFlag = false;
                    }

                    fos.close();
                    inputStream.close();
                }
                catch (Exception e) {
                    Log.e(TAG, "", e);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "下载包时发生错误。", Toast.LENGTH_SHORT).show();
                            updateProgress.dismiss();
                            if (null != notifyCanGotoListener) {
                                notifyCanGotoListener.notifyCanGoto();
                            }

                            // 因外力因素而无法下载更新
                            if (null != updateCancelListener) {
                                updateCancelListener.updateCancel(new CancelEvent(CancelEvent.DOWNLOAD_FAIL));
                            }
                        }
                    });
                }
            }
        }).start();
    }

    // 安装apk
    private void installApk() {
        File apkfile = new File(updateConfig.getSaveFileName());
        if (!apkfile.exists()) {
            if (null != updateCancelListener) {
                updateCancelListener.updateCancel(new CancelEvent(CancelEvent.INSTALL_FAIL));
            }
            return;
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    @Deprecated
    public interface NotifyCanGotoListener {

        /**
         * 可以进行跳转
         */
        @Deprecated
        public void notifyCanGoto();
    }

    @Deprecated
    public void setNotifyCanGotoListener(NotifyCanGotoListener notifyCanGotoListener) {
        this.notifyCanGotoListener = notifyCanGotoListener;
    }

    public void setUpdateOkListener(UpdateOkListener updateOkListener) {
        this.updateOkListener = updateOkListener;
    }

    public void setUpdateCancelListener(UpdateCancelListener updateCancelListener) {
        this.updateCancelListener = updateCancelListener;
    }

    public void setDownloadProgressListener(DownloadProgressListener downloadProgressListener) {
        this.downloadProgressListener = downloadProgressListener;
    }

    public void setDownloadFinishListener(DownloadFinishListener downloadFinishListener) {
        this.downloadFinishListener = downloadFinishListener;
    }

}
