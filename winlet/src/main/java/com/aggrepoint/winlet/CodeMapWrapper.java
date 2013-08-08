package com.aggrepoint.winlet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 将HashMap做封装，防止CodeTable的内容被应用修改
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 * @param <K>
 * @param <V>
 */
public class CodeMapWrapper implements Map<String, String> {
	Map<String, String> map;

	public CodeMapWrapper(Map<String, String> map) {
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
	public String get(Object key) {
		return map.get(key == null ? key : key.toString());
	}

	@Override
	public String put(String key, String value) {
		return null;
	}

	@Override
	public String remove(Object key) {
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
	}

	@Override
	public void clear() {
	}

	@Override
	public Set<String> keySet() {
		return map.keySet();
	}

	@Override
	public Collection<String> values() {
		return map.values();
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		return map.entrySet();
	}
}
