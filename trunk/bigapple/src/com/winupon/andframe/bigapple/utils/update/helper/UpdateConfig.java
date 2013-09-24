/* 
 * @(#)UpdateConfig.java    Created on 2013-5-2
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.update.helper;

import android.os.Environment;

/**
 * 下载更新的一些参数
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-5-2 下午12:26:39 $
 */
public class UpdateConfig {
    public static final String DEFAULT_SAVE_FILENAME = Environment.getExternalStorageDirectory().getPath()
            + "/bigapple/bigapple-default.apk";

    public static final String DEFAULT_UPDATE_TITLE = "提示";
    public static final String DEFAULT_UPDATE_TEXT = "软件版本更新";

    public static final String DEFAULT_POSTIVE_BTN_TEXT = "确定";
    public static final String DEFAULT_NEGATIVE_BTN_TEXT = "取消";

    public static final String DEFAULT_PROGRESS_TEXT = "请稍后...";

    // apk存放目录和下载地址
    private String saveFileName = DEFAULT_SAVE_FILENAME;
    private String apkUrl;

    // 更新提示语
    private String updateTitle = DEFAULT_UPDATE_TITLE;
    private String updateText = DEFAULT_UPDATE_TEXT;

    private String positiveBtnText = DEFAULT_POSTIVE_BTN_TEXT;
    private String negativeBtnText = DEFAULT_NEGATIVE_BTN_TEXT;

    private String progressText = DEFAULT_PROGRESS_TEXT;

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

}
