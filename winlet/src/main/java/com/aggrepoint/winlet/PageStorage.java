package com.aggrepoint.winlet;

import java.util.function.Supplier;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface PageStorage {
	public PageStorage reload();

	public <T> T getAttribute(Object obj);

	public <T> T getAttribute(Object obj, Supplier<T> supplier);

	public void setAttribute(Object key, Object value);

	public void setAttribute(Object key, Object value, boolean clearOnRefresh);

	public void removeAttribute(Object key);

	public void refresh();
}
