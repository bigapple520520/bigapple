package com.xuan.bigapple.lib.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.xuan.bigapple.lib.http.helper.FileWraper;

/**
 * 请求对象
 * 
 * @author xuan
 */
public class BPRequest {
	/** 普通参数 */
	private Map<String, String> mParamMap;
	/** 文件参数 */
	private Map<String, FileWraper> mFileParamMap;
	/** 头部参数 */
	private Map<String, String> mHeaderMap;
	/** 已请求体Json方式提交 */
	private String mBodyJson;

	public BPRequest() {
		init();
	}

	/**
	 * 放入请求体Json串
	 * 
	 * @param bodyJson
	 */
	public void putBodyJson(String bodyJson) {
		if (null != bodyJson) {
			this.mBodyJson = bodyJson;
		}
	}

	/**
	 * 添加普通参数
	 * 
	 * @param key
	 * @param value
	 */
	public void putParam(String key, String value) {
		if (key != null && value != null) {
			mParamMap.put(key, value);
		}
	}

	/**
	 * 添加文件参数
	 * 
	 * @param key
	 * @param file
	 * @throws FileNotFoundException
	 */
	public void putFile(String key, File file) throws FileNotFoundException {
		putFile(key, new FileInputStream(file), file.getName());
	}

	/**
	 * 添加文件流
	 * 
	 * @param key
	 * @param stream
	 */
	public void putFile(String key, InputStream stream) {
		putFile(key, stream, null);
	}

	/**
	 * 添加文件流
	 * 
	 * @param key
	 * @param stream
	 * @param fileName
	 */
	public void putFile(String key, InputStream stream, String fileName) {
		putFile(key, stream, fileName, null);
	}

	/**
	 * 添加文件流
	 * 
	 * @param key
	 * @param stream
	 * @param fileName
	 * @param contentType
	 */
	public void putFile(String key, InputStream stream, String fileName,
			String contentType) {
		if (key != null && stream != null) {
			mFileParamMap.put(key,
					new FileWraper(stream, fileName, contentType));
		}
	}

	/**
	 * 添加头部
	 * 
	 * @param key
	 * @param value
	 */
	public void putHeader(String key, String value) {
		mHeaderMap.put(key, value);
	}

	/**
	 * 删除普通参数
	 * 
	 * @param key
	 */
	public void removeParam(String key) {
		mParamMap.remove(key);
	}

	/**
	 * 删除文件参数
	 * 
	 * @param key
	 */
	public void removeFile(String key) {
		mFileParamMap.remove(key);
	}

	/**
	 * 删除头部
	 * 
	 * @param key
	 */
	public void removeHeader(String key) {
		mHeaderMap.remove(key);
	}

	/**
	 * 获取GET参数串
	 * 
	 * @return
	 */
	public String getGetParamsUrl() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, String> entry : mParamMap.entrySet()) {
			sb.append(entry.getKey()).append("=")
					.append(URLEncoder.encode(entry.getValue())).append("&");
		}

		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();
	}

	public Map<String, String> getParamMap() {
		return mParamMap;
	}

	public Map<String, FileWraper> getFileParamMap() {
		return mFileParamMap;
	}

	public Map<String, String> getHeaderMap() {
		return mHeaderMap;
	}

	public String getBodyJson() {
		return mBodyJson;
	}

	// 初始化MAP
	private void init() {
		mParamMap = new ConcurrentHashMap<String, String>();
		mFileParamMap = new ConcurrentHashMap<String, FileWraper>();
		mHeaderMap = new ConcurrentHashMap<String, String>();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (ConcurrentHashMap.Entry<String, String> entry : mParamMap
				.entrySet()) {
			if (result.length() > 0) {
				result.append("&");
			}

			result.append(entry.getKey());
			result.append("=");
			result.append(entry.getValue());
		}

		for (ConcurrentHashMap.Entry<String, FileWraper> entry : mFileParamMap
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
