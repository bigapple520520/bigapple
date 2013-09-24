/* 
 * @(#)UpdateConfigFactory.java    Created on 2013-5-2
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.utils.update.helper;

import android.text.TextUtils;

/**
 * 产生一些常用的配置信息的工厂类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-5-2 下午1:12:33 $
 */
@Deprecated
public abstract class UpdateConfigFactory {

    /**
     * 更新提示配置
     * 
     * @param apkUrl
     *            apk下载地址
     * @param savePath
     *            apk保存路径
     * @param saveFileName
     *            apk保存名字，全路径
     * @return
     */
    @Deprecated
    public static UpdateConfig getUpdateConfig(String apkUrl, String savePath, String saveFileName) {
        if (TextUtils.isEmpty(apkUrl)) {
            return null;
        }

        UpdateConfig updateConfig = new UpdateConfig();
        updateConfig.setApkUrl(apkUrl);

        if (!TextUtils.isEmpty(saveFileName)) {
            updateConfig.setSaveFileName(saveFileName);
        }

        return updateConfig;
    }

    /**
     * 下载提示配置，全路径
     * 
     * @param apkUrl
     * @return
     */
    @Deprecated
    public static UpdateConfig getDownloadConfig(String apkUrl, String savePath, String saveFileName) {
        if (TextUtils.isEmpty(apkUrl)) {
            return null;
        }

        UpdateConfig updateConfig = new UpdateConfig();
        updateConfig.setApkUrl(apkUrl);

        if (!TextUtils.isEmpty(saveFileName)) {
            updateConfig.setSaveFileName(saveFileName);
        }

        updateConfig.setUpdateText("安装该软件");
        return updateConfig;
    }

}
