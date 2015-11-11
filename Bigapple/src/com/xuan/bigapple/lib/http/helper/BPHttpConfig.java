package com.xuan.bigapple.lib.http.helper;

import com.xuan.bigapple.lib.http.BPHttpClientFactory;
import com.xuan.bigapple.lib.http.callback.ResultHandlerCallback;

/**
 * 全局访问参数
 * 
 * @author xuan
 */
public class BPHttpConfig {
	/** 是否打印日志 */
	public static boolean DEBUG = false;

	/** 参数编码 */
	public static String DEFAULT_ENCODE = "utf-8";
	/** 连接超时 */
	public static int DEFAULT_CONNECTION_TIMEOUT = 1000 * 30;
	/** 读取超时 */
	public static int DEFAULT_READ_TIMEOUT = 1000 * 30;

	/** 安卓自带的apache的HttpClient实现方式 */
	public static int CLIENT_IMPL_HTTPCLIENT = 1;

	/** 提交|获取的编码方式 */
	private String mEncode = DEFAULT_ENCODE;
	/** 连接超时 */
	private int mConnectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
	/** 读取超时 */
	private int mReadTimeout = DEFAULT_READ_TIMEOUT;

	/** 结果返回回调 */
	private ResultHandlerCallback mResultHandlerCallback;

	/** 下载文件时才有用，表示是否支持断点续传 */
	private boolean mDownloadIsSupportResume;
	/** 下载时文件存放路径 */
	private String mDownloadFileName;

	/** 实现方式，现在只有一种基于HttpClient实现的 */
	private int mImplType = BPHttpClientFactory.CLIENT_IMPL_HTTPCLIENT;

	public int getImplType() {
		return mImplType;
	}

	public BPHttpConfig setImplType(int implType) {
		this.mImplType = implType;
		return this;
	}

	public String getEncode() {
		return mEncode;
	}

	public BPHttpConfig setEncode(String encode) {
		this.mEncode = encode;
		return this;
	}

	public int getConnectionTimeout() {
		return mConnectionTimeout;
	}

	public BPHttpConfig setConnectionTimeout(int connectionTimeout) {
		this.mConnectionTimeout = connectionTimeout;
		return this;
	}

	public int getReadTimeout() {
		return mReadTimeout;
	}

	public BPHttpConfig setReadTimeout(int readTimeout) {
		this.mReadTimeout = readTimeout;
		return this;
	}

	public ResultHandlerCallback getResultHandlerCallback() {
		return mResultHandlerCallback;
	}

	public BPHttpConfig setResultHandlerCallback(
			ResultHandlerCallback resultHandlerCallback) {
		this.mResultHandlerCallback = resultHandlerCallback;
		return this;
	}

	public boolean isDownloadIsSupportResume() {
		return mDownloadIsSupportResume;
	}

	public BPHttpConfig setDownloadIsSupportResume(
			boolean downloadIsSupportResume) {
		this.mDownloadIsSupportResume = downloadIsSupportResume;
		return this;
	}

	public String getDownloadFileName() {
		return mDownloadFileName;
	}

	public BPHttpConfig setDownloadFileName(String downloadFileName) {
		this.mDownloadFileName = downloadFileName;
		return this;
	}

}
