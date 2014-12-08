package com.winupon.andframe.bigapple.bitmap.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个定制的线程安全的Map，继承了ConcurrentHashMap。<br>
 * 用来存放超时时长。
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午7:14:46 $
 */
public class KeyExpiryMap<K, V> extends ConcurrentHashMap<K, Long> {
    private static final long serialVersionUID = 4935855202112325906L;

    /**
     * 构造一个Map
     * 
     * @param initialCapacity
     *            Map的初始化容量
     * @param loadFactor
     *            扩容因子
     * @param concurrencyLevel
     *            估计用户并发级别
     */
    public KeyExpiryMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    /**
     * 构造一个Map
     * 
     * @param initialCapacity
     *            Map的初始化容量
     * @param loadFactor
     *            扩容因子
     */
    public KeyExpiryMap(int initialCapacity, float loadFactor) {
        // added API 9
        super(initialCapacity, loadFactor);
    }

    /**
     * 构造一个Map
     * 
     * @param initialCapacity
     *            Map的初始化容量
     */
    public KeyExpiryMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 构造一个Map
     */
    public KeyExpiryMap() {
        super();
    }

    @Override
    public synchronized Long get(Object key) {
        if (this.containsKey(key)) {
            return super.get(key);
        }
        else {
            return null;
        }
    }

    @Override
    public synchronized Long put(K key, Long expiryTimestamp) {
        if (this.containsKey(key)) {
            this.remove(key);
        }
        return super.put(key, expiryTimestamp);
    }

    @Override
    public synchronized boolean containsKey(Object key) {
        boolean result = false;
        /**
         * 这里注意一下：Android5.0中的ConcurrentHashMap中的containsKey被重写了，<br>
         * 里面会调用get方法 所以这里不能调用super.containsKey方法，不然会产生调用死循环
         */
        Long expiryTimestamp = super.get(key);
        if (null != expiryTimestamp && System.currentTimeMillis() < expiryTimestamp) {
            result = true;
        }
        else {
            this.remove(key);
        }
        return result;
    }

    @Override
    public synchronized Long remove(Object key) {
        return super.remove(key);
    }

    @Override
    public synchronized void clear() {
        super.clear();
    }

}
