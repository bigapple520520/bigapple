package com.xuan.bigapple.lib.cache.core;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU内存缓存，拷贝Android源码（bitmap模块中的缓存更好用，扩展了缓存过期时间设置）
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-8-1 下午4:51:58 $
 */
public class LruCache<K, V> {
	private final LinkedHashMap<K, V> map;

	private int size;// 实际容量
	private final int maxSize;// 最大容量，事先决定的

	private int putCount;// 存入次数
	private int createCount;// 被创建次数
	private int evictionCount;// 被剔除次数
	private int hitCount;// 命中次数
	private int missCount;// 没命中次数

	public LruCache(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize <= 0");
		}
		this.maxSize = maxSize;
		this.map = new LinkedHashMap<K, V>(0, 0.75f, true);// true表示以访问顺序排序
	}

	/**
	 * 获取缓存
	 * 
	 * @param key
	 * @return
	 */
	public final V get(K key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}

		V mapValue;
		synchronized (this) {
			mapValue = map.get(key);
			if (mapValue != null) {
				hitCount++;
				return mapValue;
			}
			missCount++;
		}

		V createdValue = create(key);
		if (createdValue == null) {
			return null;
		}

		synchronized (this) {
			createCount++;
			mapValue = map.put(key, createdValue);

			if (mapValue != null) {
				// 有冲突，放回最近put进去的那个值
				map.put(key, mapValue);
			} else {
				size += safeSizeOf(key, createdValue);
			}
		}

		if (mapValue != null) {
			entryRemoved(false, key, createdValue, mapValue);
			return mapValue;
		} else {
			trimToSize(maxSize);
			return createdValue;
		}
	}

	/**
	 * 放入缓存
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public final V put(K key, V value) {
		if (key == null || value == null) {
			throw new NullPointerException("key == null || value == null");
		}

		V previous;
		synchronized (this) {
			putCount++;
			size += safeSizeOf(key, value);
			previous = map.put(key, value);
			if (previous != null) {
				size -= safeSizeOf(key, previous);
			}
		}

		if (previous != null) {
			entryRemoved(false, key, previous, value);
		}

		trimToSize(maxSize);
		return previous;
	}

	/**
	 * 减少缓存到maxSize
	 * 
	 * @param maxSize
	 */
	private void trimToSize(int maxSize) {
		while (true) {
			K key;
			V value;
			synchronized (this) {
				if (size < 0 || (map.isEmpty() && size != 0)) {
					throw new IllegalStateException(getClass().getName()
							+ ".sizeOf() is reporting inconsistent results!");
				}

				if (size <= maxSize || map.isEmpty()) {
					break;
				}

				Map.Entry<K, V> toEvict = map.entrySet().iterator().next();
				key = toEvict.getKey();
				value = toEvict.getValue();
				map.remove(key);
				size -= safeSizeOf(key, value);
				evictionCount++;
			}

			entryRemoved(true, key, value, null);
		}
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 * @return
	 */
	public final V remove(K key) {
		if (key == null) {
			throw new NullPointerException("key == null");
		}

		V previous;
		synchronized (this) {
			previous = map.remove(key);
			if (previous != null) {
				size -= safeSizeOf(key, previous);
			}
		}

		if (previous != null) {
			entryRemoved(false, key, previous, null);
		}

		return previous;
	}

	/**
	 * 子类可自行实现，每次移除缓存时都会被调用
	 * 
	 * @param evicted
	 * @param key
	 * @param oldValue
	 * @param newValue
	 */
	protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
	}

	/**
	 * 子类可自行实现，每次没有名字，会被调用
	 * 
	 * @param key
	 * @return
	 */
	protected V create(K key) {
		return null;
	}

	/**
	 * 子类可自行实现，用来计算缓存的大小，每个算一个
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	protected int sizeOf(K key, V value) {
		return 1;
	}

	// 判断每个缓存的大小是否安全
	private int safeSizeOf(K key, V value) {
		int result = sizeOf(key, value);
		if (result < 0) {
			throw new IllegalStateException("Negative size: " + key + "="
					+ value);
		}
		return result;
	}

	/**
	 * 剔除所有缓存
	 */
	public final void evictAll() {
		trimToSize(-1);// 紧缩到-1，就是全部删除喽
	}

	public synchronized final int size() {
		return size;
	}

	public synchronized final int maxSize() {
		return maxSize;
	}

	public synchronized final int hitCount() {
		return hitCount;
	}

	public synchronized final int missCount() {
		return missCount;
	}

	public synchronized final int createCount() {
		return createCount;
	}

	public synchronized final int putCount() {
		return putCount;
	}

	public synchronized final int evictionCount() {
		return evictionCount;
	}

	public synchronized final Map<K, V> snapshot() {
		return new LinkedHashMap<K, V>(map);
	}

	@Override
	public synchronized final String toString() {
		int accesses = hitCount + missCount;
		int hitPercent = accesses != 0 ? (100 * hitCount / accesses) : 0;
		return String.format(
				"LruMemoryCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]",
				maxSize, hitCount, missCount, hitPercent);
	}

}
