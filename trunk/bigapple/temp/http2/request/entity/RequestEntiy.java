package com.winupon.andframe.bigapple.http2.request.entity;

import java.io.IOException;
import java.io.OutputStream;

/**
 * 请求实体
 * 
 * @author xuan
 * 
 */
public interface RequestEntiy {
	/**
	 * 输出实体到指定流中
	 * 
	 * @param outstream
	 * @throws IOException
	 */
	public void writeTo(final OutputStream outstream) throws IOException;
}
