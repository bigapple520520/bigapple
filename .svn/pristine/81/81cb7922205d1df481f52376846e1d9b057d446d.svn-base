package com.winupon.andframe.bigapple.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

import com.winupon.andframe.bigapple.http.client.HttpHandler;
import com.winupon.andframe.bigapple.http.client.RequestCallBack;
import com.winupon.andframe.bigapple.http.client.RequestParams;
import com.winupon.andframe.bigapple.http.client.SyncHttpHandler;

/**
 * Http请求工具类，注意在客户端使用时，考虑保持单例
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:56:17 $
 */
public class AnHttpUtils {
    private static final int DEFAULT_SOCKET_BUFFER_SIZE = 8 * 1024; // 8KB
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    private static int DEFAULT_MAX_CONNECTIONS = 10;
    private static int DEFAULT_GET_CONNECTION_TIMEOUT = 10 * 1000;
    private static int DEFAULT_MAX_CONNECTIONS_PERROUTE = 10;

    private static int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
    private static int DEFAULT_SO_TIMEOUT = 10 * 1000;
    private static int DEFAULT_MAX_RETRIES = 5;

    private static int maxConnections = DEFAULT_MAX_CONNECTIONS;// 最大连接个数
    private static int getConnectionTimeout = DEFAULT_GET_CONNECTION_TIMEOUT;// 获取连接数的最大等待时间，默认10秒
    private static int maxConnectionsPerRoute = DEFAULT_MAX_CONNECTIONS_PERROUTE;// 每个路由最大连接数

    private static int socketTimeout = DEFAULT_SOCKET_TIMEOUT; // 连接超时时间
    private static int soTimeout = DEFAULT_SO_TIMEOUT;// 读取超时时间
    private static int maxRetries = DEFAULT_MAX_RETRIES;// 错误尝试次数，错误异常表请在RetryHandler添加

    private final DefaultHttpClient httpClient;
    private final HttpContext httpContext;
    private String charset = "utf-8";

    private final Map<String, String> clientHeaderMap;

    public AnHttpUtils() {
        BasicHttpParams httpParams = new BasicHttpParams();

        ConnManagerParams.setMaxTotalConnections(httpParams, maxConnections);// 最大连接数
        ConnManagerParams.setTimeout(httpParams, getConnectionTimeout);// 获取连接数的最大等待时间
        ConnManagerParams.setMaxConnectionsPerRoute(httpParams, new ConnPerRouteBean(maxConnectionsPerRoute));// 每个路由最大连接数

        HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
        HttpConnectionParams.setSoTimeout(httpParams, soTimeout);
        HttpConnectionParams.setTcpNoDelay(httpParams, true);// true表示不启用nagle算法
        HttpConnectionParams.setSocketBufferSize(httpParams, DEFAULT_SOCKET_BUFFER_SIZE);

        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);// 支持多线程访问安全

        httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        httpClient = new DefaultHttpClient(cm, httpParams);
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            public void process(HttpRequest request, HttpContext context) {
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }

                for (String header : clientHeaderMap.keySet()) {
                    request.addHeader(header, clientHeaderMap.get(header));
                }
            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }

                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });

        httpClient.setHttpRequestRetryHandler(new RetryHandler(maxRetries));
        clientHeaderMap = new HashMap<String, String>();
    }

    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    public HttpContext getHttpContext() {
        return this.httpContext;
    }

    public void configCharset(String charSet) {
        if (charSet != null && charSet.trim().length() != 0) {
            this.charset = charSet;
        }
    }

    public void configCookieStore(CookieStore cookieStore) {
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public void configUserAgent(String userAgent) {
        HttpProtocolParams.setUserAgent(this.httpClient.getParams(), userAgent);
    }

    /**
     * 设置网络连接超时时间，默认为10秒钟，这里粗略的设置了，同时设置了：1、获取连接超时时间 2、连接超时时间 3、读取数据时间
     * 
     * @param timeout
     */
    public void configTimeout(int timeout) {
        final HttpParams httpParams = this.httpClient.getParams();
        ConnManagerParams.setTimeout(httpParams, timeout);
        HttpConnectionParams.setSoTimeout(httpParams, timeout);
        HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
    }

    /**
     * 设置https请求时 的 SSLSocketFactory
     * 
     * @param sslSocketFactory
     */
    public void configSSLSocketFactory(SSLSocketFactory sslSocketFactory) {
        Scheme scheme = new Scheme("https", sslSocketFactory, 443);
        this.httpClient.getConnectionManager().getSchemeRegistry().register(scheme);
    }

    /**
     * 配置错误重试次数
     * 
     * @param retry
     */
    public void configRequestExecutionRetryCount(int count) {
        this.httpClient.setHttpRequestRetryHandler(new RetryHandler(count));
    }

    /**
     * 添加http请求头
     * 
     * @param header
     * @param value
     */
    public void addHeader(String header, String value) {
        clientHeaderMap.put(header, value);
    }

    // ///////////////////////////////////////////////异步GET方法//////////////////////////////////////////////////////
    public void get(String url, RequestCallBack<? extends Object> callBack) {
        get(url, null, callBack);
    }

    public void get(String url, RequestParams params, RequestCallBack<? extends Object> callBack) {
        sendRequest(httpClient, httpContext, new HttpGet(getUrlWithQueryString(url, params)), null, callBack);
    }

    public void get(String url, Header[] headers, RequestParams params, RequestCallBack<? extends Object> callBack) {
        HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
        if (headers != null) {
            request.setHeaders(headers);
        }
        sendRequest(httpClient, httpContext, request, null, callBack);
    }

    // ///////////////////////////////////////////////同步GET方法//////////////////////////////////////////////////////
    public Object getSync(String url) {
        return getSync(url, null);
    }

    public Object getSync(String url, RequestParams params) {
        HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
        return sendSyncRequest(httpClient, httpContext, request, null);
    }

    public Object getSync(String url, Header[] headers, RequestParams params) {
        HttpUriRequest request = new HttpGet(getUrlWithQueryString(url, params));
        if (headers != null) {
            request.setHeaders(headers);
        }
        return sendSyncRequest(httpClient, httpContext, request, null);
    }

    // ///////////////////////////////////////////////异步POST方法//////////////////////////////////////////////////////
    public void post(String url, RequestCallBack<? extends Object> callBack) {
        post(url, null, callBack);
    }

    public void post(String url, RequestParams params, RequestCallBack<? extends Object> callBack) {
        post(url, paramsToEntity(params), null, callBack);
    }

    public void post(String url, HttpEntity entity, String contentType, RequestCallBack<? extends Object> callBack) {
        sendRequest(httpClient, httpContext, addEntityToRequestBase(new HttpPost(url), entity), contentType, callBack);
    }

    public <T> void post(String url, Header[] headers, RequestParams params, String contentType,
            RequestCallBack<T> callBack) {
        HttpEntityEnclosingRequestBase request = new HttpPost(url);
        if (params != null) {
            request.setEntity(paramsToEntity(params));
        }
        if (headers != null) {
            request.setHeaders(headers);
        }
        sendRequest(httpClient, httpContext, request, contentType, callBack);
    }

    public void post(String url, Header[] headers, HttpEntity entity, String contentType,
            RequestCallBack<? extends Object> callBack) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPost(url), entity);
        if (headers != null) {
            request.setHeaders(headers);
        }
        sendRequest(httpClient, httpContext, request, contentType, callBack);
    }

    // ///////////////////////////////////////////////同步GET方法//////////////////////////////////////////////////////
    public Object postSync(String url) {
        return postSync(url, null);
    }

    public Object postSync(String url, RequestParams params) {
        return postSync(url, paramsToEntity(params), null);
    }

    public Object postSync(String url, HttpEntity entity, String contentType) {
        return sendSyncRequest(httpClient, httpContext, addEntityToRequestBase(new HttpPost(url), entity), contentType);
    }

    public Object postSync(String url, Header[] headers, RequestParams params, String contentType) {
        HttpEntityEnclosingRequestBase request = new HttpPost(url);
        if (params != null) {
            request.setEntity(paramsToEntity(params));
        }
        if (headers != null) {
            request.setHeaders(headers);
        }
        return sendSyncRequest(httpClient, httpContext, request, contentType);
    }

    public Object postSync(String url, Header[] headers, HttpEntity entity, String contentType) {
        HttpEntityEnclosingRequestBase request = addEntityToRequestBase(new HttpPost(url), entity);
        if (headers != null) {
            request.setHeaders(headers);
        }
        return sendSyncRequest(httpClient, httpContext, request, contentType);
    }

    // /////////////////////////////////////////异步GET下载方法///////////////////////////////////////////////////////
    public HttpHandler<File> download(String url, String target, RequestCallBack<File> callback) {
        return download(url, null, target, false, callback);
    }

    public HttpHandler<File> download(String url, String target, boolean isResume, RequestCallBack<File> callback) {
        return download(url, null, target, isResume, callback);
    }

    public HttpHandler<File> download(String url, RequestParams params, String target, RequestCallBack<File> callback) {
        return download(url, params, target, false, callback);
    }

    public HttpHandler<File> download(String url, RequestParams params, String target, boolean isResume,
            RequestCallBack<File> callback) {
        final HttpGet get = new HttpGet(getUrlWithQueryString(url, params));
        HttpHandler<File> handler = new HttpHandler<File>(httpClient, httpContext, callback, charset);
        handler.execute(get, target, isResume);
        return handler;
    }

    // ///////////////////////////////////////////////内部方法///////////////////////////////////////////////////////
    /**
     * 异步请求
     * 
     * @param client
     * @param httpContext
     * @param uriRequest
     * @param contentType
     * @param ajaxCallBack
     */
    protected <T> void sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest,
            String contentType, RequestCallBack<T> ajaxCallBack) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        new HttpHandler<T>(client, httpContext, ajaxCallBack, charset).execute(uriRequest);
    }

    /**
     * 同步请求
     * 
     * @param client
     * @param httpContext
     * @param uriRequest
     * @param contentType
     * @return
     */
    protected Object sendSyncRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest,
            String contentType) {
        if (contentType != null) {
            uriRequest.addHeader("Content-Type", contentType);
        }

        return new SyncHttpHandler(client, httpContext, charset).sendRequest(uriRequest);
    }

    /**
     * 拼接参数,UTF-8编码
     * 
     * @param url
     * @param params
     * @return
     */
    private static String getUrlWithQueryString(String url, RequestParams params) {
        if (params != null) {
            String paramString = params.getParamString();
            url += "?" + paramString;
        }
        return url;
    }

    /**
     * 获取HttpEntity
     * 
     * @param params
     * @return
     */
    private HttpEntity paramsToEntity(RequestParams params) {
        HttpEntity entity = null;

        if (params != null) {
            entity = params.getEntity();
        }

        return entity;
    }

    /**
     * 把HttpEntity对象设置到request中去
     * 
     * @param requestBase
     * @param entity
     * @return
     */
    private HttpEntityEnclosingRequestBase addEntityToRequestBase(HttpEntityEnclosingRequestBase requestBase,
            HttpEntity entity) {
        if (entity != null) {
            requestBase.setEntity(entity);
        }

        return requestBase;
    }

    /**
     * 下载压缩文件时封装实体
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-12 下午5:29:51 $
     */
    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }

}
