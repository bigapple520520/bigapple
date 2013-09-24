/* 
 * @(#)CacheObject.java    Created on 2011-11-16
 * Copyright (c) 2011 ZDSoft Networks, Inc. All rights reserved.
 * $Id: CacheObject.java 21585 2011-11-22 06:46:47Z huangwj $
 */
package com.winupon.andframe.bigapple.cache;

/**
 * 可以使用这个对象来包装缓存对象，就可以判断缓存是否过期了
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-19 下午8:09:55 $
 */
public class CacheObject<T> {
    private volatile long creationTime;// 创建时间时长
    private volatile long expiryTime = -1;// 过期时间，默认是永久
    private T value;// 缓存对象

    public CacheObject() {
        initCreationTime();
    }

    public CacheObject(T value) {
        this.value = value;
        initCreationTime();
    }

    public CacheObject(T value, long expiryTime) {
        this.value = value;
        this.expiryTime = expiryTime;
        initCreationTime();
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public long getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(long expiryTime) {
        this.expiryTime = expiryTime;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isExpired() {
        if (expiryTime < 0) {
            return false;
        }

        long time = creationTime + expiryTime;
        return time > 0 && time <= System.currentTimeMillis();
    }

    private void initCreationTime() {
        creationTime = System.currentTimeMillis();
    }

}
