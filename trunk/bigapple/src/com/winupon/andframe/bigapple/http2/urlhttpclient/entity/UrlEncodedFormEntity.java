package com.winupon.andframe.bigapple.http2.urlhttpclient.entity;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;

import android.text.TextUtils;

import com.winupon.andframe.bigapple.http2.urlhttpclient.core.RequestParams;
import com.winupon.andframe.bigapple.io.IOUtils;

/**
 * 对应表单普通参数上传，post体中的内容
 * 
 * @author xuan
 */
public class UrlEncodedFormEntity implements Entity {
	private String encode = "utf-8";

	private final RequestParams requestParams;

	public UrlEncodedFormEntity(RequestParams requestParams, String encode) {
		this.requestParams = requestParams;
		this.encode = encode;
	}

	/**
	 * 把内容存入输出流中
	 * 
	 * @param outstream
	 * @throws IOException
	 */
	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		Map<String, String> paramMap = null;
		if (null != requestParams) {
			paramMap = requestParams.getParamMap();
		}

		if (null != paramMap && !paramMap.isEmpty()
				&& !TextUtils.isEmpty(encode)) {

			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> e : paramMap.entrySet()) {
				sb.append(URLEncoder.encode(e.getKey(), encode));
				sb.append("=");
				sb.append(URLEncoder.encode(e.getValue(), encode));
				sb.append("&");
			}

			DataOutputStream out = new DataOutputStream(outstream);
			out.writeBytes(sb.toString());
			out.flush();
			IOUtils.closeQuietly(out);
		}
	}

}
