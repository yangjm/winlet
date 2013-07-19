package com.aggrepoint.winlet;

public interface PageStorage {
	public <T> T getAttribute(Object obj);

	public void setAttribute(Object key, Object value);

	public void removeAttribute(Object key);
}
