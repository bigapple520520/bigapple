package com.winupon.andframe.bigapple.http2;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Http的请求
 * 
 * @author xuan
 */
public class HttpRequest {
    public static final String HTTP_GET = "GET";
    public static final String HTTP_POST = "POST";

    /** 请求方法 */
    private String method;
    /** 请求地址 */
    private URL url;

    /**
     * 构造方法（默认GET）
     * 
     * @throws MalformedURLException
     */
    public HttpRequest() throws MalformedURLException {
        this.method = HTTP_GET;
    }

    /**
     * 构造方法
     * 
     * @param urlStr
     *            访问地址
     * @param method
     *            访问方式
     * @throws MalformedURLException
     */
    public HttpRequest(String urlStr, String method) throws MalformedURLException {
        url = new URL(urlStr);
        this.method = method;
    }

    /**
     * 构造方法（默认GET）
     * 
     * @param urlStr
     *            访问地址
     * @throws MalformedURLException
     */
    public HttpRequest(String urlStr) throws MalformedURLException {
        url = new URL(urlStr);
        this.method = HTTP_GET;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public URL getUrl() {
        return url;
    }

}
