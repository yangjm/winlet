package com.aggrepoint.utils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class MapListener<K, V> implements Map<K, V> {
	private Map<K, V> inner;
	private Consumer<Map<K, V>> listener;

	public MapListener(Map<K, V> map, Consumer<Map<K, V>> listener) {
		inner = map;
		this.listener = listener;
	}

	@Override
	public int size() {
		return inner.size();
	}

	@Override
	public boolean isEmpty() {
		return inner.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return inner.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return inner.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return inner.get(key);
	}

	@Override
	public V put(K key, V value) {
		inner.put(key, value);
		listener.accept(inner);
		return value;
	}

	@Override
	public V remove(Object key) {
		V v = inner.remove(key);
		listener.accept(inner);
		return v;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		inner.putAll(m);
		listener.accept(inner);
	}

	@Override
	public void clear() {
		inner.clear();
		listener.accept(inner);
	}

	@Override
	public Set<K> keySet() {
		return inner.keySet();
	}

	@Override
	public Collection<V> values() {
		return inner.values();
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return inner.entrySet();
	}

}
