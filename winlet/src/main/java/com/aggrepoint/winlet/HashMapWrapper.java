package com.aggrepoint.winlet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 将HashMap做封装，防止内容被应用修改
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 * @param <K>
 * @param <V>
 */
public class HashMapWrapper<K, V> implements Map<K, V> {
	Map<K, V> map;

	public HashMapWrapper(Map<K, V> map) {
		this.map = map;
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return map.get(key);
	}

	@Override
	public V put(K key, V value) {
		return null;
	}

	@Override
	public V remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
	}

	@Override
	public void clear() {
	}

	@Override
	public Set<K> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<V> values() {
		return map.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return map.entrySet();
	}
}
