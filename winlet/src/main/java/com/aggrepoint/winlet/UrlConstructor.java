package com.aggrepoint.winlet;

import javax.servlet.http.HttpServletRequest;

public class UrlConstructor {
	private String pageRoot;
	private String resRoot;
	private String urlPrefix;
	private String urlPrepend;

	public UrlConstructor(HttpServletRequest req, String urlPrefix, String urlPrepend) {
		pageRoot = req.getContextPath() + "/site";
		resRoot = req.getContextPath();
		this.urlPrefix = urlPrefix;
		this.urlPrepend = urlPrepend;
	}

	public String getPageUrl(String path) {
		String url = null;
		if (path.startsWith("/"))
			url = pageRoot + path;
		else
			url = pageRoot + "/" + path;

		if (urlPrefix != null && url.startsWith(urlPrefix))
			url = url.substring(urlPrefix.length());

		if (urlPrepend != null)
			if (url.startsWith("/"))
				url = urlPrepend + url;
			else
				url = urlPrepend + "/" + url;

		return url;
	}

	public String getResourceUrl(String path) {
		if (path.startsWith("/"))
			return resRoot + path;
		return resRoot + "/" + path;
	}
}
