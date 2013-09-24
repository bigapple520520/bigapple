package com.winupon.andframe.bigapple.http.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import com.winupon.andframe.bigapple.http.client.content.FileWraper;

/**
 * 请求参数封装
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:31:56 $
 */
public class RequestParams {
    private static String ENCODING = "UTF-8";

    protected ConcurrentHashMap<String, String> paramMap;// 普通参数
    protected ConcurrentHashMap<String, FileWraper> fileParamMap;// 文件参数

    // 构造
    // ----------------------------------------------------------------------------------------------------------------
    public RequestParams() {
        init();
    }

    public RequestParams(Map<String, String> source) {
        init();
        for (Map.Entry<String, String> entry : source.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    public RequestParams(String key, String value) {
        init();
        put(key, value);
    }

    public RequestParams(Object... keysAndValues) {
        init();
        int len = keysAndValues.length;
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Supplied arguments must be even");
        }

        for (int i = 0; i < len; i += 2) {
            String key = String.valueOf(keysAndValues[i]);
            String val = String.valueOf(keysAndValues[i + 1]);
            put(key, val);
        }
    }

    private void init() {
        paramMap = new ConcurrentHashMap<String, String>();
        fileParamMap = new ConcurrentHashMap<String, FileWraper>();
    }

    // 添加参数
    // ----------------------------------------------------------------------------------------------------------------
    public void put(String key, String value) {
        if (key != null && value != null) {
            paramMap.put(key, value);
        }
    }

    public void put(String key, File file) throws FileNotFoundException {
        put(key, new FileInputStream(file), file.getName());
    }

    public void put(String key, InputStream stream) {
        put(key, stream, null);
    }

    public void put(String key, InputStream stream, String fileName) {
        put(key, stream, fileName, null);
    }

    public void put(String key, InputStream stream, String fileName, String contentType) {
        if (key != null && stream != null) {
            fileParamMap.put(key, new FileWraper(stream, fileName, contentType));
        }
    }

    public void remove(String key) {
        paramMap.remove(key);
        fileParamMap.remove(key);
    }

    /**
     * 把所有参数打包成HttpEntity返回，POST请求时用
     * 
     * @return
     */
    public HttpEntity getEntity() {
        HttpEntity entity = null;

        if (!fileParamMap.isEmpty()) {
            MultipartEntity multipartEntity = new MultipartEntity();

            // 添加字符串参数
            for (ConcurrentHashMap.Entry<String, String> entry : paramMap.entrySet()) {
                multipartEntity.addPart(entry.getKey(), entry.getValue());
            }

            // 添加文件参数
            int currentIndex = 0;
            int lastIndex = fileParamMap.entrySet().size() - 1;
            for (ConcurrentHashMap.Entry<String, FileWraper> entry : fileParamMap.entrySet()) {
                FileWraper file = entry.getValue();
                if (file.inputStream != null) {
                    boolean isLast = (currentIndex == lastIndex);
                    if (file.contentType != null) {
                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, file.contentType,
                                isLast);
                    }
                    else {
                        multipartEntity.addPart(entry.getKey(), file.getFileName(), file.inputStream, isLast);
                    }
                }
                currentIndex++;
            }

            entity = multipartEntity;
        }
        else {
            try {
                entity = new UrlEncodedFormEntity(getParamsList(), ENCODING);
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return entity;
    }

    /**
     * 获取参数列表，GTE请求时用
     * 
     * @return
     */
    protected List<BasicNameValuePair> getParamsList() {
        List<BasicNameValuePair> basicNameValuePairList = new LinkedList<BasicNameValuePair>();

        for (ConcurrentHashMap.Entry<String, String> entry : paramMap.entrySet()) {
            basicNameValuePairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        return basicNameValuePairList;
    }

    /**
     * 获取参数串
     * 
     * @return
     */
    public String getParamString() {
        return URLEncodedUtils.format(getParamsList(), ENCODING);
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (ConcurrentHashMap.Entry<String, String> entry : paramMap.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }

            result.append(entry.getKey());
            result.append("=");
            result.append(entry.getValue());
        }

        for (ConcurrentHashMap.Entry<String, FileWraper> entry : fileParamMap.entrySet()) {
            if (result.length() > 0) {
                result.append("&");
            }

            result.append(entry.getKey());
            result.append("=");
            result.append("FILE");
        }

        return result.toString();
    }

}
