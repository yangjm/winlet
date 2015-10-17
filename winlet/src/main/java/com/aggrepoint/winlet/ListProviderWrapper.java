package com.aggrepoint.winlet;

import java.util.List;
import java.util.Map;

public class ListProviderWrapper implements ListProvider {
	ListProvider wrapped;
	int type;

	public ListProviderWrapper(int type, ListProvider wrapped) {
		this.type = type;
		this.wrapped = wrapped;
	}

	@Override
	public Map<String, ?> getMap(String type) {
		return wrapped.getMap(type);
	}

	@Override
	public List<?> getList(String type) {
		return wrapped.getList(type);
	}

	@Override
	public Map<String, String> getCodeValueMap(String type) {
		return wrapped.getCodeValueMap(type);
	}

	@Override
	public List<CodeValue> getCodeValueList(String type) {
		return wrapped.getCodeValueList(type);
	}

	public int getType() {
		return type;
	}
}
