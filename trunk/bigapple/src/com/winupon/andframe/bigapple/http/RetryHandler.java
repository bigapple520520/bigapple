package com.winupon.andframe.bigapple.http;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.os.SystemClock;

/**
 * 重连接口的实现
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-2 下午7:54:33 $
 */
public class RetryHandler implements HttpRequestRetryHandler {
    private static final int RETRY_SLEEP_INTERVAL = 1000;

    // 需重连异常
    private static HashSet<Class<?>> exceptionWhiteList = new HashSet<Class<?>>();

    // 不需重连异常
    private static HashSet<Class<?>> exceptionBlackList = new HashSet<Class<?>>();

    static {
        exceptionWhiteList.add(NoHttpResponseException.class);
        exceptionWhiteList.add(UnknownHostException.class);
        exceptionWhiteList.add(SocketException.class);

        exceptionBlackList.add(InterruptedIOException.class);
        exceptionBlackList.add(SSLHandshakeException.class);
    }

    // 最大重连数
    private final int maxRetries;

    public RetryHandler(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean retryRequest(IOException exception, int retriedTimes, HttpContext context) {
        boolean retry = true;

        Boolean b = (Boolean) context.getAttribute(ExecutionContext.HTTP_REQ_SENT);
        boolean sent = (b != null && b.booleanValue());

        if (retriedTimes > maxRetries) {
            // 超过最大重连数
            retry = false;
        }
        else if (exceptionBlackList.contains(exception.getClass())) {
            // 不重连异常
            retry = false;
        }
        else if (exceptionWhiteList.contains(exception.getClass())) {
            retry = true;
        }
        else if (!sent) {
            retry = true;
        }

        if (retry) {
            try {
                HttpRequestBase currRequest = (HttpRequestBase) context.getAttribute(ExecutionContext.HTTP_REQUEST);
                retry = (currRequest != null) && "GET".equals(currRequest.getMethod());
            }
            catch (Exception e) {
                retry = false;
                e.printStackTrace();
            }
        }

        if (retry) {
            // 休眠1秒钟后再继续尝试
            SystemClock.sleep(RETRY_SLEEP_INTERVAL);
        }
        else {
            exception.printStackTrace();
        }

        return retry;
    }

}
