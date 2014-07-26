/* 
 * @(#)AndroidHttpClientFace.java    Created on 2013-12-31
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.http2.andhttpclient;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.winupon.andframe.bigapple.http2.urlhttpclient.core.RequestParams;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 对AndroidHttpClient的封装，使用户调用更加的简单
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-12-31 下午7:35:52 $
 */
public class ORGHttpClient {
	private static final String DEFAULT_ENCODE = "utf-8";

	// ///////////////////////////////////////get方法部分////////////////////////////////////////////////////////
	public String get(String url) throws IOException {
		return get(url, null);
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param paramsMap
	 * @return
	 */
	public String get(String url, RequestParams requestParams) {
		logUrlAndParams(url, requestParams);

		if (null != requestParams && !requestParams.getParamMap().isEmpty()) {
			url += "?" + requestParams.getUrlParamsString();
		}

		String result = "";
		try {
			AndroidHttpClient client = AndroidHttpClient.newInstance("");
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = client.execute(httpGet);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(),
						DEFAULT_ENCODE);
			} else {
				LogUtils.e("请求返回失败");
			}
		} catch (Exception e) {
			LogUtils.e("", e);
		}

		return result;
	}

	// ///////////////////////////////////////post方法部分/////////////////////////////////////////////////
	public String post(String url) {
		return post(url, null);
	}

	/**
	 * post请求
	 * 
	 * @param url
	 * @param paramsMap
	 * @return
	 */
	public String post(String url, RequestParams requestParams) {
		logUrlAndParams(url, requestParams);

		LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

		if (null != requestParams && !requestParams.getParamMap().isEmpty()) {
			for (Map.Entry<String, String> entry : requestParams.getParamMap()
					.entrySet()) {
				try {
					params.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				} catch (Exception e) {
					LogUtils.e("", e);
				}
			}
		}

		String result = "";
		try {
			AndroidHttpClient client = AndroidHttpClient.newInstance("");
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODE));
			HttpResponse response = client.execute(httpPost);

			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				result = EntityUtils.toString(response.getEntity(),
						DEFAULT_ENCODE);
			} else {
				LogUtils.e("请求返回失败");
			}
		} catch (Exception e) {
			LogUtils.e("", e);
		}

		return result;
	}

	// 打出日志
	private void logUrlAndParams(String url, RequestParams requestParams) {
		if (null == requestParams) {
			LogUtils.d(url);
		} else {
			LogUtils.d(url + "[" + requestParams.toString() + "]");
		}
	}

}
