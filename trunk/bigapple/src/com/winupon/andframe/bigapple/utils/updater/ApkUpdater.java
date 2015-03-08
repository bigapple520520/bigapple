/* 
 * @(#)Updater.java    Created on 2015-3-6
 * Copyright (c) 2015 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.updater;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
    private ApkUpdaterHelper apkUpdaterHelper;
    
    /**默认保存的apk路径*/
    public static String DEFUALT_SAVE_FILENAME = Environment.getExternalStorageDirectory().getPath()
            + "/bigapple/bigapple-default.apk";
    
    private ApkUpdater(ApkUpdaterHelper apkUpdaterHelper){
    	this.apkUpdaterHelper = apkUpdaterHelper;
    }

    ////////////////////////downloadWithProgressDialog///////////////////////////////
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
    public static ApkUpdater downloadWithProgressDialog(Context context, String apkUrl, String saveFileName, final ApkUpdaterListener apkUpdaterListener, ApkUpdateConfig config) {
    	if(null == context){
    		Log.e(TAG, "Context Can not be null.");
            return null;
    	}
    	
    	if(Validators.isEmpty(apkUrl)){
    		Log.e(TAG, "ApkUrl Can not be empty.");
            return null;
    	}
    	
        if (Validators.isEmpty(saveFileName)) {
            Log.e(TAG, "SaveFileName Can not be empty.");
            return null;
        }
    	
        if(null == config){
        	config = new ApkUpdateConfig();
        }
        
        //查看是否有自定义的progressDialog，没有就用系统自带的那个
        if(null == config.cusTomProgressDialog){
        	config.cusTomProgressDialog = new ProgressDialog(context);
        }
        
    	ApkUpdater apkUpdater = new ApkUpdater(new ApkUpdaterHelper());
    	apkUpdater.apkUpdaterHelper.downloadWithProgressDialog(context, apkUrl, saveFileName, apkUpdaterListener, config);
    	return apkUpdater;
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
     */
    public static ApkUpdater downloadWithProgressDialog(Context context, String apkUrl, String saveFileName, final ApkUpdaterListener apkUpdaterListener) {
    	return ApkUpdater.downloadWithProgressDialog(context, apkUrl, saveFileName, apkUpdaterListener, null);
    }
    
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
    	if(Validators.isEmpty(apkUrl)){
    		Log.e(TAG, "ApkUrl Can not be empty.");
            return;
    	}
    	
        if (Validators.isEmpty(saveFileName)) {
            Log.e(TAG, "SaveFileName Can not be empty.");
            return;
        }
    	
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
    	ApkUpdater.download(apkUrl, saveFileName, null);
    }
    
    /**
     * 下载APK
     * 
     * @param apkUrl
     *            apk网络地址
     * @param saveFileName
     *            apk本地保存地址
     */
    public static void download(String apkUrl) {
    	ApkUpdater.download(apkUrl, DEFUALT_SAVE_FILENAME, null);
    }

    /**
     * 安装Apk
     * 
     * @param context
     *            上下文
     * @param fileName
     *            本地apk地址
     */
    public static boolean install(Context context, String fileName) {
    	if(null == context){
    		Log.e(TAG, "Context Can not be null.");
            return false;
    	}
    	
        if (Validators.isEmpty(fileName)) {
            Log.e(TAG, "FileName Can not be empty.");
            return false;
        }

        ApkUpdaterHelper apkUpdaterHelper = new ApkUpdaterHelper();
        return apkUpdaterHelper.installApk(context, fileName);
    }
    
    /**
     * 下载后自动安装
     * 
     * @param context
     * @param apkUrl
     * @param saveFileName
     * @param apkUpdaterListener
     */
    public static void downloadAndInstall(final Context context, final String apkUrl, final String saveFileName, final ApkUpdaterListener apkUpdaterListener){
    	ApkUpdater.download(apkUrl, saveFileName, new ApkUpdaterListener() {
			@Override
			public void downloadStop(String saveFilename) {
				if(null != apkUpdaterListener){
					apkUpdaterListener.downloadStop(saveFileName);
				}
			}
			
			@Override
			public void downloadProgress(int progress) {
				if(null != apkUpdaterListener){
					apkUpdaterListener.downloadProgress(progress);
				}
			}
			
			@Override
			public void downloadFinish(String saveFilename) {
				if(null != apkUpdaterListener){
					apkUpdaterListener.downloadFinish(saveFileName);
				}
				//下载成功进行安装操作
				ApkUpdater.install(context, saveFileName);
			}
			
			@Override
			public void downloadError(Throwable e, String message) {
				if(null != apkUpdaterListener){
					apkUpdaterListener.downloadError(e, message);
				}
			}
		});
    }
    
    /**
     * 下载后自动安装
     * 
     * @param context
     * @param apkUrl
     * @param saveFileName
     */
    public static void downloadAndInstall(final Context context, final String apkUrl, final String saveFileName){
    	ApkUpdater.downloadAndInstall(context, apkUrl, saveFileName, null);
    }
    
    /**
     * 下载后自动安装
     * 
     * @param context
     * @param apkUrl
     */
    public static void downloadAndInstall(final Context context, final String apkUrl){
    	ApkUpdater.downloadAndInstall(context, apkUrl, DEFUALT_SAVE_FILENAME, null);
    }
    
    public static void choiceIfDownloadAndInstall(final Context context, final String apkUrl, final String saveFileName, final ApkUpdaterListener apkUpdaterListener, ApkUpdateConfig 
    		choiceConfig){
    	//没有就创建一个默认配置
    	if(null == choiceConfig){
    		choiceConfig = new ApkUpdateConfig();
    	}
    	
    	//如果没有自定义alertDialog，就默认使用系统自带的
    	final ApkUpdateConfig cc = choiceConfig;
    	if(null == choiceConfig.cusTomAlertDialog){
    		choiceConfig.cusTomAlertDialog = new AlertDialog.Builder(context)
    		.setTitle(cc.title)
    		.setMessage(cc.message)
    		.setCancelable(cc.cancelable)
    		.create();
    	}
    	
    	//设置确定事件
    	choiceConfig.cusTomAlertDialog.setButton(0, cc.okBtnText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				boolean isContinue = true;
				if(null != cc.onChoiceListener){
					isContinue = cc.onChoiceListener.onChoiceOk();
				}
				
				if(isContinue){
				}
			}
		});
    	
    	//设置取消时间
    	choiceConfig.cusTomAlertDialog.setButton(1, cc.cancelBtnText, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if(null != cc.onChoiceListener){
					cc.onChoiceListener.onChoiceOk();
				}
			}
		});
    	//点击外部不允许取消
    	choiceConfig.cusTomAlertDialog.setCanceledOnTouchOutside(false);
    	choiceConfig.cusTomAlertDialog.show();
    }
    
    /**
     * 停止下载，他停止后不允许再恢复
     */
    public void stopDownload() {
    	apkUpdaterHelper.stopDownload();
    } 
    
}
