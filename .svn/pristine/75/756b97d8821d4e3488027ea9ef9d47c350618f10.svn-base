package com.winupon.andframe.bigapple.http2.urlhttpclient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Random;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import com.winupon.andframe.bigapple.io.IOUtils;
import com.winupon.andframe.bigapple.utils.log.LogUtils;

/**
 * 自定义一个上传文件的HttpEntity
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午2:49:53 $
 */
public class MultipartEntity {
    private final static char[] MULTIPART_CHARS = "-_1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
            .toCharArray();

    private String boundary = null;// 边界
    ByteArrayOutputStream out = new ByteArrayOutputStream();

    boolean isSetLast = false;
    boolean isSetFirst = false;

    public MultipartEntity() {
        final StringBuffer buf = new StringBuffer();
        final Random rand = new Random();
        for (int i = 0; i < 30; i++) {
            buf.append(MULTIPART_CHARS[rand.nextInt(MULTIPART_CHARS.length)]);
        }
        this.boundary = buf.toString();
    }

    /**
     * 设置第一个分界
     */
    public void writeFirstBoundaryIfNeeds() {
        if (isSetFirst) {
            return;
        }

        try {
            out.write(("--" + boundary + "\r\n").getBytes());
        }
        catch (IOException e) {
            LogUtils.e("call MultipartEntity.writeFirstBoundaryIfNeeds error", e);
        }

        isSetFirst = true;
    }

    /**
     * 设置最后一个分界
     */
    public void writeLastBoundaryIfNeeds() {
        if (isSetLast) {
            return;
        }

        try {
            out.write(("\r\n--" + boundary + "--\r\n").getBytes());
        }
        catch (final IOException e) {
            LogUtils.e("call MultipartEntity.writeLastBoundaryIfNeeds error", e);
        }

        isSetLast = true;
    }

    /**
     * 添加普通参数
     * 
     * @param key
     * @param value
     */
    public void addPart(final String key, final String value) {
        writeFirstBoundaryIfNeeds();
        try {
            out.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
            out.write(value.getBytes());
            out.write(("\r\n--" + boundary + "\r\n").getBytes());
        }
        catch (final IOException e) {
            LogUtils.e("call MultipartEntity.addPart(key,value) error", e);
        }
    }

    /**
     * 添加文件
     * 
     * @param key
     * @param fileName
     * @param fin
     * @param isLast
     */
    public void addPart(final String key, final String fileName, final InputStream fin, final boolean isLast) {
        addPart(key, fileName, fin, "application/octet-stream", isLast);
    }

    /**
     * 添加文件
     * 
     * @param key
     * @param fileName
     * @param fin
     * @param type
     * @param isLast
     */
    public void addPart(final String key, final String fileName, final InputStream fin, String type,
            final boolean isLast) {
        writeFirstBoundaryIfNeeds();
        try {
            type = "Content-Type: " + type + "\r\n";
            out.write(("Content-Disposition: form-data; name=\"" + key + "\"; filename=\"" + fileName + "\"\r\n")
                    .getBytes());
            out.write(type.getBytes());
            out.write("Content-Transfer-Encoding: binary\r\n\r\n".getBytes());

            final byte[] tmp = new byte[4 * 1024];
            int l = 0;
            while ((l = fin.read(tmp)) != -1) {
                out.write(tmp, 0, l);
            }
            if (!isLast) {
                out.write(("\r\n--" + boundary + "\r\n").getBytes());
            }
            out.flush();
        }
        catch (final IOException e) {
            LogUtils.e("call MultipartEntity.addPart(key,fileName,inputStream,type,isLast) error", e);
        }
        finally {
            IOUtils.closeQuietly(fin);
        }
    }

    /**
     * 添加文件
     * 
     * @param key
     * @param value
     * @param isLast
     */
    public void addPart(final String key, final File value, final boolean isLast) {
        try {
            addPart(key, value.getName(), new FileInputStream(value), isLast);
        }
        catch (final FileNotFoundException e) {
            LogUtils.e("call MultipartEntity.addPart(key,file,isLast) error", e);
        }
    }

    /**
     * 获取总上传长度
     * 
     * @return
     */
    public long getContentLength() {
        writeLastBoundaryIfNeeds();
        return out.toByteArray().length;
    }

    public Header getContentType() {
        return new BasicHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    public void writeTo(final OutputStream outstream) throws IOException {
        outstream.write(out.toByteArray());
    }

    public InputStream getContent() throws IOException {
        return new ByteArrayInputStream(out.toByteArray());
    }

}
