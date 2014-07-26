/* 
 * @(#)Result.java    Created on 2012-7-6
 * Copyright (c) 2012 ZDSoft Networks, Inc. All rights reserved.
 * $Id$
 */
package com.winupon.andframe.bigapple.asynctask.helper;

import java.io.Serializable;

/**
 * 返回的结果对象，一般可以用作Http请求结果的封装
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2012-7-6 下午01:00:51 $
 */
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1336383322394823709L;

    private boolean success;
    private String message;
    private T value;

    public Result() {
    }

    public Result(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

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
