package com.xuan.bigapple.lib.bitmap.core.cache;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 基于Lru算法的内存缓存。
 * 
 * @author xuan
 * @version $Revision: 1.0 $, $Date: 2013-9-17 下午7:03:41 $
 */
public class LruMemoryCache<K, V> {
	private final LinkedHashMap<K, V> map;

	private int size;
	private int maxSize;

	private int putCount;
	private int createCount;
	private int evictionCount;
	private int hitCount;
	private int missCount;

	public LruMemoryCache(int maxSize) {
		if (maxSize <= 0) {
			throw new IllegalArgumentException("maxSize <= 0");
		}
		this.maxSize = maxSize;
		this.map = new LinkedHashMap<K, V>(0, 0.75f, true);// true表示以访问顺序排序，利用这个可实现LRU算法
	}

	public void setMaxSize(int maxSize) {
		this.maxSize = maxSize;
		trimToSize(maxSize);
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

		// 未找到缓存，试图创建
		V createdValue = create(key);
		if (createdValue == null) {
			return null;
		}

		synchronized (this) {
			createCount++;
			mapValue = map.put(key, createdValue);

			if (mapValue != null) {
				// 此时应该是冲突了，有人已经put了，那就已那个put的东西为准，所以再默默的放回去
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

	public final V put(K key, V value) {
		return put(key, value, Long.MAX_VALUE);
	}

	public final V put(K key, V value, long expiryTimestamp) {
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

	private void trimToSize(int maxSize) {
		while (true) {
			K key;
			V value;
			synchronized (this) {
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

	protected void entryRemoved(boolean evicted, K key, V oldValue, V newValue) {
	}

	protected V create(K key) {
		return null;
	}

	private int safeSizeOf(K key, V value) {
		int result = sizeOf(key, value);
		if (result <= 0) {
			size = 0;
			for (Map.Entry<K, V> entry : map.entrySet()) {
				size += sizeOf(entry.getKey(), entry.getValue());
			}
		}
		return result;
	}

	protected int sizeOf(K key, V value) {
		return 1;
	}

	public final void evictAll() {
		trimToSize(-1); // -1 will evict 0-sized elements
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
		return String.format(Locale.getDefault(),
				"LruMemoryCache[maxSize=%d,hits=%d,misses=%d,hitRate=%d%%]",
				maxSize, hitCount, missCount, hitPercent);
	}

}
