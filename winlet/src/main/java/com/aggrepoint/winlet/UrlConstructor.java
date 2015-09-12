package com.aggrepoint.winlet;

import javax.servlet.http.HttpServletRequest;

public class UrlConstructor {
	private String pageRoot;
	private String resRoot;
	private String urlPrefix;

	public UrlConstructor(HttpServletRequest req, String urlPrefix) {
		pageRoot = req.getContextPath() + "/site";
		resRoot = req.getContextPath();
		this.urlPrefix = urlPrefix;
	}

	public String getPageUrl(String path) {
		String url = null;
		if (path.startsWith("/"))
			url = pageRoot + path;
		else
			url = pageRoot + "/" + path;

		if (urlPrefix != null && url.startsWith(urlPrefix))
			return url.substring(urlPrefix.length());

		return url;
	}

	public String getResourceUrl(String path) {
		if (path.startsWith("/"))
			return resRoot + path;
		return resRoot + "/" + path;
	}
}
