package com.winupon.andframe.bigapple.http2.request;

import java.net.MalformedURLException;

import com.winupon.andframe.bigapple.http2.request.HttpRequest;
import com.winupon.andframe.bigapple.http2.request.entity.RequestEntiy;

/**
 * GET请求方法
 * 
 * @author xuan
 */
public class HttpPost extends HttpRequest {
	private RequestEntiy requestEntiy;

	public HttpPost(String urlStr) throws MalformedURLException {
		super(urlStr);
	}

	public HttpPost(String urlStr, RequestEntiy requestEntiy)
			throws MalformedURLException {
		super(urlStr);
		this.requestEntiy = requestEntiy;
	}

	public RequestEntiy getRequestEntiy() {
		return requestEntiy;
	}

	public void setRequestEntiy(RequestEntiy requestEntiy) {
		this.requestEntiy = requestEntiy;
	}

}
