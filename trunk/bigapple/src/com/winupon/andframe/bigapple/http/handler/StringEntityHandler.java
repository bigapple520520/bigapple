package com.winupon.andframe.bigapple.http.handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;

/**
 * 字符串处理
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:08:42 $
 */
public class StringEntityHandler {
    private static int BUFFER_SIZE = 1024;

    /**
     * 处理
     * 
     * @param entity
     *            处理实体
     * @param callback
     *            处理时的回调
     * @param charset
     *            字符串的编码方式
     * 
     * @return
     * @throws IOException
     */
    public Object handleEntity(HttpEntity entity, EntityCallBack callback, String charset) throws IOException {
        if (entity == null) {
            return null;
        }

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];

        long count = entity.getContentLength();
        long curCount = 0;
        int len = -1;
        InputStream is = entity.getContent();
        while ((len = is.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
            curCount += len;
            if (callback != null) {
                callback.callBack(count, curCount, false);
            }
        }

        if (callback != null) {
            callback.callBack(count, curCount, true);
        }

        byte[] data = outStream.toByteArray();
        outStream.close();
        is.close();
        return new String(data, charset);
    }

}
