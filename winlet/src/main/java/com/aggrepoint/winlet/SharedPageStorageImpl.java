package com.aggrepoint.winlet;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class SharedPageStorageImpl implements SharedPageStorage {
	HashMap<Object, Object> pageSession = null;
	HashMap<Object, Object> pageRefreshSession = null;

	public static final String PAGE_SESSION_KEY = "com.aggrepoint.page";
	public static final String REFRESH_PREFIX = "REFRESH_";

	protected SharedPageStorageImpl(ReqInfo reqInfo) {
		HttpSession session = reqInfo.getSession();
		synchronized (session) {
			@SuppressWarnings("unchecked")
			HashMap<String, HashMap<Object, Object>> htByPage = (HashMap<String, HashMap<Object, Object>>) session
					.getAttribute(PAGE_SESSION_KEY);
			if (htByPage == null) {
				htByPage = new HashMap<String, HashMap<Object, Object>>();
				session.setAttribute(PAGE_SESSION_KEY, htByPage);
			}

			pageSession = htByPage.get(reqInfo.getPageId());
			if (pageSession == null) {
				pageSession = new HashMap<Object, Object>();
				htByPage.put(reqInfo.getPageId(), pageSession);
			}

			pageRefreshSession = htByPage.get(REFRESH_PREFIX
					+ reqInfo.getPageId());
			if (pageRefreshSession == null) {
				pageRefreshSession = new HashMap<Object, Object>();
				htByPage.put(REFRESH_PREFIX + reqInfo.getPageId(),
						pageRefreshSession);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(Object obj) {
		T t = (T) pageSession.get(obj);
		return t == null ? (T) pageRefreshSession.get(obj) : t;
	}

	@Override
	public void setAttribute(Object key, Object value) {
		setAttribute(key, value, false);
	}

	@Override
	public synchronized void setAttribute(Object key, Object value,
			boolean clearOnRefresh) {
		removeAttribute(key);

		if (value == null)
			return;

		if (clearOnRefresh)
			pageRefreshSession.put(key, value);
		else
			pageSession.put(key, value);
	}

	@Override
	public synchronized void removeAttribute(Object key) {
		pageSession.remove(key);
		pageRefreshSession.remove(key);
	}

	@Override
	public void refresh() {
		pageRefreshSession.clear();
	}
}
