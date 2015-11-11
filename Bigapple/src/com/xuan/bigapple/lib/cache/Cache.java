package com.xuan.bigapple.lib.cache;

/**
 * 缓存通用接口
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午8:38:02 $
 */
public interface Cache<K, V> {

	/**
	 * 获取缓存
	 * 
	 * @param key
	 * @return
	 */
	public V get(K key);

	/**
	 * 放入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public V put(K key, V value);

	/**
	 * 放入缓存，含过期时间
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public V put(K key, V value, long expiryTimestamp);

	/**
	 * 删除缓存key
	 * 
	 * @param key
	 * @return
	 */
	public V remove(K key);

	/**
	 * 删除所有缓存
	 */
	public void removeAll();

	/**
	 * 关闭缓存，关闭之后不能再用，需要重新初始化
	 */
	public void destroy();

	/**
	 * 最大容量
	 * 
	 * @return
	 */
	public int maxSize();

	/**
	 * 当前容量
	 * 
	 * @return
	 */
	public int size();

}
