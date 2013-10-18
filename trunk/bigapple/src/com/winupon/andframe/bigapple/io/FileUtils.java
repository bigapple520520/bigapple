/* 
 * @(#)FileUtils.java    Created on 2013-9-4
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件操作工具类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-4 下午7:24:34 $
 */
public abstract class FileUtils {
    public static final String EMPTY = "";

    public static final long ONE_KB = 1024;
    public static final long ONE_MB = ONE_KB * ONE_KB;
    public static final long ONE_GB = ONE_KB * ONE_MB;
    public static final long ONE_TB = ONE_KB * ONE_GB;
    public static final long ONE_PB = ONE_KB * ONE_TB;
    public static final long ONE_EB = ONE_KB * ONE_PB;

    /**
     * 取得文件的后缀名。
     * 
     * @param fileName
     *            文件名
     * @return 后缀名
     */
    public static String getExtension(String fileName) {
        if (null == fileName) {
            return EMPTY;
        }

        int pointIndex = fileName.lastIndexOf(".");
        return pointIndex > 0 && pointIndex < fileName.length() ? fileName.substring(pointIndex + 1).toLowerCase()
                : EMPTY;
    }

    // /////////////////////////////////////////字节写入读出文件方法，一般可以用来写图片，声音等//////////////////////////
    /**
     * 字节写入到文件中
     * 
     * @param file
     * @param data
     * @param append
     * @throws IOException
     */
    public static void writeByteArrayToFile(File file, byte[] data, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            out.write(data);
        }
        finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 读出文件中的字节
     * 
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] readFileToByteArray(File file) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.toByteArray(in, file.length());
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

    // 字符串从文件中写入读出方法
    // ----------------------------------------------------------------------------------------------------------------
    /**
     * 数据写入文件
     * 
     * @param file
     *            要写入的文件
     * @param data
     *            数据
     * @param encoding
     *            指定编码
     * @param append
     *            是否追加
     * @throws IOException
     */
    public static void writeStringToFile(File file, String data, String encoding, boolean append) throws IOException {
        OutputStream out = null;
        try {
            out = openOutputStream(file, append);
            IOUtils.write(data, out, encoding);
        }
        finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 从文件中以指定编码读取成字符串
     * 
     * @param file
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readFileToString(File file, String encoding) throws IOException {
        InputStream in = null;
        try {
            in = openInputStream(file);
            return IOUtils.toString(in, encoding);
        }
        finally {
            IOUtils.closeQuietly(in);
        }
    }

    // 打开输入输出流方法
    // -----------------------------------------------------------------------------------------------------------------
    /**
     * 打开一个文件的写入流，文件不存在会自动创建
     * 
     * @param file
     *            文件
     * @param append
     *            是否以追加的方式写入
     * @return
     * @throws IOException
     */
    public static FileOutputStream openOutputStream(File file, boolean append) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }
            if (file.canWrite() == false) {
                throw new IOException("File '" + file + "' cannot be written to");
            }
        }
        else {
            File parent = file.getParentFile();
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory()) {
                    throw new IOException("Directory '" + parent + "' could not be created");
                }
            }
        }
        return new FileOutputStream(file, append);
    }

    /**
     * 打开文件输入流，校验友好的提示
     * 
     * @param file
     *            要打开的文件
     * @return
     * @throws IOException
     */
    public static FileInputStream openInputStream(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) {
                throw new IOException("File '" + file + "' exists but is a directory");
            }

            if (file.canRead() == false) {
                throw new IOException("File '" + file + "' cannot be read");
            }
        }
        else {
            throw new FileNotFoundException("File '" + file + "' does not exist");
        }

        return new FileInputStream(file);
    }

    // //////////////////////////////////文件单位字节友好输出////////////////////////////////////////////////////////////
    /**
     * 友好的显示单位，舍弃有点问题，例如：byteCountToDisplaySize(2047)显示1K，明显是舍弃了
     * 
     * @param size
     *            byte单位值
     * @return
     */
    public static String byteCountToDisplaySize(long size) {
        String displaySize;
        if (size / ONE_EB > 0) {
            displaySize = String.valueOf(size / ONE_EB) + " EB";
        }
        else if (size / ONE_EB > 0) {
            displaySize = String.valueOf(size / ONE_EB) + " PB";
        }
        else if (size / ONE_TB > 0) {
            displaySize = String.valueOf(size / ONE_TB) + " TB";
        }
        else if (size / ONE_GB > 0) {
            displaySize = String.valueOf(size / ONE_GB) + " GB";
        }
        else if (size / ONE_MB > 0) {
            displaySize = String.valueOf(size / ONE_MB) + " MB";
        }
        else if (size / ONE_KB > 0) {
            displaySize = String.valueOf(size / ONE_KB) + " KB";
        }
        else {
            displaySize = String.valueOf(size) + " bytes";
        }

        return displaySize;
    }

    /**
     * 删除指定目录下文件及目录
     * 
     * @param deleteThisPath
     *            是否需要删除这个本身指定的文件或者文件夹
     * @param filepath
     *            文件或者文件夹路径
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) throws IOException {
        if (null != filePath) {
            File file = new File(filePath);

            if (file.isDirectory()) {// 处理目录
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFolderFile(files[i].getAbsolutePath(), true);
                }
            }

            if (deleteThisPath) {
                if (!file.isDirectory()) {
                    // 如果是文件，删除
                    file.delete();
                }
                else {
                    // 目录
                    if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                        file.delete();
                    }
                }
            }
        }
    }

    /**
     * 递归取得某个目录下所有的文件
     * 
     * @param path
     *            目录
     * @return 文件List
     */
    public static List<File> getNestedFiles(String path) {
        File directory = new File(path);
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("Nonexistent directory[" + path + "]");
        }

        return new Recursiver().getFileList(directory);
    }

    /**
     * 递归获取指定目录下的所有文件
     * 
     * @author xuan
     * @version $Revision: 1.0 $, $Date: 2013-9-5 下午1:13:17 $
     */
    private static class Recursiver {
        private static ArrayList<File> files = new ArrayList<File>();

        public List<File> getFileList(File file) {
            File children[] = file.listFiles();

            for (int i = 0; i < children.length; i++) {
                if (children[i].isDirectory()) {
                    new Recursiver().getFileList(children[i]);
                }
                else {
                    files.add(children[i]);
                }
            }

            return files;
        }
    }

}
