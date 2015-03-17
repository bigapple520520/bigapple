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

import com.winupon.andframe.bigapple.utils.Validators;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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
    private Handler handler = new Handler(Looper.getMainLooper());
    
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
    public void downloadApk(final String apkUrl, final String saveFilename, final ApkUpdaterListener apkUpdaterListener) {
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
                    File apkFile = new File(saveFilename);
                    File parentFile = apkFile.getParentFile();
                    if (!parentFile.exists()) {
                        boolean success = parentFile.mkdirs();
                        if (!success) {
                            if (DEBUG) {
                                Log.e(TAG, "Mkdirs failed");
                                postUI(new Runnable() {
									@Override
									public void run() {
										apkUpdaterListener.downloadError(null, "Mkdirs failed");
									}
								});
                            }
                        }
                    }

                    // 创建文件
                    if (!apkFile.exists()) {
                    	boolean success = apkFile.createNewFile();
                    	if (!success) {
                            if (DEBUG) {
                                Log.e(TAG, "Create file failed");
                                postUI(new Runnable() {
									@Override
									public void run() {
										apkUpdaterListener.downloadError(null, "Create file failed");
									}
								});
                            }
                        }
                    }

                    // 从输入流中读取字节数据，写到文件中
                    FileOutputStream fos = new FileOutputStream(apkFile);
                    int count = 0;
                    byte buf[] = new byte[BUFFER_SIZE];
                    do {
                        int numread = inputStream.read(buf);
                        count += numread;
                        final int progress = (int) (((float) count / length) * 100);
                        if (null != apkUpdaterListener) {
                        	postUI(new Runnable() {
								@Override
								public void run() {
									apkUpdaterListener.downloadProgress(progress);
								}
							});
                        }

                        if (numread <= 0) {
                            // 下载完成
                        	postUI(new Runnable() {
								@Override
								public void run() {
									apkUpdaterListener.downloadFinish(saveFilename);
								}
							});
                            break;
                        }

                        fos.write(buf, 0, numread);
                    }
                    while (!stopFlag);

                    // 取消下载
                    if (stopFlag) {
                    	postUI(new Runnable() {
							@Override
							public void run() {
								apkUpdaterListener.downloadStop(saveFilename);
							}
						});
                        stopFlag = false;
                    }

                    fos.close();
                    inputStream.close();
                }
                catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    apkUpdaterListener.downloadError(e, e.getMessage());
                }
            }
        }).start();
    }
    
    /**
     * 下载APK
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
    public void downloadWithProgressDialog(Context context, String apkUrl, String saveFileName, final ApkUpdaterListener apkUpdaterListener, ApkUpdateConfig config) {
        final ProgressDialog pd = config.cusTomProgressDialog;
        
    	ApkUpdaterHelper apkUpdaterHelper = new ApkUpdaterHelper();
        apkUpdaterHelper.downloadApk(apkUrl, saveFileName, new ApkUpdaterListener() {
			@Override
			public void downloadStop(String saveFilename) {
				if(null != apkUpdaterListener){
					apkUpdaterListener.downloadStop(saveFilename);
				}
				pd.dismiss();
			}
			
			@Override
			public void downloadProgress(int progress) {
				if(null != apkUpdaterListener){
					apkUpdaterListener.downloadProgress(progress);
				}
				pd.setProgress(progress);
			}
			
			@Override
			public void downloadFinish(String saveFilename) {
				if(null != apkUpdaterListener){
					apkUpdaterListener.downloadFinish(saveFilename);
				}
				pd.dismiss();
			}
			
			@Override
			public void downloadError(Throwable e, String message) {
				if(null != apkUpdaterListener){
					apkUpdaterListener.downloadError(e, message);
				}
				pd.dismiss();
			}
		});
    }

    /**
     * 停止下载，他停止后不允许再恢复
     */
    public void stopDownload() {
        this.stopFlag = true;
    }
    
    /**提交任务到主线程*/
    private void postUI(Runnable runnable){
    	handler.post(runnable);
    }

}
