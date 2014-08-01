/* 
 * @(#)UpdateConfig.java    Created on 2013-5-2
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.update.helper;

import android.os.Environment;

/**
 * 下载apk的一些参数配置
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-5-2 下午12:26:39 $
 */
public class UpdateConfig {
    /**
     * 下载成功后apk存放的地址
     */
    private String saveFileName = Environment.getExternalStorageDirectory().getPath()
            + "/bigapple/bigapple-default.apk";

    /**
     * 需要下载的apk的网络地址
     */
    private String apkUrl;

    /**
     * 更新提示语,主要用在点击后单出确认框，让用户确认操作
     */
    private String updateTitle = "提示";
    private String updateText = "软件版本更新";
    private String positiveBtnText = "确定";
    private String negativeBtnText = "取消";
    private String progressText = "请稍后...";

    /**
     * 下载完成后是否自动安装，如果设置成false，那么只会加载到你指定的文件目录不会自动安装，默认自动安装
     */
    private boolean autoInstall = true;

    /**
     * 下载时apk,是否允许被取消，默认允许
     */
    private boolean canCancel = true;

    public String getSaveFileName() {
        return saveFileName;
    }

    public void setSaveFileName(String saveFileName) {
        this.saveFileName = saveFileName;
    }

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String getUpdateText() {
        return updateText;
    }

    public void setUpdateText(String updateText) {
        this.updateText = updateText;
    }

    public String getUpdateTitle() {
        return updateTitle;
    }

    public void setUpdateTitle(String updateTitle) {
        this.updateTitle = updateTitle;
    }

    public String getPositiveBtnText() {
        return positiveBtnText;
    }

    public void setPositiveBtnText(String positiveBtnText) {
        this.positiveBtnText = positiveBtnText;
    }

    public String getNegativeBtnText() {
        return negativeBtnText;
    }

    public void setNegativeBtnText(String negativeBtnText) {
        this.negativeBtnText = negativeBtnText;
    }

    public String getProgressText() {
        return progressText;
    }

    public void setProgressText(String progressText) {
        this.progressText = progressText;
    }

    public boolean isAutoInstall() {
        return autoInstall;
    }

    public void setAutoInstall(boolean autoInstall) {
        this.autoInstall = autoInstall;
    }

    public boolean isCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

}
