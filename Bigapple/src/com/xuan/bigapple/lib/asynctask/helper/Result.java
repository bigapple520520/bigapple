package com.xuan.bigapple.lib.asynctask.helper;

import java.io.Serializable;

/**
 * 耗时操作返回的结果对象，一般可以用作Http请求结果的封装
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2012-7-6 下午01:00:51 $
 */
public class Result<T> implements Serializable {
	private static final long serialVersionUID = 1336383322394823709L;

	/**
	 * 结果的成功失败标识
	 */
	private boolean success;
	/**
	 * 结果的提示
	 */
	private String message;
	/**
	 * 结果的数据
	 */
	private T value;

	/**
	 * 构造方法
	 */
	public Result() {
	}

	/**
	 * 构造方法
	 * 
	 * @param success
	 *            正确与否标志
	 * @param message
	 *            提示
	 */
	public Result(boolean success, String message) {
		this.success = success;
		this.message = message;
	}

	/**
	 * 构造方法
	 * 
	 * @param success
	 *            正确与否标志
	 * @param message
	 *            提示
	 * @param value
	 *            数据对象
	 */
	public Result(boolean success, String message, T value) {
		this.success = success;
		this.message = message;
		this.value = value;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

}
