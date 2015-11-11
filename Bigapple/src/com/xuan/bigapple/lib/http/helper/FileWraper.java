package com.xuan.bigapple.lib.http.helper;

import java.io.InputStream;

/**
 * 文件类封装
 * 
 * @author xuan
 */
public class FileWraper {
	/** 文件流 */
	public InputStream inputStream;
	/** 文件名称 */
	public String fileName;
	/** 内容类型 */
	public String contentType;

	/**
	 * 构造方法
	 * 
	 * @param inputStream
	 * @param fileName
	 * @param contentType
	 */
	public FileWraper(InputStream inputStream, String fileName,
			String contentType) {
		this.inputStream = inputStream;
		this.fileName = fileName;
		this.contentType = contentType;
	}

	/**
	 * 获取文件名称
	 * 
	 * @return
	 */
	public String getFileName() {
		return (null != fileName) ? fileName : "nofilename";
	}

}
