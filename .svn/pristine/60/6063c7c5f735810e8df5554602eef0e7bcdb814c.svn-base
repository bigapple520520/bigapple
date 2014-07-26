/* 
 * @(#)UrlHttpClientDemoActivity.java    Created on 2014-1-2
 * Copyright (c) 2014 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.zzdemo.http2;

import com.winupon.andframe.bigapple.ioc.app.AnActivity;

/**
 * UrlHttpClient测试demo
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2014-1-2 下午6:52:41 $
 */
public class UrlHttpClientDemoActivity extends AnActivity {
    // @InjectView(R.id.buttonGet)
    // private Button buttonGet;
    //
    // @InjectView(R.id.buttonPost)
    // private Button buttonPost;
    //
    // @InjectView(R.id.buttonDownload)
    // private Button buttonDownload;
    //
    // @InjectView(R.id.buttonUpload)
    // private Button buttonUpload;
    //
    // @InjectView(R.id.buttonGetHttps)
    // private Button buttonGetHttps;
    //
    // @InjectView(R.id.textView)
    // private TextView textView;
    //
    // @Override
    // protected void onCreate(Bundle savedInstanceState) {
    // super.onCreate(savedInstanceState);
    // setContentView(R.layout.demo_http2_main);
    //
    // PreferenceModel.instance(getApplication()).saveSystemProperties("xuan",
    // getPackageName() + "eeeee",
    // Types.STRING);
    //
    // initGet();
    // initPost();
    // initDownload();
    // initUpload();
    //
    // initGetHttps();
    // }
    //
    // private void initGetHttps() {
    // buttonGetHttps.setText("getHppts测试");
    // buttonGetHttps.setOnClickListener(new Button.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // new CompatibleAsyncTask<Object, Object, String>() {
    // @Override
    // protected void onPreExecute() {
    // textView.setText("正在努力getHppts中");
    // }
    //
    // @Override
    // protected String doInBackground(Object... params) {
    // URLHttpClient client = new URLHttpClient();
    // String ret = null;
    // try {
    // ret = client.get("https://www.alipay.com");
    // }
    // catch (Exception e) {
    // LogUtils.e("", e);
    // ret = "请求出错啦！";
    // }
    //
    // return ret;
    // }
    //
    // @Override
    // protected void onPostExecute(String result) {
    // textView.setText(result);
    // }
    // }.execute();
    // }
    // });
    // }
    //
    // // upload方法测试
    // private void initUpload() {
    // buttonUpload.setText("upload方法测试");
    // buttonUpload.setOnClickListener(new Button.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // new CompatibleAsyncTask<Object, Long, String>() {
    // @Override
    // protected void onPreExecute() {
    // textView.setText("正在努力upload中");
    // }
    //
    // @Override
    // protected String doInBackground(Object... params) {
    // URLHttpClient client = new URLHttpClient();
    // String ret = null;
    // try {
    // // 要上传的文件地址
    // String filePath11 = Environment.getExternalStorageDirectory().getPath()
    // + "/bigappletest/11.jpg";
    //
    // String filePath22 = Environment.getExternalStorageDirectory().getPath()
    // + "/bigappletest/22.jpg";
    //
    // String filePath33 = Environment.getExternalStorageDirectory().getPath()
    // + "/bigappletest/33.jpg";
    //
    // RequestParams requestParams = new RequestParams();
    // requestParams.put("file11", new File(filePath11));
    // requestParams.put("file22", new File(filePath22));
    // requestParams.put("file33", new File(filePath33));
    // requestParams.put("name", "我是普通参数");
    // ret = client.upload("http://blog.xuanner.com/test/testUpload.php",
    // requestParams);
    // }
    // catch (Exception e) {
    // LogUtils.e("", e);
    // ret = "请求出错啦！";
    // }
    //
    // return ret;
    // }
    //
    // @Override
    // protected void onPostExecute(String result) {
    // textView.setText(result);
    // }
    // }.execute();
    // }
    // });
    // }
    //
    // // download方法测试
    // private void initDownload() {
    // buttonDownload.setText("download方法测试");
    // buttonDownload.setOnClickListener(new Button.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // new CompatibleAsyncTask<Object, Long, String>() {
    // @Override
    // protected void onPreExecute() {
    // textView.setText("正在努力download中");
    // }
    //
    // @Override
    // protected String doInBackground(Object... params) {
    // URLHttpClient client = new URLHttpClient();
    // String filePath = null;
    // try {
    // filePath = Environment.getExternalStorageDirectory().getPath() +
    // "/bigappletest/33.jpg";
    // client.download("http://img2.pcpop.com/ArticleImages/0x0/0/118/000118652.jpg",
    // filePath,
    // new ResultCallBack() {
    // @Override
    // public void callBack(long count, long current, boolean mustNoticeUI) {
    // publishProgress(count, current);
    // }
    // }, false);
    // }
    // catch (Exception e) {
    // LogUtils.e("", e);
    // filePath = "请求出错啦！";
    // }
    //
    // return "下载好的文件放在：" + filePath;
    // }
    //
    // @Override
    // protected void onProgressUpdate(Long... values) {
    // textView.setText("下载进度如下" + values[1] + "/" + values[0]);
    // };
    //
    // @Override
    // protected void onPostExecute(String result) {
    // textView.setText(result);
    // }
    // }.execute();
    // }
    // });
    // }
    //
    // // post方法测试
    // private void initPost() {
    // buttonPost.setText("post方法测试");
    // buttonPost.setOnClickListener(new Button.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // new CompatibleAsyncTask<Object, Object, String>() {
    // @Override
    // protected void onPreExecute() {
    // textView.setText("正在努力post中");
    // }
    //
    // @Override
    // protected String doInBackground(Object... params) {
    // URLHttpClient client = new URLHttpClient();
    // String ret = null;
    // try {
    // RequestParams requestParams = new RequestParams();
    // requestParams.put("name", "我就是name");
    // requestParams.put("value", "我就是value");
    // ret = client.post("http://blog.xuanner.com/test/testPost.php",
    // requestParams);
    // }
    // catch (Exception e) {
    // LogUtils.e("", e);
    // ret = "请求出错啦！";
    // }
    //
    // return ret;
    // }
    //
    // @Override
    // protected void onPostExecute(String result) {
    // textView.setText(result);
    // }
    // }.execute();
    // }
    // });
    // }
    //
    // // Get方法测试
    // private void initGet() {
    // buttonGet.setText("get方法测试");
    // buttonGet.setOnClickListener(new Button.OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // new CompatibleAsyncTask<Object, Object, String>() {
    // @Override
    // protected void onPreExecute() {
    // textView.setText("正在努力get中");
    // }
    //
    // @Override
    // protected String doInBackground(Object... params) {
    // URLHttpClient client = new URLHttpClient();
    // String ret = null;
    // try {
    // RequestParams requestParams = new RequestParams();
    // requestParams.put("name", "我就是get上去的参数");
    // ret = client.get("http://blog.xuanner.com/test/testGet.php",
    // requestParams);
    // }
    // catch (Exception e) {
    // LogUtils.e("", e);
    // ret = e.getMessage();
    // }
    //
    // return ret;
    // }
    //
    // @Override
    // protected void onPostExecute(String result) {
    // textView.setText(result);
    // }
    // }.execute();
    // }
    // });
    // }

}
