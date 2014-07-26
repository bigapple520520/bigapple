package com.winupon.andframe.bigapple.http2;

import java.io.BufferedOutputStream;
import java.net.HttpURLConnection;

import com.winupon.andframe.bigapple.http2.params.HttpParams;
import com.winupon.andframe.bigapple.http2.request.HttpPost;
import com.winupon.andframe.bigapple.http2.request.HttpRequest;
import com.winupon.andframe.bigapple.http2.request.entity.RequestEntiy;
import com.winupon.andframe.bigapple.http2.response.HttpResponse;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * Http操作实现
 * 
 * @author xuan
 * 
 */
public class HttpClient {

	/**
	 * 执行请求
	 * 
	 * @param httpRequest
	 * @param httpParams
	 * @return
	 */
	public HttpResponse excute(HttpRequest httpRequest, HttpParams httpParams) {
		HttpURLConnection connection = null;
		HttpResponse httpResponse = null;
		try {
			// 获取连接
			connection = (HttpURLConnection) httpRequest.getUrl()
					.openConnection();

			// 封装请求报头参数
			connection.setRequestMethod(httpRequest.getMethod());
			httpParams.injectToConnection(connection);

			// 如果是POST，设置内容到请求体
			if (httpRequest instanceof HttpPost) {
				RequestEntiy requestEntiy = ((HttpPost) httpRequest)
						.getRequestEntiy();
				if (null != requestEntiy) {
					requestEntiy.writeTo(new BufferedOutputStream(connection
							.getOutputStream()));
				}
			}

			// 封装响应对象
			httpResponse = new HttpResponse();
			httpResponse.setResponseCode(connection.getResponseCode());
			httpResponse.setHttpURLConnection(connection);
		} catch (Exception e) {
			LogUtils.e("", e);
		} finally {
			// connection.disconnect();
		}

		return httpResponse;
	}

}
