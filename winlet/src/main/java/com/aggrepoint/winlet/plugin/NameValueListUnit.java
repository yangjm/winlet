package com.aggrepoint.winlet.plugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aggrepoint.utils.CollectionUtils;
import com.aggrepoint.winlet.CodeValue;

public class NameValueListUnit<T extends NameValue> implements ListUnit<T> {
	List<T> list;
	List<CodeValue> cvList;
	Map<String, T> map;
	Map<String, String> cvMap;

	public NameValueListUnit(T[] items) {
		list = Arrays.asList(items);
		cvList = list.stream().map(p -> new CodeValue(p.name(), p.value()))
				.collect(Collectors.toList());
		map = CollectionUtils.toHashMap(list, T::name);
		cvMap = CollectionUtils.toHashMap(list, T::name, T::value);

	}

	@Override
	public List<T> getList() {
		return list;
	}

	@Override
	public List<CodeValue> getCodeValueList() {
		return cvList;
	}

	@Override
	public Map<String, T> getMap() {
		return map;
	}

	@Override
	public Map<String, String> getCodeValueMap() {
		return cvMap;
	}
}
