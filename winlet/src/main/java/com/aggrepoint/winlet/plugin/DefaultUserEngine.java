package com.aggrepoint.winlet.plugin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.aggrepoint.winlet.UserEngine;
import com.aggrepoint.winlet.UserProfile;

/**
 * 缺省UserEngine，当应用没有指定的UserEngine时使用
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DefaultUserEngine implements UserEngine {
	static final String SESSION_KEY = DefaultUserEngine.class.getName()
			+ ".USER";

	static UserProfile ANONYMOUS = new UserProfile() {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean isAnonymous() {
			return true;
		}

		@Override
		public String getLoginId() {
			return "";
		}

		@Override
		public String getName() {
			return "";
		}
	};

	@Override
	public UserProfile getUser(HttpServletRequest req) {
		HttpSession session = req.getSession(false);
		UserProfile up = session == null ? null : (UserProfile) session
				.getAttribute(SESSION_KEY);
		if (up == null)
			up = ANONYMOUS;
		return up;
	}

	@Override
	public void setUser(HttpServletRequest req, UserProfile user) {
		if (user == null) {
			HttpSession session = req.getSession(false);
			if (session != null)
				session.removeAttribute(SESSION_KEY);
		} else
			req.getSession(true).setAttribute(SESSION_KEY, user);
	}
}
