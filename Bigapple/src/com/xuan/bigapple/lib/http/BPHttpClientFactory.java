package com.xuan.bigapple.lib.http;

import com.xuan.bigapple.lib.http.helper.BPHttpConfig;
import com.xuan.bigapple.lib.http.impl.httpclient.BPDefaultHttpClient;

/**
 * BPHttpClient实现工厂类
 * 
 * @author xuan
 */
public abstract class BPHttpClientFactory {
	public final static int CLIENT_IMPL_HTTPCLIENT = 1;

	/**
	 * 返回默认实现方式
	 * 
	 * @return
	 */
	public static BPHttpClient getDefaultCliet(BPHttpConfig config) {
		BPHttpClient client = null;
		if (CLIENT_IMPL_HTTPCLIENT == config.getImplType()) {
			client = getHttpClietImpl();
		} else {
			client = getHttpClietImpl();
		}
		return client;
	}

	/**
	 * android自带的HttpClient实现方式
	 * 
	 * @return
	 */
	public static BPHttpClient getHttpClietImpl() {
		return new BPDefaultHttpClient();
	}

}
