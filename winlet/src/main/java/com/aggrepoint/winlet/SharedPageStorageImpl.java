package com.aggrepoint.winlet;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class SharedPageStorageImpl implements SharedPageStorage {
	HashMap<Object, Object> pageSession = null;

	public static final String PAGE_SESSION_KEY = "com.aggrepoint.page";

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
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(Object obj) {
		return (T) pageSession.get(obj);
	}

	@Override
	public void setAttribute(Object key, Object value) {
		if (value == null)
			pageSession.remove(key);
		else
			pageSession.put(key, value);
	}

	@Override
	public void removeAttribute(Object key) {
		pageSession.remove(key);
	}
}
