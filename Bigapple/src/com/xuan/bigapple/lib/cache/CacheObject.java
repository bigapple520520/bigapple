package com.xuan.bigapple.lib.cache;

/**
 * 用这个封装缓存对象的唯一好处就是可以设置缓存过期，取到对象后可以根据是否过期做相应的操作
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
