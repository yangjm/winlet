package com.aggrepoint.winlet;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PageStorageImpl implements PageStorage {
	HashMap<Object, Object> winletSession = null;
	HashMap<Object, Object> winletRefreshSession = null;

	public static final String WINLET_SESSION_KEY_PREFIX = "com.aggrepoint.winlet.prefix";
	public static final String REFRESH_PREFIX = "REFRESH_";

	protected PageStorageImpl(ReqInfo reqInfo) {
		HttpSession session = reqInfo.getSession();
		synchronized (session) {
			String key = WINLET_SESSION_KEY_PREFIX
					+ reqInfo.getWinlet().toString();

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

			winletRefreshSession = htByWinlet.get(REFRESH_PREFIX
					+ reqInfo.getPageId());
			if (winletRefreshSession == null) {
				winletRefreshSession = new HashMap<Object, Object>();
				htByWinlet.put(REFRESH_PREFIX + reqInfo.getPageId(),
						winletRefreshSession);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAttribute(Object obj) {
		T t = (T) winletSession.get(obj);
		return t == null ? (T) winletRefreshSession.get(obj) : t;
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
			winletRefreshSession.put(key, value);
		else
			winletSession.put(key, value);
	}

	@Override
	public synchronized void removeAttribute(Object key) {
		winletSession.remove(key);
		winletRefreshSession.remove(key);
	}

	@Override
	public void refresh() {
		winletRefreshSession.clear();
	}
}
