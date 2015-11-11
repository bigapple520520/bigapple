package com.xuan.bigapple.lib.media;

import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.text.TextUtils;

import com.xuan.bigapple.lib.media.helper.MediaConfig;
import com.xuan.bigapple.lib.utils.log.LogUtils;

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
	 * @param isFullpath
	 */
	public void startPlaying(String fileName, boolean isFullpath) {
		startPlaying(fileName, isFullpath, null);
	}

	/**
	 * 播放音频
	 * 
	 * @param fileName
	 * @param isFullpath
	 *            表示fileName指定的是否是全路径
	 * @param playingListener
	 *            播放监听
	 */
	public void startPlaying(String fileName, boolean isFullpath,
			final PlayingListener playingListener) {
		if (TextUtils.isEmpty(fileName)) {
			return;
		}

		stopPlaying();
		mediaPlayer = getMediaPlayer();
		try {
			// 确定播放地址
			String playFileName = null;
			if (isFullpath) {
				playFileName = fileName;
			} else {
				playFileName = mediaConfig.getFilePath() + File.separator
						+ fileName + "." + mediaConfig.getFileExt();
			}

			mediaPlayer.setDataSource(playFileName);
			mediaPlayer.prepare();

			if (null != playingListener) {
				playingListener.startPlay(mediaPlayer, playFileName);
			}

			mediaPlayer.start();
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.reset();
					if (null != playingListener) {
						playingListener.endPlay();
					}
				}
			});
		} catch (Exception e) {
			LogUtils.e(e.getMessage(), e);
		}
	}

	/**
	 * 停止播放
	 */
	public void stopPlaying() {
		if (null != mediaPlayer) {
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.reset();
		}
	}

	/**
	 * 释放调下次重新会创建了
	 */
	public void release() {
		stopPlaying();
		if (null != mediaPlayer) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
	}

	/**
	 * 是否在播放状态
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		if (null == mediaPlayer) {
			return false;
		}

		return mediaPlayer.isPlaying();
	}

	public MediaPlayer getMediaPlayer() {
		if (null == mediaPlayer) {
			mediaPlayer = new MediaPlayer();
		}

		return mediaPlayer;
	}

	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}

	/**
	 * 播放回调监控
	 * 
	 * @author xuan
	 */
	public static interface PlayingListener {
		/**
		 * 开始播放
		 * 
		 * @param mediaPlayer
		 * @param fileName
		 */
		void startPlay(MediaPlayer mediaPlayer, String fileName);

		/**
		 * 结束播放
		 */
		void endPlay();
	}

}
