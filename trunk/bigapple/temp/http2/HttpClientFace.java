/* 
 * @(#)URLHttpClient.java    Created on 2014-1-2
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.http2;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import com.winupon.andframe.bigapple.http2.params.HttpParams;
import com.winupon.andframe.bigapple.http2.request.HttpGet;
import com.winupon.andframe.bigapple.http2.request.HttpPost;
import com.winupon.andframe.bigapple.http2.request.entity.MultipartEntity;
import com.winupon.andframe.bigapple.http2.request.entity.UrlEncodedFormEntity;
import com.winupon.andframe.bigapple.http2.request.param.RequestParams;
import com.winupon.andframe.bigapple.http2.response.HttpResponse;
import com.winupon.andframe.bigapple.http2.response.handler.EntityToFile;
import com.winupon.andframe.bigapple.http2.response.handler.EntityToString;
import com.winupon.andframe.bigapple.http2.response.handler.HandlerCallBack;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 对默认实现的DefaultHttpClient简单封装
 * 
 * @author xuan
 */
public class HttpClientFace {
	private int connectionTimeout = 1000 * 60;
	private int readTimeout = 1000 * 60;
	private String encode = "utf-8";

	private HttpParams getHttpParams() {
		HttpParams httpParams = new HttpParams();
		httpParams.setConnectionTimeout(connectionTimeout);
		httpParams.setReadTimeout(readTimeout);
		httpParams.setEncode(encode);
		return httpParams;
	}

	// /////////////////////////////////////////GET请求部分////////////////////////////////////////////////////////////
	public String get(String url) throws IOException {
		return get(url, null);
	}

	/**
	 * GET请求
	 * 
	 * @param url
	 * @param requestParams
	 * @return
	 * @throws IOException
	 */
	public String get(String url, RequestParams requestParams)
			throws IOException {
		logUrlAndParams(url, requestParams);

		if (null != requestParams && !requestParams.getParamMap().isEmpty()) {
			url += "?" + requestParams.getUrlParamsString();
		}

		HttpClient client = null;
		HttpResponse httpResponse = null;
		String ret = null;
		try {
			client = new HttpClient();
			httpResponse = client.excute(new HttpGet(url), getHttpParams());

			if (HttpURLConnection.HTTP_OK == httpResponse.getResponseCode()) {
				ret = new EntityToString().toString(httpResponse
						.getHttpURLConnection());
			}
		} catch (Exception e) {
			LogUtils.e("", e);
		} finally {
			httpResponse.getHttpURLConnection().disconnect();// 有什么办法不要在这里close啊
		}

		if (HttpURLConnection.HTTP_OK != httpResponse.getResponseCode()) {
			// 请求不成功，抛出异常让上层知道处理
			throw new IOException(
					String.valueOf(httpResponse.getResponseCode()));
		}

		return ret;
	}

	// //////////////////////////////////////////下载部分//////////////////////////////////////////////////////////////
	public void download(String url, String target, HandlerCallBack callBack)
			throws Exception {
		download(url, target, callBack, false);
	}

	public void download(String url, String target) throws Exception {
		download(url, target, null, false);
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 * @param target
	 * @param callback
	 * @param isResume
	 * @return
	 * @throws IOException
	 */
	public File download(String url, String target, HandlerCallBack callback,
			boolean isResume) throws IOException {
		logUrlAndParams(url, null);

		HttpClient client = null;
		HttpResponse httpResponse = null;
		File ret = null;
		try {
			client = new HttpClient();
			httpResponse = client.excute(new HttpGet(url), getHttpParams());

			if (HttpURLConnection.HTTP_OK == httpResponse.getResponseCode()) {
				ret = new EntityToFile().toFile(
						httpResponse.getHttpURLConnection(), callback, target);
			}
		} catch (Exception e) {
			LogUtils.e("", e);
		} finally {
			httpResponse.getHttpURLConnection().disconnect();
		}

		if (HttpURLConnection.HTTP_OK != httpResponse.getResponseCode()) {
			// 请求不成功，抛出异常让上层知道处理
			throw new IOException(
					String.valueOf(httpResponse.getResponseCode()));
		}

		return ret;
	}

	// /////////////////////////////////////////////POST请求/////////////////////////////////////////////////////////
	public String post(String url) throws IOException {
		return post(url, null);
	}

	/**
	 * POST请求
	 * 
	 * @param url
	 * @param params
	 *            url携带参数
	 * @return
	 * @throws IOException
	 */
	public String post(String url, RequestParams requestParams)
			throws IOException {
		logUrlAndParams(url, requestParams);

		HttpClient client = null;
		HttpResponse httpResponse = null;
		String ret = null;
		try {
			client = new HttpClient();
			HttpParams httpParams = getHttpParams();
			httpParams.setUseCaches(false);
			httpResponse = client.excute(new HttpPost(url,
					new UrlEncodedFormEntity(requestParams)), httpParams);

			if (HttpURLConnection.HTTP_OK == httpResponse.getResponseCode()) {
				ret = new EntityToString().toString(httpResponse
						.getHttpURLConnection());
			}
		} catch (Exception e) {
			LogUtils.e("", e);
		} finally {
			httpResponse.getHttpURLConnection().disconnect();
		}

		if (HttpURLConnection.HTTP_OK != httpResponse.getResponseCode()) {
			// 请求不成功，抛出异常让上层知道处理
			throw new IOException(
					String.valueOf(httpResponse.getResponseCode()));
		}

		return ret;
	}

	// //////////////////////////////////////upload方法//////////////////////////////////////////////////////////
	/**
	 * POST请求TODO:
	 * 
	 * @param url
	 * @param params
	 *            url携带参数
	 * @return
	 * @throws IOException
	 */
	public String upload(String url, RequestParams requestParams)
			throws IOException {
		logUrlAndParams(url, requestParams);

		HttpClient client = null;
		HttpResponse httpResponse = null;
		String ret = null;
		try {
			client = new HttpClient();

			HttpParams httpParams = getHttpParams();
			httpParams.setUseCaches(false);
			httpParams.setContentType(HttpParams.CONTENT_TYPE_MUTIPART);// 模拟表单上传
			httpResponse = client.excute(new HttpPost(url, new MultipartEntity(
					requestParams)), httpParams);

			if (HttpURLConnection.HTTP_OK == httpResponse.getResponseCode()) {
				ret = new EntityToString().toString(httpResponse
						.getHttpURLConnection());
			}
		} catch (Exception e) {
			LogUtils.e("", e);
		} finally {
			httpResponse.getHttpURLConnection().disconnect();
		}

		return ret;
	}

	// 打出日志
	private void logUrlAndParams(String url, RequestParams requestParams) {
		if (null == requestParams) {
			LogUtils.d(url);
		} else {
			LogUtils.d(url + "[" + requestParams.toString() + "]");
		}
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

}
