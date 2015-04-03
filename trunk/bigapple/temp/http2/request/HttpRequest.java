package com.winupon.andframe.bigapple.http2.request;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Http的请求
 * 
 * @author xuan
 */
public class HttpRequest {
	public static final String HTTP_GET = "GET";
	public static final String HTTP_POST = "POST";

	private String method;// 请求方法
	private final URL url;// 请求地址

	public HttpRequest(String urlStr) throws MalformedURLException {
		url = new URL(urlStr);
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public URL getUrl() {
		return url;
	}

}
