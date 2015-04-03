package com.winupon.andframe.bigapple.http2.response;

import java.net.HttpURLConnection;

/**
 * Http请求的响应
 * 
 * @author xuan
 * 
 */
public class HttpResponse {
	private int responseCode;// 响应状态码
	private HttpURLConnection httpURLConnection;

	public int getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}

	public HttpURLConnection getHttpURLConnection() {
		return httpURLConnection;
	}

	public void setHttpURLConnection(HttpURLConnection httpURLConnection) {
		this.httpURLConnection = httpURLConnection;
	}

}
