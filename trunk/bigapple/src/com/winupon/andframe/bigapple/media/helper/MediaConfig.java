/* 
 * @(#)MediaConfig.java    Created on 2013-5-2
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.media.helper;

import android.os.Environment;

/**
 * media操作的参数类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-5-2 下午8:03:27 $
 */
public class MediaConfig {
    public static final String DEFAULT_VOICE_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/bigapple/voice/";
    public static final String DEFAULT_VOICE_EXT = "amr";

    private String voicePath = DEFAULT_VOICE_PATH;// 文件存放路劲
    private String voiceExt = DEFAULT_VOICE_EXT;// 文件后缀名

    public MediaConfig() {
    }

    public MediaConfig(String voicePath, String voiceExt) {
        this.voicePath = voicePath;
        this.voiceExt = voiceExt;
    }

    public String getVoicePath() {
        return voicePath;
    }

    public void setVoicePath(String voicePath) {
        this.voicePath = voicePath;
    }

    public String getVoiceExt() {
        return voiceExt;
    }

    public void setVoiceExt(String voiceExt) {
        this.voiceExt = voiceExt;
    }

}
