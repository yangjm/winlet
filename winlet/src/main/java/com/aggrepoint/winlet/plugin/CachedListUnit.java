package com.aggrepoint.winlet.plugin;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.aggrepoint.utils.CollectionUtils;
import com.aggrepoint.winlet.CodeValue;
import com.aggrepoint.winlet.HashMapWrapper;
import com.aggrepoint.winlet.ListWrapper;

public class CachedListUnit<S, T> implements ListUnit<T> {
	S svc;
	List<T> cached;
	Function<S, List<T>> loadFunc;
	Function<T, Object> keyFunc;
	Function<T, Object> valueFunc;

	HashMapWrapper<String, T> map;
	HashMapWrapper<String, String> cvMap;
	ListWrapper<T> list;
	ListWrapper<CodeValue> cvList;

	public CachedListUnit(S svc, Function<S, List<T>> loadFunc,
			Function<T, Object> keyFunc, Function<T, Object> valueFunc) {
		this.svc = svc;
		this.loadFunc = loadFunc;
		this.keyFunc = keyFunc;
		this.valueFunc = valueFunc;
	}

	private void update() {
		List<T> l = loadFunc.apply(svc);

		if (cached != l) {
			map = new HashMapWrapper<String, T>(CollectionUtils.toHashMap(l,
					p -> {
						Object obj = keyFunc.apply(p);
						return obj == null ? "" : obj.toString();
					}, p -> p));
			cvMap = new HashMapWrapper<String, String>(CodeValue.map(l,
					keyFunc, valueFunc));
			list = new ListWrapper<T>(l);
			cvList = new ListWrapper<CodeValue>(CodeValue.list(l, keyFunc,
					valueFunc));
			cached = l;
		}
	}

	@Override
	public List<T> getList() {
		update();
		return list;
	}

	@Override
	public List<CodeValue> getCodeValueList() {
		update();
		return cvList;
	}

	@Override
	public Map<String, T> getMap() {
		update();
		return map;
	}

	@Override
	public Map<String, String> getCodeValueMap() {
		update();
		return cvMap;
	}
}
