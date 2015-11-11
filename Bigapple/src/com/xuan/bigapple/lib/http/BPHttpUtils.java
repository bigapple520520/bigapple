package com.xuan.bigapple.lib.http;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.xuan.bigapple.lib.http.callback.ResultHandlerCallback;
import com.xuan.bigapple.lib.http.helper.BPHttpConfig;
import com.xuan.bigapple.lib.utils.Validators;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * HTTP工具类，后期可以有多种实现方式
 * 
 * @author xuan
 */
public abstract class BPHttpUtils {

	/**
	 * post json提交
	 * 
	 * @param url
	 * @param request
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static BPResponse postJson(String url, BPRequest request,
			BPHttpConfig config) throws Exception {
		if (Validators.isEmpty(url)) {
			new BPResponse(-1, "Url is empty.");
		}

		BPHttpClient client = BPHttpClientFactory.getDefaultCliet(config);
		return client.postJson(url, request, config);
	}

	/**
	 * post提交
	 * 
	 * @param url
	 * @param request
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static BPResponse post(String url, BPRequest request,
			BPHttpConfig config) throws Exception {
		if (Validators.isEmpty(url)) {
			new BPResponse(-1, "Url is empty.");
		}

		BPHttpClient client = BPHttpClientFactory.getDefaultCliet(config);
		return client.post(url, request, config);
	}

	/**
	 * POST请求
	 * 
	 * @param url
	 * @param paramsMap
	 * @param md5Key
	 * @return
	 */
	public static BPResponse post(String url, Map<String, String> paramsMap)
			throws Exception {
		if (Validators.isEmpty(url)) {
			new BPResponse(-1, "Url is empty.");
		}

		if (null == paramsMap) {
			paramsMap = new ConcurrentHashMap<String, String>();
		}

		BPHttpConfig config = new BPHttpConfig();
		BPHttpClient client = BPHttpClientFactory.getDefaultCliet(config);
		BPRequest bpRequest = new BPRequest();
		// 设置参数
		setParams(bpRequest, paramsMap);
		return client.post(url, bpRequest, config);
	}

	/**
	 * get请求
	 * 
	 * @param url
	 * @param request
	 * @param config
	 * @return
	 * @throws Exception
	 */
	public static BPResponse get(String url, BPRequest request,
			BPHttpConfig config) throws Exception {
		if (Validators.isEmpty(url)) {
			new BPResponse(-1, "Url is empty.");
		}

		BPHttpClient client = BPHttpClientFactory.getDefaultCliet(config);
		return client.get(url, request, config);
	}

	/**
	 * GET请求
	 * 
	 * @param url
	 * @param paramsMap
	 * @param md5Key
	 * @return
	 */
	public static BPResponse get(String url, Map<String, String> paramsMap)
			throws Exception {
		if (Validators.isEmpty(url)) {
			new BPResponse(-1, "Url is empty.");
		}

		if (null == paramsMap) {
			paramsMap = new ConcurrentHashMap<String, String>();
		}

		BPHttpConfig config = new BPHttpConfig();
		BPHttpClient client = BPHttpClientFactory.getDefaultCliet(config);
		BPRequest bpRequest = new BPRequest();
		// 设置参数
		setParams(bpRequest, paramsMap);
		return client.get(url, bpRequest, config);
	}

	/**
	 * 上传文件
	 * 
	 * @param url
	 * @param fileNameMap
	 * @param paramsMap
	 * @return
	 */
	public static BPResponse upload(String url,
			Map<String, String> fileNameMap, Map<String, String> paramsMap)
			throws Exception {
		if (Validators.isEmpty(url)) {
			new BPResponse(-1, "Url is empty.");
		}

		if (null == paramsMap) {
			paramsMap = new ConcurrentHashMap<String, String>();
		}

		if (null == fileNameMap) {
			fileNameMap = new ConcurrentHashMap<String, String>();
		}

		BPHttpConfig config = new BPHttpConfig();
		BPHttpClient client = BPHttpClientFactory.getDefaultCliet(config);
		BPRequest bpRequest = new BPRequest();
		// 设置普通参数
		setParams(bpRequest, paramsMap);
		// 设置文件参数
		setFileParams(bpRequest, fileNameMap);

		return client.post(url, bpRequest, config);
	}

	/**
	 * 下载到文件
	 * 
	 * @param downloadUrl
	 *            下载地址
	 * @param paramsMap
	 *            get参数
	 * @param saveFileName
	 *            文件保存地址
	 * @param resultHandlerCallback
	 *            下载回调
	 * @return
	 * @throws Exception
	 */
	public static BPResponse dowloadFile(String downloadUrl,
			Map<String, String> paramsMap, String saveFileName,
			ResultHandlerCallback resultHandlerCallback) throws Exception {
		if (Validators.isEmpty(downloadUrl)) {
			new BPResponse(-1, "DownloadUrl is empty.");
		}

		if (null == paramsMap) {
			paramsMap = new ConcurrentHashMap<String, String>();
		}

		// 配置
		BPHttpConfig httpConfig = new BPHttpConfig();
		httpConfig.setDownloadFileName(saveFileName);
		httpConfig.setResultHandlerCallback(resultHandlerCallback);

		BPHttpClient client = BPHttpClientFactory.getDefaultCliet(httpConfig);
		BPRequest bpRequest = new BPRequest();
		// 设置参数
		setParams(bpRequest, paramsMap);

		return client.dowload(downloadUrl, bpRequest, httpConfig);
	}

	/** 设置参数 */
	private static void setParams(BPRequest bpRequest,
			Map<String, String> paramsMap) {
		for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
			bpRequest.putParam(entry.getKey(), entry.getValue());
		}
	}

	/** 设置文件参数 */
	private static void setFileParams(BPRequest bpRequest,
			Map<String, String> paramsMap) {
		for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
			try {
				bpRequest.putFile(entry.getKey(), new File(entry.getValue()));
			} catch (Exception e) {
				LogUtils.e(e.getMessage(), e);
			}
		}
	}

}
