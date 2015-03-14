package com.aggrepoint.winlet;

import javax.servlet.http.HttpServletRequest;

public class UrlConstructor {
	private String pageRoot;
	private String resRoot;

	public UrlConstructor(HttpServletRequest req) {
		pageRoot = req.getContextPath() + "/site";
		resRoot = req.getContextPath();
	}

	public String getPageUrl(String path) {
		if (path.startsWith("/"))
			return pageRoot + path;
		return pageRoot + "/" + path;
	}

	public String getResourceUrl(String path) {
		if (path.startsWith("/"))
			return resRoot + path;
		return resRoot + "/" + path;
	}
}
