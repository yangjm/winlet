package com.aggrepoint.dao;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public abstract class ListToMap<K, T> {
	public abstract K getObjectKey(T t);

	public void convert(List<T> list, Map<K, T> map) {
		map.clear();
		for (T t : list)
			map.put(getObjectKey(t), t);
	}

	public HashMap<K, T> toHashMap(List<T> list) {
		HashMap<K, T> map = new HashMap<K, T>();
		convert(list, map);
		return map;
	}

	public Hashtable<K, T> toHashtable(List<T> list) {
		Hashtable<K, T> map = new Hashtable<K, T>();
		convert(list, map);
		return map;
	}
}
