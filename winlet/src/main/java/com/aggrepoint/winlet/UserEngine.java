package com.aggrepoint.winlet;


import javax.servlet.http.HttpServletRequest;


public interface UserEngine {
	public UserProfile getUser(HttpServletRequest req);

	public void setUser(HttpServletRequest req, UserProfile user);
}
