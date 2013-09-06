package com.aggrepoint.dao;

import com.icebean.core.common.ThreadContext;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public class UserContext {
	static final String CONTEXT_KEY = UserContext.class.getName();

	public static void setUser(String id) {
		ThreadContext.setAttribute(CONTEXT_KEY, id);
	}

	public static String getUser() {
		String id = (String) ThreadContext.getAttribute(CONTEXT_KEY);
		return id == null ? "" : id;
	}
}
