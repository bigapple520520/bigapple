/* 
 * @(#)TestDemo.java    Created on 2013-8-7
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.example.bigapple_demo.http;

import java.io.File;
import java.util.HashMap;

import android.os.Environment;
import android.widget.TextView;

import com.winupon.andframe.bigapple.http.AnHttpUtils;
import com.winupon.andframe.bigapple.http.client.RequestCallBack;
import com.winupon.andframe.bigapple.http.client.RequestParams;

/**
 * Http模块测试地址测试例子
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午4:55:57 $
 */
public abstract class HttpTestDemo {

    // 文件上传测试
    // -----------------------------------------------------------------------------------------------------------
    public static void uploadFileTest(final TextView textView) {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/xuan/1.jpg");
        RequestParams requestParams = new RequestParams();

        requestParams.put("name", "xuan");// 普通参数
        try {
            requestParams.put("file", file);// 文件参数
        }
        catch (Exception e) {
            textView.setText("文件不存在");
        }

        RequestCallBack<String> requestCallBack = new RequestCallBack<String>() {
            @Override
            public void onStart() {
                textView.setText("文件开始上传");
            }

            @Override
            public void onLoading(long count, long current) {
                textView.setText("文件上传进度：" + current + "/" + count);
            }

            @Override
            public void onSuccess(String t) {
                textView.setText(t);
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                textView.setText("文件上传失败:Throwable[" + t.getMessage() + "]errorNo[" + errorNo + "]strMsg[" + strMsg
                        + "]");
            }
        };
        requestCallBack.progress(true, 10);

        AnHttpUtils httpUtils = new AnHttpUtils();
        httpUtils.post("http://192.168.0.103/test/test.htm", requestParams, requestCallBack);
    }

    // 下载方法测试
    // -----------------------------------------------------------------------------------------------------------
    public static void downloadTest(final TextView textView) {
        String target = Environment.getExternalStorageDirectory().getPath() + "/xuan/1.jpg";// 文件存放位置

        RequestCallBack<File> requestCallBack = new RequestCallBack<File>() {
            @Override
            public void onStart() {
                textView.setText("文件开始下载");
            }

            @Override
            public void onLoading(long count, long current) {
                textView.setText("文件下载进度：" + current + "/" + count);
            }

            @Override
            public void onSuccess(File t) {
                textView.setText("文件下载成功");
            }

            @Override
            public void onFailure(Throwable t, int errorNo, String strMsg) {
                textView.setText("文件下失败:Throwable[" + t.getMessage() + "]errorNo[" + errorNo + "]strMsg[" + strMsg
                        + "]");
            }
        };

        requestCallBack.progress(true, 10);// 大大的注意，只有设置了这个方法，onLoading才会被调用，单位ms

        AnHttpUtils httpUtils = new AnHttpUtils();
        httpUtils
                .download(
                        "http://h.hiphotos.baidu.com/album/w%3D2048/sign=bcc2b1908601a18bf0eb154faa170608/42166d224f4a20a471350b7b91529822720ed066.jpg",
                        target, requestCallBack);
    }

    // POST方法测试
    // ----------------------------------------------------------------------------------------
    /**
     * 异步POST测试
     * 
     * @param textView
     */
    public static void postTest(final TextView textView) {
        AnHttpUtils httpUtils = new AnHttpUtils();

        RequestParams requestParams = new RequestParams("name", "xuan");
        requestParams.put("age", "7");

        httpUtils.post("http://192.168.0.103/test/test.htm", requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(String t) {
                textView.setText(t);
            }
        });
    }

    /**
     * 同步POST测试，实际要用线程启动
     * 
     * @return
     */
    public static void postSyncTest(final TextView textView) {
        AnHttpUtils httpUtils = new AnHttpUtils();

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("name", "xuan");
        paramMap.put("age", "7");

        RequestParams requestParams = new RequestParams(paramMap);

        textView.setText((String) httpUtils.postSync("http://192.168.0.103/test/test.htm", requestParams));
    }

    // GET方法测试
    // ----------------------------------------------------------------------------------------------
    /**
     * 同步GET测试，实际要用线程启动
     * 
     * @return
     */
    public static void getSyncTest(final TextView textView) {
        AnHttpUtils httpUtils = new AnHttpUtils();
        RequestParams requestParams = new RequestParams();
        requestParams.put("name", "xuan");
        requestParams.put("age", "7");
        textView.setText((String) httpUtils.getSync("http://192.168.0.103/test/test.htm", requestParams));
    }

    /**
     * 异步GET测试
     * 
     * @param textView
     */
    public static void getTest(final TextView textView) {
        AnHttpUtils httpUtils = new AnHttpUtils();
        RequestParams requestParams = new RequestParams(new Object[] { "name", "xuan", "age", "7" });
        httpUtils.get("http://192.168.0.103/test/test.htm", requestParams, new RequestCallBack<String>() {
            @Override
            public void onSuccess(String t) {
                textView.setText(t);
            }
        });
    }

}
