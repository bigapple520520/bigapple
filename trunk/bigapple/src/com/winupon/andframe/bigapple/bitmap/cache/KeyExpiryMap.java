package com.winupon.andframe.bigapple.bitmap.cache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 用来存放超时时长的Map集合
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午7:14:46 $
 */
public class KeyExpiryMap<K, V> extends ConcurrentHashMap<K, Long> {
    private static final long serialVersionUID = 4935855202112325906L;

    public KeyExpiryMap(int initialCapacity, float loadFactor, int concurrencyLevel) {
        super(initialCapacity, loadFactor, concurrencyLevel);
    }

    public KeyExpiryMap(int initialCapacity, float loadFactor) {
        // added API 9
        super(initialCapacity, loadFactor);
    }

    public KeyExpiryMap(int initialCapacity) {
        super(initialCapacity);
    }

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
        if (super.containsKey(key)) {
            if (System.currentTimeMillis() < super.get(key)) {
                result = true;
            }
            else {
                this.remove(key);
            }
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
