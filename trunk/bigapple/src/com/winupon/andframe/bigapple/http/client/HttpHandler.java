package com.winupon.andframe.bigapple.http.client;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.winupon.andframe.bigapple.http.handler.EntityCallBack;
import com.winupon.andframe.bigapple.http.handler.FileEntityHandler;
import com.winupon.andframe.bigapple.http.handler.StringEntityHandler;

/**
 * 异步处理Http
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:53:59 $
 */
public class HttpHandler<T> extends AsyncTask<Object, Object, Object> implements EntityCallBack {
    private final static int UPDATE_START = 1;
    private final static int UPDATE_LOADING = 2;
    private final static int UPDATE_FAILURE = 3;
    private final static int UPDATE_SUCCESS = 4;

    private final AbstractHttpClient client;
    private final HttpContext context;

    private final StringEntityHandler stringEntityHandler = new StringEntityHandler();
    private final FileEntityHandler fileEntityHandler = new FileEntityHandler();

    private final RequestCallBack<T> callback;

    private int executionCount = 0;// 记录重连次数
    private String targetUrl = null; // 下载文件时保存的路径
    private boolean isResume = false; // 是否断点续传
    private final String charset;
    private long recordTime;

    public HttpHandler(AbstractHttpClient client, HttpContext context, RequestCallBack<T> callback, String charset) {
        this.client = client;
        this.context = context;
        this.callback = callback;
        this.charset = charset;
    }

    @Override
    protected Object doInBackground(Object... params) {
        if (params != null && params.length == 3) {
            // 如果是下载文件，就要求传文件保存路径和是否断点续传
            targetUrl = String.valueOf(params[1]);
            isResume = (Boolean) params[2];
        }

        try {
            publishProgress(UPDATE_START);
            makeRequestWithRetries((HttpUriRequest) params[0]);
        }
        catch (IOException e) {
            publishProgress(UPDATE_FAILURE, e, 0, e.getMessage());
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        int update = Integer.valueOf(String.valueOf(values[0]));
        switch (update) {
        case UPDATE_START:
            if (callback != null) {
                callback.onStart();
            }
            break;
        case UPDATE_LOADING:
            if (callback != null) {
                callback.onLoading(Long.valueOf(String.valueOf(values[1])), Long.valueOf(String.valueOf(values[2])));
            }
            break;
        case UPDATE_FAILURE:
            if (callback != null) {
                callback.onFailure((Throwable) values[1], (Integer) values[2], (String) values[3]);
            }
            break;
        case UPDATE_SUCCESS:
            if (callback != null) {
                callback.onSuccess((T) values[1]);
            }
            break;
        }
        super.onProgressUpdate(values);
    }

    public boolean isStop() {
        return fileEntityHandler.isStop();
    }

    public void stop() {
        fileEntityHandler.setStop(true);
    }

    // 实际请求
    private void makeRequestWithRetries(HttpUriRequest request) throws IOException {
        if (isResume && targetUrl != null) {
            File downloadFile = new File(targetUrl);
            long fileLen = 0;
            if (downloadFile.isFile() && downloadFile.exists()) {
                fileLen = downloadFile.length();
            }

            if (fileLen > 0) {
                request.setHeader("RANGE", "bytes=" + fileLen + "-");
            }
        }

        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        while (retry) {
            try {
                if (!isCancelled()) {
                    HttpResponse response = client.execute(request, context);
                    if (!isCancelled()) {
                        handleResponse(response);
                    }
                }
                return;
            }
            catch (UnknownHostException e) {
                publishProgress(UPDATE_FAILURE, e, 0, "unknownHostException：can't resolve host");
                return;
            }
            catch (IOException e) {
                cause = e;
                retry = retryHandler.retryRequest(cause, ++executionCount, context);
            }
            catch (NullPointerException e) {
                cause = new IOException("NPE in HttpClient:" + e.getMessage());
                retry = retryHandler.retryRequest(cause, ++executionCount, context);
            }
            catch (Exception e) {
                cause = new IOException("Exception:" + e.getMessage());
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

    // 处理HttpResponse
    private void handleResponse(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        if (status.getStatusCode() >= 300) {
            String errorMsg = "response status error code:" + status.getStatusCode();
            if (status.getStatusCode() == 416 && isResume) {
                errorMsg += " \n maybe you have download complete.";
            }

            publishProgress(UPDATE_FAILURE,
                    new HttpResponseException(status.getStatusCode(), status.getReasonPhrase()),
                    status.getStatusCode(), errorMsg);
        }
        else {
            try {
                HttpEntity entity = response.getEntity();
                Object responseBody = null;
                if (entity != null) {
                    recordTime = SystemClock.uptimeMillis();
                    if (targetUrl != null) {
                        responseBody = fileEntityHandler.handleEntity(entity, this, targetUrl, isResume);
                    }
                    else {
                        responseBody = stringEntityHandler.handleEntity(entity, this, charset);
                    }
                }
                publishProgress(UPDATE_SUCCESS, responseBody);
            }
            catch (IOException e) {
                publishProgress(UPDATE_FAILURE, e, 0, e.getMessage());
            }
        }
    }

    @Override
    public void callBack(long count, long current, boolean mustNoticeUI) {
        if (callback != null && callback.isProgress()) {
            if (mustNoticeUI) {
                publishProgress(UPDATE_LOADING, count, current);
            }
            else {
                long curTime = SystemClock.uptimeMillis();
                if (curTime - recordTime >= callback.getRate()) {
                    recordTime = curTime;
                    publishProgress(UPDATE_LOADING, count, current);
                }
            }
        }
    }

}
