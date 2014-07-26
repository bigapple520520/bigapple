package com.winupon.andframe.bigapple.http2.urlhttpclient.entity;

import java.io.IOException;
import java.io.OutputStream;

/**
 * post请求时，需要把内容放入post体内，这个接口就是用来规范不同参数类型的处理
 * 
 * @author xuan
 */
public interface Entity {
	/**
	 * 把内容存入输出流中
	 * 
	 * @param outstream
	 * @throws IOException
	 */
	public void writeTo(final OutputStream outstream) throws IOException;
}
