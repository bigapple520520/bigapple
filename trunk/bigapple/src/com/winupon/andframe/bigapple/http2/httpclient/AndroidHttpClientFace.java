/* 
 * @(#)AndroidHttpClientFace.java    Created on 2013-12-31
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.http2.httpclient;

import java.util.LinkedList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * 对AndroidHttpClient的封装，使用户调用更加的简单
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-12-31 下午7:35:52 $
 */
public class AndroidHttpClientFace {
    private static final String TAG = "AndroidHttpClientFace";

    private static final String DEFAULT_ENCODE = "utf-8";

    /**
     * post请求
     * 
     * @param url
     * @param paramsMap
     * @return
     */
    public static String requestURLPost(String url, Map<String, String> paramsMap) {
        Log.v(TAG, url + paramsMap.toString());

        LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            try {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            catch (Exception e) {
                Log.e(TAG, "", e);
            }
        }

        String result = "";
        try {
            AndroidHttpClient client = AndroidHttpClient.newInstance("");
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODE));
            HttpResponse response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), DEFAULT_ENCODE);
            }
            else {
                Log.e(TAG, "请求返回失败");
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }

        return result;
    }

}
