package com.aggrepoint.winlet;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

public class WinletStorageImpl implements WinletStorage {
	HashMap<Object, Object> winletSession = null;

	public static final String WINLET_SESSION_KEY_PREFIX = "com.aggrepoint.winlet.prefix";

	protected WinletStorageImpl(ReqInfo reqInfo) {
		HttpSession session = reqInfo.getSession();
		synchronized (session) {
			String key = WINLET_SESSION_KEY_PREFIX
					+ reqInfo.getViewInstance().getWinlet().toString();

			@SuppressWarnings("unchecked")
			HashMap<String, HashMap<Object, Object>> htByWinlet = (HashMap<String, HashMap<Object, Object>>) session
					.getAttribute(key);
			if (htByWinlet == null) {
				htByWinlet = new HashMap<String, HashMap<Object, Object>>();
				session.setAttribute(key, htByWinlet);
			}

			winletSession = htByWinlet.get(reqInfo.getPageId());
			if (winletSession == null) {
				winletSession = new HashMap<Object, Object>();
				htByWinlet.put(reqInfo.getPageId(), winletSession);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(Object obj) {
		return (T) winletSession.get(obj);
	}

	@Override
	public void setAttribute(Object key, Object value) {
		if (value == null)
			winletSession.remove(key);
		else
			winletSession.put(key, value);
	}

	@Override
	public void removeAttribute(Object key) {
		winletSession.remove(key);
	}
}
