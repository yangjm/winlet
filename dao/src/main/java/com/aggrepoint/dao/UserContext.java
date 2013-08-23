package com.aggrepoint.dao;

import com.icebean.core.common.ThreadContext;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 *
 */
public class UserContext {
	public static void setUser(String id) {
		ThreadContext.setAttribute(UserContext.class.getName(), id);
	}

	public static String getUser() {
		String id = (String) ThreadContext.getAttribute(UserContext.class
				.getName());
		return id == null ? "" : id;
	}
}
