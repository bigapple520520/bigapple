package com.xuan.bigapple.lib.media;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.media.MediaRecorder;
import android.os.Handler;

import com.xuan.bigapple.lib.io.FileUtils;
import com.xuan.bigapple.lib.media.helper.MediaConfig;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 录音器工具类
 * 
 * @author xuan
 * @version $Revision: 31004 $, $Date: 2012-09-28 19:33:03 +0800 (周五, 28 九月
 *          2012) $
 */
public class MediaRecorderModel {
	private final int BASE = 1;

	private final MediaConfig mediaConfig;// 配置信息

	private MediaRecorder mediaRecorder;// 录音器
	private volatile String fileName;// 文件名

	private final ExecutorService singleThreadPool;

	private volatile long lastStartTimeMillis;// 记录开始录音时间，跟结束录音做对比看是否超过某个时间
	private boolean isStarted = false;// 标记是否正在录音

	private final Handler handler = new Handler();

	private OnRecordListener onRecordListener;

	/** 定时获取录音分贝数 */
	private final Runnable calculateDbRunnable = new Runnable() {
		@Override
		public void run() {
			calculateDb();
		}
	};

	public MediaRecorderModel(MediaConfig mediaConfig) {
		singleThreadPool = Executors.newSingleThreadExecutor();
		this.mediaConfig = mediaConfig;
		checkFile();
	}

	public String getVersionId() {
		return mediaRecorder.toString();
	}

	/**
	 * 开始录制
	 * 
	 * @param voiceFileName
	 * @param onRecordStartedListener
	 */
	public void startRecording(String voiceFileName) {
		fileName = voiceFileName;

		singleThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				if (isStarted) {
					LogUtils.d("startRecord isStarted");
					return;
				}

				try {
					isStarted = true;
					LogUtils.d("startRecord");
					// 初始化MediaRecorder
					mediaRecorder = new MediaRecorder();
					// 设置麦克风
					mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
					// 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样
					mediaRecorder
							.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
					mediaRecorder
							.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
					// 文件输出路径
					mediaRecorder.setOutputFile(mediaConfig.getFilePath()
							+ File.separator + fileName + "."
							+ mediaConfig.getFileExt());
					try {
						mediaRecorder.prepare();
					} catch (Exception e) {
						LogUtils.e(e.getMessage(), e);
					}

					// 开始录音
					mediaRecorder.start();

					// 通知录音开始
					if (null != onRecordListener) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								onRecordListener.onRecordStarted();
							}
						});
					}

					calculateDb();// 开始记录分贝
					lastStartTimeMillis = System.currentTimeMillis();
				} catch (Exception e) {
					LogUtils.e(e.getMessage(), e);
				}
			}
		});
	}

	/**
	 * 结束录制
	 * 
	 * @param isCancel
	 *            是否是取消结束
	 */
	public void stopRecording(final boolean isCancel) {
		singleThreadPool.submit(new Runnable() {
			@Override
			public void run() {
				if (!isStarted) {
					LogUtils.d("stopRecord !isStarted");
					return;
				}

				isStarted = false;
				try {
					LogUtils.d("stopRecord");
					final long x = System.currentTimeMillis()
							- lastStartTimeMillis;
					boolean success = true;

					// 录音时间如果没超过1S进行onTooShort通知
					if (x < 1000) {
						success = false;

						if (null != onRecordListener && !isCancel) {
							// 录音时间太短
							handler.post(new Runnable() {
								@Override
								public void run() {
									onRecordListener.onTooShort();
								}
							});
						}

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							LogUtils.e(e.getMessage(), e);
						}
					}

					mediaRecorder.stop();
					mediaRecorder.release(); // 重置mediaRecorder对象，使其为空闲状态
					mediaRecorder = null;

					// 删除录音时间太短的文件
					if (!success || isCancel) {
						FileUtils.deleteFileOrDirectoryQuietly(mediaConfig
								.getFilePath()
								+ File.separator
								+ fileName
								+ "." + mediaConfig.getFileExt());
					}

					final boolean temp = success;
					// 通知录音结束
					if (null != onRecordListener) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								onRecordListener.onRecordStoped(isCancel, temp,
										fileName, x);
							}
						});
					}
				} catch (Exception e) {
					LogUtils.e(e.getMessage(), e);
				}
			}
		});
	}

	public void destroy() {
		singleThreadPool.shutdown();
	}

	/**
	 * 获取录音器
	 * 
	 * @return
	 */
	public MediaRecorder getMediaRecorder() {
		return mediaRecorder;
	}

	public void setMediaRecorder(MediaRecorder mediaRecorder) {
		this.mediaRecorder = mediaRecorder;
	}

	/**
	 * 检查文件夹是否存在
	 */
	private void checkFile() {
		File file = new File(mediaConfig.getFilePath());
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 设置录音监听
	 * 
	 * @param onRecordListener
	 */
	public void setOnRecordListener(OnRecordListener onRecordListener) {
		this.onRecordListener = onRecordListener;
	}

	/** 获取录音分贝 */
	private void calculateDb() {
		if (!isStarted) {
			handler.removeCallbacks(calculateDbRunnable);
			return;
		}

		if (null != mediaRecorder) {
			double ratio = (double) mediaRecorder.getMaxAmplitude() / BASE;
			double db = 0;// 分贝
			if (ratio > 1) {
				db = 20 * Math.log10(ratio);
			}

			LogUtils.d("分贝值：" + db);
			final double fDb = db;
			if (null != onRecordListener) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						onRecordListener.onVolumeDb(fDb);
					}
				});
			}

			handler.postDelayed(calculateDbRunnable,
					mediaConfig.getVolumeInteral());
		}
	}

	/**
	 * 录音监听
	 * 
	 * @author xuan
	 */
	public static interface OnRecordListener {
		/**
		 * 录音开始
		 */
		void onRecordStarted();

		/**
		 * 录音结束
		 * 
		 * @param isCancel
		 *            是否是取消
		 * @param success
		 *            录音是否成功
		 * @param fileName
		 *            录音文件名称
		 * @param voiceLength
		 *            语音长度
		 */
		void onRecordStoped(boolean isCancel, boolean success, String fileName,
				long voiceLength);

		/**
		 * 录音时间太短
		 */
		void onTooShort();

		/**
		 * 分贝数[1,90.3]db
		 * 
		 * @param db
		 */
		void onVolumeDb(double db);
	}

}
