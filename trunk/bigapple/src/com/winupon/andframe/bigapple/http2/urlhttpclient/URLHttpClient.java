/* 
 * @(#)URLHttpClient.java    Created on 2014-1-2
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.http2.urlhttpclient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import com.winupon.andframe.bigapple.http2.urlhttpclient.handler.EntityCallBack;
import com.winupon.andframe.bigapple.http2.urlhttpclient.handler.FileEntityHandler;
import com.winupon.andframe.bigapple.http2.urlhttpclient.handler.StringEntityHandler;
import com.winupon.andframe.bigapple.io.IOUtils;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 对UrlConnection客户端部分的封装
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-1-2 下午1:02:31 $
 */
public class URLHttpClient {
    private static final int DEDAULT_BUFFER_SIZE = 8 * 1024;

    private int connectionTimeout = 1000 * 60;
    private int readTimeout = 1000 * 60;
    private String encode = "utf-8";

    // //////////////////////////////////////////下载部分//////////////////////////////////////////////////////////////
    public void download(String url, String target) throws Exception {
        download(url, target, null, false);
    }

    /**
     * 下载文件
     * 
     * @param downloadUrl
     * @param file
     * @return
     * @throws Exception
     */
    public File download(String url, String target, EntityCallBack callBack, boolean isResume) throws Exception {
        logUrlAndParams(url, null);

        File result = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            result = new FileEntityHandler().handleEntity(connection, callBack, target, isResume);
        }
        catch (IOException e) {
            LogUtils.e("download url[" + url + "] error", e);
            throw e;
        }
        finally {
            connection.disconnect();
        }

        return result;
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
    public String get(String url, RequestParams requestParams) throws IOException {
        logUrlAndParams(url, requestParams);

        if (null != requestParams && !requestParams.getParamMap().isEmpty()) {
            url += "?" + requestParams.getUrlParamsString();
        }

        String result = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            result = new StringEntityHandler().handleEntity(connection, null, encode);
        }
        catch (IOException e) {
            LogUtils.d("get url[" + url + "] error");
            throw e;
        }
        finally {
            connection.disconnect();
        }

        return result;
    }

    // /////////////////////////////////////////////POST请求/////////////////////////////////////////////////////////
    public String post(String url) throws IOException {
        return post(url, null);
    }

    /**
     * POST请求TODO:
     * 
     * @param url
     * @param params
     *            url携带参数
     * @return
     * @throws IOException
     */
    public String post(String url, RequestParams requestParams) throws IOException {
        logUrlAndParams(url, requestParams);

        String result = null;
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);

            if (!requestParams.getFileParamMap().isEmpty()) {// 含有文件上传
                // conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            }
            else {// 普通参数表单提交
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                // 对参数编码放入请求体内
                if (null != requestParams && !requestParams.getParamMap().isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (Map.Entry<String, String> e : requestParams.getParamMap().entrySet()) {
                        sb.append(e.getKey());
                        sb.append("=");
                        sb.append(URLEncoder.encode(e.getValue(), encode));
                        sb.append("&");
                    }

                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                    out.writeBytes(sb.toString());
                    out.flush();
                    IOUtils.closeQuietly(out);
                }
            }

            // 读取结果
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encode), DEDAULT_BUFFER_SIZE);
            StringBuilder buffer = new StringBuilder();
            String line = reader.readLine();
            if (line != null) {
                buffer.append(line);
                while ((line = reader.readLine()) != null) {
                    buffer.append("\n" + line);
                }
            }

            result = buffer.toString();
        }
        catch (IOException e) {
            LogUtils.e("get url[" + url + "] error");
            throw e;
        }
        finally {
            connection.disconnect();
            IOUtils.closeQuietly(reader);
        }

        return result;
    }

    // 打出日志
    private void logUrlAndParams(String url, RequestParams requestParams) {
        if (null == requestParams) {
            LogUtils.d(url);
        }
        else {
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
