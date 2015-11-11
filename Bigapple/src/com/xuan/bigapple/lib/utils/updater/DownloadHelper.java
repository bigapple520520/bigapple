package com.xuan.bigapple.lib.utils.updater;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Handler;
import android.os.Looper;

import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 文件下载帮助工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2015-3-17 下午5:31:24 $
 */
public class DownloadHelper {
	private static final int BUFFER_SIZE = 1024;
	private volatile boolean stopFlag = false;// 停止下载标记
	private final Handler handler = new Handler(Looper.getMainLooper());

	/**
	 * 下载APK
	 * 
	 * @param fileUrl
	 *            下载文件网络地址
	 * @param saveFilename
	 *            下载后保存文件本地地址
	 * @param downloadListener
	 *            下载监听
	 */
	public void download(final String fileUrl, final String saveFilename,
			final DownloadListener downloadListener) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					// Listener-开始下载
					if (null != downloadListener) {
						postUI(new Runnable() {
							@Override
							public void run() {
								downloadListener.downloadStart();
							}
						});
					}

					HttpGet getMethod = new HttpGet(fileUrl);
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
							LogUtils.e("Mkdirs failed");
							// Listener-下载错误
							if (null != downloadListener) {
								postUI(new Runnable() {
									@Override
									public void run() {
										downloadListener.downloadError(null,
												"Mkdirs failed");
									}
								});
							}
						}
					}

					// 创建文件
					if (!apkFile.exists()) {
						boolean success = apkFile.createNewFile();
						if (!success) {
							LogUtils.e("Create file failed");
							// Listener-下载错误
							if (null != downloadListener) {
								postUI(new Runnable() {
									@Override
									public void run() {
										downloadListener.downloadError(null,
												"Create file failed");
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
						// Listener-下载中
						if (null != downloadListener) {
							postUI(new Runnable() {
								@Override
								public void run() {
									downloadListener.downloadProgress(progress);
								}
							});
						}

						if (numread <= 0) {
							// Listener-下载完成
							if (null != downloadListener) {
								postUI(new Runnable() {
									@Override
									public void run() {
										downloadListener
												.downloadFinish(saveFilename);
									}
								});
							}
							break;
						}

						fos.write(buf, 0, numread);
					} while (!stopFlag);

					// 取消下载
					if (stopFlag) {
						// Listener-下载中止
						if (null != downloadListener) {
							postUI(new Runnable() {
								@Override
								public void run() {
									downloadListener.downloadStop(saveFilename);
								}
							});
						}
						stopFlag = false;
					}

					fos.close();
					inputStream.close();
				} catch (Exception e) {
					LogUtils.e(e.getMessage(), e);

					// Listener-下载错误
					if (null != downloadListener) {
						downloadListener.downloadError(e, e.getMessage());
					}
				}
			}
		}).start();
	}

	/**
	 * 停止下载，他停止后不允许再恢复
	 */
	public void stopDownload() {
		this.stopFlag = true;
	}

	// 提交任务到主线程
	private void postUI(Runnable runnable) {
		handler.post(runnable);
	}

}
