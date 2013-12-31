package com.winupon.andframe.bigapple.io;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * IO工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-4 下午7:22:40 $
 */
public abstract class IOUtils {
    private static final int EOF = -1;
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    /**
     * 默默的关闭可关闭流
     * 
     * @param closeable
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        }
        catch (IOException ioe) {
            // ignore
        }
    }

    /**
     * 把数据以指定编码写入输出流中
     * 
     * @param data
     * @param output
     * @param encoding
     * @throws IOException
     */
    public static void write(String data, OutputStream output, String encoding) throws IOException {
        if (data != null) {
            output.write(data.getBytes(Charsets.toCharset(encoding)));
        }
    }

    /**
     * 把字节流按指定编码组成字符串
     * 
     * @param input
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String toString(InputStream input, String encoding) throws IOException {
        InputStreamReader in = new InputStreamReader(input, Charsets.toCharset(encoding));// 把字节流转成字符流

        int n = 0;
        StringBuilder builder = new StringBuilder();
        char[] buffer = new char[DEFAULT_BUFFER_SIZE];
        while (EOF != (n = in.read(buffer))) {
            if (null != buffer) {
                builder.append(buffer, 0, n);
            }
        }

        return builder.toString();
    }

    /**
     * 从流中准确的读出指定字节
     * 
     * @param input
     * @param longSize
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input, long longSize) throws IOException {
        if (longSize > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Size cannot be greater than Integer max value: " + longSize);
        }

        int size = (int) longSize;// 转成int

        if (size < 0) {
            throw new IllegalArgumentException("Size must be equal or greater than zero: " + size);
        }

        if (size == 0) {
            return new byte[0];
        }

        byte[] data = new byte[size];
        int offset = 0;
        int readed;

        while (offset < size && (readed = input.read(data, offset, size - offset)) != EOF) {
            offset += readed;
        }

        if (offset != size) {
            throw new IOException("Unexpected readed size. current: " + offset + ", excepted: " + size);
        }

        return data;
    }

}
