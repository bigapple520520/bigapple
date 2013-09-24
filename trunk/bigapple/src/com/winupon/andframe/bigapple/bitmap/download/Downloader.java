package com.winupon.andframe.bigapple.bitmap.download;

import java.io.OutputStream;

/**
 * 图片下载器接口
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午5:30:16 $
 */
public interface Downloader {
    /**
     * 把网络或本地图片下载到文件的 outputStream
     * 
     * @param urlString
     * @param outputStream
     * 
     * @return 返回过期时长
     */
    long downloadToStream(String urlString, OutputStream outputStream);

    /**
     * 设置图片过期时长
     * 
     * @param expiry
     */
    void setDefaultExpiry(long expiry);

    /**
     * 获取图片过期时长
     * 
     * @return
     */
    long getDefaultExpiry();

}
