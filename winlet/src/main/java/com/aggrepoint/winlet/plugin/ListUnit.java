package com.aggrepoint.winlet.plugin;

import java.util.List;
import java.util.Map;

import com.aggrepoint.winlet.CodeValue;

public interface ListUnit<T> {
	public List<T> getList();

	public List<CodeValue> getCodeValueList();

	public Map<String, T> getMap();

	public Map<String, String> getCodeValueMap();
}
