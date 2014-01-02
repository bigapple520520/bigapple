/* 
 * @(#)URLHttpClient.java    Created on 2014-1-2
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.http2.urlhttpclient;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import android.util.Log;

import com.winupon.andframe.bigapple.io.IOUtils;

/**
 * 对UrlConnection客户端部分的封装
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-1-2 下午1:02:31 $
 */
public class URLHttpClient {
    private static final String TAG = "URLHttpClient";

    public static final boolean DEBUG = false;

    private static final int DEDAULT_BUFFER_SIZE = 8 * 1024;

    private int connectionTimeout = 1000 * 60;
    private int readTimeout = 1000 * 60;
    private int downloadReadTimeout = 1000 * 60 * 10;
    private String encode = "utf-8";

    // //////////////////////////////////////////下载部分//////////////////////////////////////////////////////////////
    public void download(String url, File file) throws Exception {
        download(url, file, null);
    }

    /**
     * 下载文件
     * 
     * @param downloadUrl
     * @param file
     * @return
     * @throws Exception
     */
    public void download(String url, File file, DownloadCallBack downloadCallBack) throws Exception {
        if (null == file) {
            throw new IOException("file not be null!");
        }

        File parentFile = file.getParentFile();
        if (parentFile.mkdirs()) {
            throw new IOException("file[" + parentFile.getPath() + "] can not create it's parent dir");
        }

        InputStream in = null;
        OutputStream out = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(downloadReadTimeout);

            // HttpURLConnection默认是gzip获取的，为了获取getContentLength，故设置不要gzip获取
            connection.setRequestProperty("Accept-Encoding", "identity");

            // 开始下载
            int total = connection.getContentLength();
            if (null != downloadCallBack) {
                downloadCallBack.onStart(total);
            }

            in = connection.getInputStream();
            out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] bs = new byte[DEDAULT_BUFFER_SIZE];
            int bytesReaded = 0;
            int addUpBytes = 0;
            while ((bytesReaded = in.read(bs)) != -1) {
                out.write(bs, 0, bytesReaded);
                addUpBytes += bytesReaded;

                // 下载中回调
                if (null != downloadCallBack) {
                    downloadCallBack.onLoading(total, addUpBytes);
                }
            }

            // 下载完成回调
            if (null != downloadCallBack) {
                downloadCallBack.onEnd();
            }
        }
        catch (Exception e) {
            Log.e(TAG, "download file from url[" + url + "] error", e);
            file.delete();
            throw e;
        }
        finally {
            connection.disconnect();
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(out);
        }
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
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

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
            Log.e(TAG, "get url[" + url + "] error");
            throw e;
        }
        finally {
            connection.disconnect();
            IOUtils.closeQuietly(reader);
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
            connection.setInstanceFollowRedirects(true);

            // 表示正文是内容是使用URLEncoder.encode进行编码
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 对参数编码放入请求体内
            if (null != requestParams && !requestParams.getParamMap().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map.Entry<String, String> e : requestParams.getParamMap().entrySet()) {
                    sb.append(e.getKey());
                    sb.append("=");
                    sb.append(URLEncoder.encode(e.getValue(), encode));
                    sb.append(" ");
                }

                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(sb.toString());
                out.flush();
                IOUtils.closeQuietly(out);
            }

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
            Log.e(TAG, "get url[" + url + "] error");
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
        if (DEBUG) {
            if (null == requestParams) {
                Log.d(TAG, url);
            }
            else {
                Log.d(TAG, url + "[" + requestParams.toString() + "]");
            }
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

    public int getDownloadReadTimeout() {
        return downloadReadTimeout;
    }

    public void setDownloadReadTimeout(int downloadReadTimeout) {
        this.downloadReadTimeout = downloadReadTimeout;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

}
