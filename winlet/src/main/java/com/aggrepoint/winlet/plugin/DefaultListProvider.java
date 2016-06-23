package com.aggrepoint.winlet.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aggrepoint.winlet.CodeValue;
import com.aggrepoint.winlet.ListProvider;

public class DefaultListProvider implements ListProvider {
	private HashMap<String, ListUnit<?>> lists;

	protected void initLists(HashMap<String, ListUnit<?>> lists) {
	}

	protected ListUnit<?> getListUnit(String type) {
		if (lists == null) {
			HashMap<String, ListUnit<?>> ls = new HashMap<String, ListUnit<?>>();
			initLists(ls);
			lists = ls;
		}

		return lists.get(type);
	}

	@Override
	public Map<String, ?> getMap(String type) {
		ListUnit<?> unit = getListUnit(type);
		if (unit == null)
			return null;
		return unit.getMap();
	}

	@Override
	public Map<String, String> getCodeValueMap(String type) {
		ListUnit<?> unit = getListUnit(type);
		if (unit == null)
			return null;
		return unit.getCodeValueMap();
	}

	@Override
	public List<?> getList(String type) {
		ListUnit<?> unit = getListUnit(type);
		if (unit == null)
			return null;
		return unit.getList();
	}

	@Override
	public List<CodeValue> getCodeValueList(String type) {
		ListUnit<?> unit = getListUnit(type);
		if (unit == null)
			return null;
		return unit.getCodeValueList();
	}
}
