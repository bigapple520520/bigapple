/* 
 * @(#)FileBody.java    Created on 2013-8-7
 * Copyright (c) 2013 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.http2.urlhttpclient.core;

import java.io.InputStream;

/**
 * 文件打包类
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-7 下午8:14:21 $
 */
public class FileWraper {
    public InputStream inputStream;
    public String fileName;
    public String contentType;

    public FileWraper(InputStream inputStream, String fileName, String contentType) {
        this.inputStream = inputStream;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public String getFileName() {
        return (null != fileName) ? fileName : "nofilename";
    }

}
