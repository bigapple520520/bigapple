package com.winupon.andframe.bigapple.bitmap.download;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import com.winupon.andframe.bigapple.io.IOUtils;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 一个简单的下载器实现
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午5:30:37 $
 */
public class SimpleDownloader implements Downloader {
    public static final int BUFFER_SIZE = 4096;// 4K
    public static final int CONNECT_TIMEOUT = 1000 * 15;// 15S
    public static final int READ_TIMEOUT = 1000 * 30;// 30S

    /**
     * 把网络或本地图片下载到文件的 outputStream
     * 
     * @param uri
     * @param outputStream
     * @return 图片过期时间点； 小于零，下载失败
     */
    public long downloadToStream(String uri, OutputStream outputStream) {
        URLConnection urlConnection = null;
        BufferedInputStream bis = null;

        long result = -1;
        try {
            if (uri.startsWith("/")) {
                FileInputStream fileInputStream = new FileInputStream(uri);
                bis = new BufferedInputStream(fileInputStream);
                result = System.currentTimeMillis() + getDefaultExpiry();
            }
            else {
                final URL url = new URL(uri);
                urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
                urlConnection.setReadTimeout(READ_TIMEOUT);
                bis = new BufferedInputStream(urlConnection.getInputStream());
                result = urlConnection.getExpiration(); // 如果header中不包含expires返回0
                result = result < System.currentTimeMillis() ? System.currentTimeMillis() + getDefaultExpiry() : result;
            }

            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = bis.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();
        }
        catch (Exception e) {
            result = -1;
            LogUtils.e("", e);
        }
        finally {
            IOUtils.closeQuietly(bis);
        }

        return result;
    }

    private long defaultExpiry;

    /**
     * 设置图片过期时长
     * 
     * @param expiry
     *            表示过期的那个时间点
     */
    @Override
    public void setDefaultExpiry(long expiry) {
        this.defaultExpiry = expiry;
    }

    /**
     * 获取图片过期时长
     * 
     * @return
     */
    @Override
    public long getDefaultExpiry() {
        return this.defaultExpiry;
    }

}
