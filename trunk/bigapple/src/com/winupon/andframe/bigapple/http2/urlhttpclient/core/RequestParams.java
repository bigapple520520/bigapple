package com.winupon.andframe.bigapple.http2.urlhttpclient.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 请求参数封装
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:31:56 $
 */
public class RequestParams {
	private ConcurrentHashMap<String, String> paramMap;// 普通参数
	private ConcurrentHashMap<String, FileWraper> fileParamMap;// 文件参数

	// ///////////////////////////////////////////////构造//////////////////////////////////////////////////////////////
	public RequestParams() {
		init();
	}

	public RequestParams(Map<String, String> source) {
		init();
		for (Map.Entry<String, String> entry : source.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public RequestParams(String key, String value) {
		init();
		put(key, value);
	}

	public RequestParams(Object... keysAndValues) {
		init();
		int len = keysAndValues.length;
		if (len % 2 != 0) {
			throw new IllegalArgumentException(
					"Supplied arguments must be even");
		}

		for (int i = 0; i < len; i += 2) {
			String key = String.valueOf(keysAndValues[i]);
			String val = String.valueOf(keysAndValues[i + 1]);
			put(key, val);
		}
	}

	private void init() {
		paramMap = new ConcurrentHashMap<String, String>();
		fileParamMap = new ConcurrentHashMap<String, FileWraper>();
	}

	// /////////////////////////////////////////////////添加参数////////////////////////////////////////////////////////
	public void put(String key, String value) {
		if (null != key && null != value) {
			paramMap.put(key, value);
		}
	}

	public void put(String key, File file) throws FileNotFoundException {
		put(key, new FileInputStream(file), file.getName());
	}

	public void put(String key, InputStream stream) {
		put(key, stream, null);
	}

	public void put(String key, InputStream stream, String fileName) {
		put(key, stream, fileName, null);
	}

	public void put(String key, InputStream stream, String fileName,
			String contentType) {
		if (key != null && stream != null) {
			fileParamMap
					.put(key, new FileWraper(stream, fileName, contentType));
		}
	}

	public void remove(String key) {
		paramMap.remove(key);
		fileParamMap.remove(key);
	}

	public ConcurrentHashMap<String, String> getParamMap() {
		return paramMap;
	}

	public void setParamMap(ConcurrentHashMap<String, String> paramMap) {
		this.paramMap = paramMap;
	}

	public ConcurrentHashMap<String, FileWraper> getFileParamMap() {
		return fileParamMap;
	}

	public void setFileParamMap(
			ConcurrentHashMap<String, FileWraper> fileParamMap) {
		this.fileParamMap = fileParamMap;
	}

	/**
	 * 获取Url后的拼接串
	 * 
	 * @return
	 */
	public String getUrlParamsString() {
		StringBuilder sb = null;
		try {
			sb = new StringBuilder();
			for (Map.Entry<String, String> e : paramMap.entrySet()) {
				sb.append(e.getKey()).append("=")
						.append(URLEncoder.encode(e.getValue(), "utf-8"))
						.append("&");
			}
		} catch (Exception e) {
			LogUtils.e("", e);
			return "";
		}

		return sb.toString();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (ConcurrentHashMap.Entry<String, String> entry : paramMap
				.entrySet()) {
			if (result.length() > 0) {
				result.append("&");
			}

			result.append(entry.getKey());
			result.append("=");
			result.append(entry.getValue());
		}

		for (ConcurrentHashMap.Entry<String, FileWraper> entry : fileParamMap
				.entrySet()) {
			if (result.length() > 0) {
				result.append("&");
			}

			result.append(entry.getKey());
			result.append("=");
			result.append("FILE");
		}

		return result.toString();
	}

}
