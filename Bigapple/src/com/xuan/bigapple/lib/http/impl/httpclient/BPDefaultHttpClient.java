package com.xuan.bigapple.lib.http.impl.httpclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;

import com.xuan.bigapple.lib.http.BPHttpClient;
import com.xuan.bigapple.lib.http.BPRequest;
import com.xuan.bigapple.lib.http.BPResponse;
import com.xuan.bigapple.lib.http.helper.BPHttpConfig;
import com.xuan.bigapple.lib.http.helper.FileWraper;
import com.xuan.bigapple.lib.http.impl.httpclient.handler.FileResultHandler;
import com.xuan.bigapple.lib.http.impl.httpclient.handler.StringResultHandler;
import com.xuan.bigapple.lib.utils.log.LogUtils;

/**
 * 使用AndroidHttpClient实现
 * 
 * @author xuan
 */
public class BPDefaultHttpClient implements BPHttpClient {

	@Override
	public BPResponse postJson(String url, BPRequest request,
			BPHttpConfig config) throws Exception {
		BPResponse bpResponse = new BPResponse();

		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		// 设置头部信息
		if (null != request.getHeaderMap()) {
			for (Map.Entry<String, String> entry : request.getHeaderMap()
					.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
		}

		// 设置请求体内容
		HttpEntity httpEntity = getStringEntity(request, config);
		if (null == httpEntity) {
			bpResponse.setStatusCode(-1);
			bpResponse.setReasonPhrase("设置请求体错误");
			return bpResponse;
		}
		httpPost.setEntity(httpEntity);
		return invoke(client, httpPost, request, config, false);
	}

	@Override
	public BPResponse get(String url, BPRequest bpRequest,
			BPHttpConfig httConfig) throws TimeoutException {
		if (BPHttpConfig.DEBUG) {
			LogUtils.d("Get url is：" + url + "?" + bpRequest.toString());
		}

		// apache HttpClient
		HttpClient httpClient = new DefaultHttpClient();
		// 设置GET参数
		if (!bpRequest.getParamMap().isEmpty()) {
			url = "?" + bpRequest.getGetParamsUrl();
		}
		HttpGet httpGet = new HttpGet(url);

		return invoke(httpClient, httpGet, bpRequest, httConfig, false);
	}

	@Override
	public BPResponse post(String url, BPRequest bpRequest,
			BPHttpConfig httpConfig) throws TimeoutException {
		if (BPHttpConfig.DEBUG) {
			LogUtils.d("Post url is：" + url + "|||||post:"
					+ bpRequest.toString());
		}

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		// 设置请求体内容
		HttpEntity httpEntity = getEntity(bpRequest, httpConfig);
		if (null == httpEntity) {
			BPResponse bpResponse = new BPResponse();
			bpResponse.setStatusCode(-1);
			bpResponse.setReasonPhrase("设置请求体错误");
			return bpResponse;
		}
		httpPost.setEntity(httpEntity);

		return invoke(httpClient, httpPost, bpRequest, httpConfig, false);
	}

	@Override
	public BPResponse dowload(String url, BPRequest bpRequest,
			BPHttpConfig httConfig) throws TimeoutException {
		if (BPHttpConfig.DEBUG) {
			LogUtils.d("Dowload url is：" + url + "?" + bpRequest.toString());
		}

		// apache HttpClient
		HttpClient httpClient = new DefaultHttpClient();
		// 设置GET参数
		if (!bpRequest.getParamMap().isEmpty()) {
			url = "?" + bpRequest.getGetParamsUrl();
		}
		HttpGet httpGet = new HttpGet(url);
		return invoke(httpClient, httpGet, bpRequest, httConfig, true);
	}

	// 发起请求
	private BPResponse invoke(HttpClient httpClient,
			HttpUriRequest httpUriRequest, BPRequest bpRequest,
			BPHttpConfig httConfig, boolean isDownload) throws TimeoutException {

		BPResponse bpResponse = new BPResponse();
		try {
			// 设置连接超时
			httpClient.getParams().setParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT,
					httConfig.getConnectionTimeout());
			// 设置读取超时
			httpClient.getParams()
					.setParameter(HttpConnectionParams.SO_TIMEOUT,
							httConfig.getReadTimeout());
			// 设置头部信息
			if (!bpRequest.getHeaderMap().isEmpty()) {
				for (Map.Entry<String, String> entry : bpRequest.getHeaderMap()
						.entrySet()) {
					httpUriRequest.addHeader(entry.getKey(), entry.getValue());
				}
			}

			// 执行请求
			HttpResponse response = httpClient.execute(httpUriRequest);

			// 获取返回信息
			StatusLine statusLine = response.getStatusLine();
			bpResponse = new BPResponse();
			bpResponse.setStatusCode(statusLine.getStatusCode());
			bpResponse.setReasonPhrase(statusLine.getReasonPhrase());
			if (HttpStatus.SC_OK == bpResponse.getStatusCode()) {
				if (!isDownload) {
					// 普通请求，非下载文件
					if (null == httConfig.getResultHandlerCallback()) {
						// 不需要回调，就用自带的String编码处理
						bpResponse.setResultStr(EntityUtils.toString(
								response.getEntity(), httConfig.getEncode()));
					} else {
						// 需要回调，就用自定义的String编码处理
						bpResponse.setResultStr(new StringResultHandler()
								.handleEntity(response.getEntity(),
										httConfig.getResultHandlerCallback(),
										httConfig.getEncode()));
					}
				} else {
					// 下载文件
					bpResponse.setResultFile(new FileResultHandler()
							.handleEntity(response.getEntity(),
									httConfig.getResultHandlerCallback(),
									httConfig.getDownloadFileName(),
									httConfig.isDownloadIsSupportResume()));
				}
			}
		} catch (IOException te) {
			throw new TimeoutException(te.getMessage());
		} catch (Exception e) {
			bpResponse.setStatusCode(-1);
			bpResponse.setReasonPhrase(e.getMessage());
		}

		return bpResponse;
	}

	private StringEntity getStringEntity(BPRequest request, BPHttpConfig config) {
		// 编码
		StringEntity se = null;
		try {
			se = new StringEntity(request.getBodyJson(), config.getEncode());
		} catch (UnsupportedEncodingException e) {
			LogUtils.e(e.getMessage(), e);
		}
		se.setContentEncoding(config.getEncode());
		return se;
	}

	// 获取请求体
	private HttpEntity getEntity(BPRequest bpRequest, BPHttpConfig httpConfig) {
		HttpEntity entity = null;

		if (!bpRequest.getFileParamMap().isEmpty()) {
			MultipartEntity multipartEntity = new MultipartEntity();

			// 设置普通参数
			for (ConcurrentHashMap.Entry<String, String> entry : bpRequest
					.getParamMap().entrySet()) {
				multipartEntity.addPart(entry.getKey(), entry.getValue());
			}

			// 设置文件参数
			int currentIndex = 0;
			int lastIndex = bpRequest.getFileParamMap().entrySet().size() - 1;
			for (ConcurrentHashMap.Entry<String, FileWraper> entry : bpRequest
					.getFileParamMap().entrySet()) {
				FileWraper file = entry.getValue();
				if (file.inputStream != null) {
					boolean isLast = (currentIndex == lastIndex);
					if (file.contentType != null) {
						multipartEntity.addPart(entry.getKey(),
								file.getFileName(), file.inputStream,
								file.contentType, isLast);
					} else {
						multipartEntity.addPart(entry.getKey(),
								file.getFileName(), file.inputStream, isLast);
					}
				}
				currentIndex++;
			}

			entity = multipartEntity;
		} else {
			try {
				entity = new UrlEncodedFormEntity(getParamsList(bpRequest),
						httpConfig.getEncode());
			} catch (UnsupportedEncodingException e) {
				LogUtils.e(e.getMessage(), e);
			}
		}

		return entity;
	}

	// 获取普通参数列表
	private List<BasicNameValuePair> getParamsList(BPRequest bpRequest) {
		List<BasicNameValuePair> basicNameValuePairList = new LinkedList<BasicNameValuePair>();

		for (ConcurrentHashMap.Entry<String, String> entry : bpRequest
				.getParamMap().entrySet()) {
			basicNameValuePairList.add(new BasicNameValuePair(entry.getKey(),
					entry.getValue()));
		}

		return basicNameValuePairList;
	}

}
