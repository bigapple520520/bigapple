/* 
 * @(#)URLHttpClient.java    Created on 2014-1-2
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.http2.urlhttpclient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.winupon.andframe.bigapple.http2.urlhttpclient.core.RequestParams;
import com.winupon.andframe.bigapple.http2.urlhttpclient.entity.MultipartEntity;
import com.winupon.andframe.bigapple.http2.urlhttpclient.entity.UrlEncodedFormEntity;
import com.winupon.andframe.bigapple.http2.urlhttpclient.handler.FileResultHandler;
import com.winupon.andframe.bigapple.http2.urlhttpclient.handler.ResultCallBack;
import com.winupon.andframe.bigapple.http2.urlhttpclient.handler.StringResultHandler;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 对UrlConnection客户端部分的封装
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-1-2 下午1:02:31 $
 */
public class URLHttpClient {
    private int connectionTimeout = 1000 * 60;
    private int readTimeout = 1000 * 60;
    private String encode = "utf-8";

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
            connection.setRequestProperty("Accept-Encoding", "identity");

            result = new StringResultHandler().handleResult(connection, null, encode);
        }
        catch (IOException e) {
            LogUtils.e("get url[" + url + "] error");
            throw e;
        }
        finally {
            connection.disconnect();
        }

        return result;
    }

    // //////////////////////////////////////////下载部分//////////////////////////////////////////////////////////////
    public void download(String url, String target, ResultCallBack callBack) throws Exception {
        download(url, target, callBack, false);
    }

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
    public File download(String url, String target, ResultCallBack callBack, boolean isResume) throws Exception {
        logUrlAndParams(url, null);

        File result = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);
            connection.setRequestProperty("Accept-Encoding", "identity");// 默认HttpURLConnection使用gzip压缩的，所以调用这个使不gzip，这样
                                                                         // getContentLength才能得到准确的长度

            result = new FileResultHandler().handleResult(connection, callBack, target, isResume);
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
    public String post(String url, RequestParams requestParams) throws IOException {
        logUrlAndParams(url, requestParams);

        String result = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept-Encoding", "identity");

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// 默认post普通参数

            // 对参数编码放入请求体内
            UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(requestParams, encode);
            urlEncodedFormEntity.writeTo(new BufferedOutputStream(connection.getOutputStream()));

            result = new StringResultHandler().handleResult(connection, null, encode);
        }
        catch (IOException e) {
            LogUtils.e("post url[" + url + "] error");
            throw e;
        }
        finally {
            connection.disconnect();
        }

        return result;
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
    public String upload(String url, RequestParams requestParams) throws IOException {
        logUrlAndParams(url, requestParams);

        String result = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setRequestProperty("Accept-Encoding", "identity");
            connection.setChunkedStreamingMode(0);// 防止大文件OOM，设置成0使用系统默认缓存大小

            MultipartEntity multipartEntity = new MultipartEntity(requestParams);
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + multipartEntity.getBoundary());
            multipartEntity.writeTo(new BufferedOutputStream(connection.getOutputStream()));

            result = new StringResultHandler().handleResult(connection, null, encode);
        }
        catch (IOException e) {
            LogUtils.e("upload url[" + url + "] error");
            throw e;
        }
        finally {
            connection.disconnect();
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
