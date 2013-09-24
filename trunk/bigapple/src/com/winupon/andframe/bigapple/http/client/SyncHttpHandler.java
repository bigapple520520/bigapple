package com.winupon.andframe.bigapple.http.client;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import com.winupon.andframe.bigapple.http.handler.StringEntityHandler;

/**
 * 同步Http处理
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:54:36 $
 */
public class SyncHttpHandler {
    private final AbstractHttpClient client;
    private final HttpContext context;
    private final StringEntityHandler stringEntityHandler = new StringEntityHandler();

    private int executionCount = 0;// 记录重连次数
    private final String charset;

    public SyncHttpHandler(AbstractHttpClient client, HttpContext context, String charset) {
        this.client = client;
        this.context = context;
        this.charset = charset;
    }

    /**
     * 发送请求
     * 
     * @param params
     * @return
     */
    public Object sendRequest(HttpUriRequest... params) {
        try {
            return makeRequestWithRetries(params[0]);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    // 实际处理请求
    private Object makeRequestWithRetries(HttpUriRequest request) throws IOException {
        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        while (retry) {
            try {
                HttpResponse response = client.execute(request, context);
                return stringEntityHandler.handleEntity(response.getEntity(), null, charset);
            }
            catch (UnknownHostException e) {
                cause = e;
                retry = retryHandler.retryRequest(cause, ++executionCount, context);
            }
            catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(cause, ++executionCount, context);
            }
            catch (NullPointerException e) {
                cause = new IOException("NPE in HttpClient" + e.getMessage());
                retry = retryHandler.retryRequest(cause, ++executionCount, context);
            }
            catch (Exception e) {
                cause = new IOException("Exception" + e.getMessage());
                retry = retryHandler.retryRequest(cause, ++executionCount, context);
            }
        }

        if (cause != null) {
            throw cause;
        }
        else {
            throw new IOException("未知网络错误");
        }
    }

}
