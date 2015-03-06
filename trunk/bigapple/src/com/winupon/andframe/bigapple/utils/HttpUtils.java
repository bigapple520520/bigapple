/* 
 * @(#)HttpUtils.java    Created on 2011-2-18
 * Copyright (c) 2011 ZDSoft Networks, Inc. All rights reserved.
 * $Id: HttpUtils.java 33829 2012-12-26 05:28:54Z xuan $
 */
package com.winupon.andframe.bigapple.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.text.TextUtils;

import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 提供访问HTTP服务的工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-3-25 上午9:24:40 $
 */
public abstract class HttpUtils {
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000 * 12;
    private static final int DEFAULT_READ_TIMEOUT = 1000 * 12;
    private static final int DEFAULT_DOWNLOAD_READ_TIMEOUT = 1000 * 60 * 10;// 下载超时
    private static final String DEFAULT_ENCODE = "utf-8";

    private static final int DEDAULT_BUFFER_SIZE = 8 * 1024;

    /**
     * GET请求，可带参数，使用UTF-8编码，连接超时和请求超时默认12s
     * 
     * @param url
     * @param params
     * @return
     * @throws IOException
     */
    public static String requestURL(String url, Map<String, String> params) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : params.entrySet()) {
            sb.append(e.getKey()).append("=").append(e.getValue()).append("&");
        }

        if (!params.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return requestURL(url + "?" + sb.toString(), DEFAULT_ENCODE, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * GET请求
     * 
     * @param url
     * @param encoding
     *            编码
     * @param connectionTimeout
     *            连接超时，-1表示默认
     * @param readTimeout
     *            读取超时，-1表示默认
     * @return
     * @throws IOException
     */
    public static String requestURL(String url, String encoding, int connectionTimeout, int readTimeout)
            throws IOException {
        if (TextUtils.isEmpty(encoding)) {
            encoding = DEFAULT_ENCODE;
        }

        if (-1 == connectionTimeout) {
            connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
        }

        if (-1 == readTimeout) {
            readTimeout = DEFAULT_READ_TIMEOUT;
        }

        LogUtils.d("GET请求地址：" + url);
        String result = null;
        BufferedReader reader = null;
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.setConnectTimeout(connectionTimeout);
            connection.setReadTimeout(readTimeout);

            // connection.connect();// 可以不要，在getInputStream中会默认调用
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), encoding),
                    DEDAULT_BUFFER_SIZE);
            String line = null;
            StringBuilder buffer = new StringBuilder();
            line = reader.readLine();
            if (line != null) {
                buffer.append(line);
                while ((line = reader.readLine()) != null) {
                    buffer.append("\n" + line);
                }
            }

            result = buffer.toString();
        }
        catch (IOException e) {
            LogUtils.e("请求地址[" + url + "] 错误，原因：" + e);
            throw e;
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e1) {
                    // Ignore
                }
            }
        }

        return result;
    }

    /**
     * POST请求，异常有外部自己处理
     * 
     * @param url
     * @param paramsMap
     * @param md5Key
     * @return
     */
    public static String post(String url, Map<String, String> paramsMap) throws Exception {
        LogUtils.d("Post url is：" + url + paramsMap.toString());

        String result = "";
        try {
            LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();
            for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }

            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODE));
            HttpResponse response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), DEFAULT_ENCODE);
            }
            else {
                throw new Exception(String.valueOf(response.getStatusLine().getStatusCode()), new Throwable(
                        "Post return error. The statusCode is not HttpStatus.SC_OK"));
            }
        }
        catch (Exception e) {
            throw new Exception(e);
        }

        return result;
    }

    /**
     * POST请求
     * 
     * @param url
     * @param paramsMap
     * @param md5Key
     * @return
     */
    public static String requestURLPost(String url, Map<String, String> paramsMap) {
        LogUtils.d("POST请求地址：" + url + paramsMap.toString());

        LinkedList<BasicNameValuePair> params = new LinkedList<BasicNameValuePair>();

        for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
            try {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            catch (Exception e) {
                LogUtils.e("添加参数错误，原因：" + e);
            }
        }

        String result = "";
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, DEFAULT_ENCODE));
            HttpResponse response = client.execute(httpPost);

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), DEFAULT_ENCODE);
            }
            else {
                LogUtils.e("请求返回失败");
            }
        }
        catch (Exception e) {
            LogUtils.e("请求异常错误，原因：" + e);
        }

        return result;
    }

    /**
     * 下载文件
     * 
     * @param downloadUrl
     * @param file
     * @return
     */
    public static void downloadURLToFile(String downloadUrl, File file) throws Exception {
        InputStream in = null;
        OutputStream out = null;
        try {
            File parentFile = file.getParentFile();
            if (!parentFile.exists() || !parentFile.isDirectory()) {
                parentFile.mkdirs();
            }

            URLConnection connection = new URL(downloadUrl).openConnection();
            connection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
            connection.setReadTimeout(DEFAULT_DOWNLOAD_READ_TIMEOUT);

            in = connection.getInputStream();
            out = new BufferedOutputStream(new FileOutputStream(file));
            byte[] bs = new byte[1024];
            int bytesReaded = 0;
            while ((bytesReaded = in.read(bs)) != -1) {
                out.write(bs, 0, bytesReaded);
            }
        }
        catch (Exception e) {
            LogUtils.e("下载文件异常错误，原因：" + e);
            file.delete();
            throw e;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {
                    // Ignore
                }
            }
            if (out != null) {
                try {
                    out.close();
                }
                catch (IOException e) {
                    // Ignore
                }
            }
        }
    }

}
