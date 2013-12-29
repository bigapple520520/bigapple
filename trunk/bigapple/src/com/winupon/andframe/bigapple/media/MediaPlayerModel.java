/* 
 * @(#)MediaPlayerUtils.java    Created on 2012-12-13
 * Copyright (c) 2012 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.media;

import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;

import com.winupon.andframe.bigapple.media.helper.MediaConfig;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 播放器工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2012-12-13 下午12:20:12 $
 */
public class MediaPlayerModel {
    private final MediaConfig mediaConfig;// 参数配置
    private MediaPlayer mediaPlayer;

    public MediaPlayerModel(MediaConfig mediaConfig) {
        this.mediaConfig = mediaConfig;
    }

    /**
     * 播放音频
     * 
     * @param fileName
     */
    public void playVoice(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return;
        }

        prepareMediaPlayer();

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(mediaConfig.getVoicePath() + File.separator + fileName + "."
                    + mediaConfig.getVoiceExt());
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.reset();
                }
            });
        }
        catch (Exception e) {
            LogUtils.e("", e);
        }
    }

    public MediaPlayer getMediaPlayer() {
        prepareMediaPlayer();
        return mediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        this.mediaPlayer = mediaPlayer;
    }

    public void release() {
        getMediaPlayer().release();
    }

    // 懒加载模式，第一次使用时才实例化对象
    private void prepareMediaPlayer() {
        if (null == mediaPlayer) {
            mediaPlayer = new MediaPlayer();
        }
    }

}
