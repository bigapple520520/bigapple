package com.winupon.andframe.bigapple.http2.params;

import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Http协议的一些参数，包括超时等
 * 
 * @author xuan
 * 
 */
public class HttpParams {
	// 请求头参数
	private final HashMap<String, String> requestPropertyMap = new HashMap<String, String>();

	// 连接超时
	public static final int CONNECTION_TIMEOUT = 60 * 1000;
	private int connectionTimeout = CONNECTION_TIMEOUT;

	// 读取超时
	public static final int READ_TIMEOUT = 60 * 1000;
	private int readTimeout = READ_TIMEOUT;

	// 请求编码
	public static final String ENCODE_UTF8 = "utf-8";
	private String encode = ENCODE_UTF8;

	// 表单提交模式
	public static final String CONTENT_TYPE_MUTIPART = "multipart/form-data; boundary=";
	public static final String CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded";
	private String contentType = CONTENT_TYPE_URLENCODED;

	// 设置发送请求是否使用缓存，设置成0可以防止发送打文件OOM
	public static final int CHUNKED_STREAMING_MODE_NO = 0;
	private int chunkedStreamingMode = CHUNKED_STREAMING_MODE_NO;

	private boolean doOutPut = true;
	private boolean doInPut = true;
	private boolean useCaches = true;// POST请求设置为false

	public HttpParams() {
		// 客户端传给服务器端可接受传输方式，api2.2以上这个使用了gzip方式传输的
		requestPropertyMap.put("Accept-Encoding", "identity");
	}

	/**
	 * 设置添加请求报头参数
	 * 
	 * @param key
	 * @param value
	 */
	public void setRequestProperty(String key, String value) {
		requestPropertyMap.put(key, value);
	}

	/**
	 * 获取请求报头参数
	 * 
	 * @param key
	 * @return
	 */
	public String getRequestProperty(String key) {
		return requestPropertyMap.get(key);
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public String getEncode() {
		return encode;
	}

	public void setEncode(String encode) {
		this.encode = encode;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public boolean isDoOutPut() {
		return doOutPut;
	}

	public void setDoOutPut(boolean doOutPut) {
		this.doOutPut = doOutPut;
	}

	public boolean isDoInPut() {
		return doInPut;
	}

	public void setDoInPut(boolean doInPut) {
		this.doInPut = doInPut;
	}

	public boolean isUseCaches() {
		return useCaches;
	}

	public void setUseCaches(boolean useCaches) {
		this.useCaches = useCaches;
	}

	public int getChunkedStreamingMode() {
		return chunkedStreamingMode;
	}

	public void setChunkedStreamingMode(int chunkedStreamingMode) {
		this.chunkedStreamingMode = chunkedStreamingMode;
	}

	/**
	 * 注入请求参数头
	 * 
	 * @param connection
	 */
	public void injectToConnection(HttpURLConnection connection) {
		for (Entry<String, String> entry : requestPropertyMap.entrySet()) {
			connection.setRequestProperty(entry.getKey(), entry.getValue());
		}

		connection.setConnectTimeout(connectionTimeout);
		connection.setReadTimeout(readTimeout);
		connection.setDoOutput(doOutPut);
		connection.setDoInput(doInPut);
		connection.setUseCaches(useCaches);
	}

}
