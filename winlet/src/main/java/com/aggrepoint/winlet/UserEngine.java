package com.aggrepoint.winlet;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface UserEngine {
	public UserProfile getUser();

	public UserProfile getUser(HttpServletRequest req);

	public void setUser(UserProfile user);

	public void setUser(HttpServletRequest req, UserProfile user);
}
