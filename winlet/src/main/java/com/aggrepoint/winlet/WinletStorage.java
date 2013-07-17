package com.aggrepoint.winlet;

public interface WinletStorage {
	public <T> T getAttribute(Object obj);

	public void setAttribute(Object key, Object value);

	public void removeAttribute(Object key);
}
