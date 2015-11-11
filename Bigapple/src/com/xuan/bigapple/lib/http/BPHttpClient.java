package com.xuan.bigapple.lib.http;

import com.xuan.bigapple.lib.http.helper.BPHttpConfig;

/**
 * HTT访问通用接口
 * 
 * @author xuan
 */
public interface BPHttpClient {
	/**
	 * POST请求，Json放到请求体里面
	 * 
	 * @param url
	 * @param bpRequest
	 * @param httConfig
	 * @return
	 */
	BPResponse postJson(String url, BPRequest bpRequest, BPHttpConfig httConfig)
			throws Exception;

	/**
	 * POST请求，普通参数的方式提交
	 * 
	 * @param url
	 * @param bpRequest
	 * @param httConfig
	 * @return
	 */
	BPResponse post(String url, BPRequest bpRequest, BPHttpConfig httConfig)
			throws Exception;

	/**
	 * GET请求
	 * 
	 * @param url
	 * @param bpRequest
	 * @return
	 */
	BPResponse get(String url, BPRequest bpRequest, BPHttpConfig httConfig)
			throws Exception;

	/**
	 * 下载，用的是get请求
	 * 
	 * @param url
	 * @param saveFileName
	 * @param bpRequest
	 * @return
	 */
	BPResponse dowload(String url, BPRequest bpRequest, BPHttpConfig httConfig)
			throws Exception;

}
