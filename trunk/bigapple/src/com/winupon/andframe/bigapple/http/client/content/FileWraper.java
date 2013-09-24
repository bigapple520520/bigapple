/* 
 * @(#)FileBody.java    Created on 2013-8-7
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.http.client.content;

import java.io.InputStream;

/**
 * 存放文件流
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午8:14:21 $
 */
public class FileWraper {
    public InputStream inputStream;// 文件输入流
    public String fileName;// 文件名
    public String contentType;// 文件类型

    public FileWraper(InputStream inputStream, String fileName, String contentType) {
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public String getFileName() {
        return (null != fileName) ? fileName : "nofilename";
    }

}
