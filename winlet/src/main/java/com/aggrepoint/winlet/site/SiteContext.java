package com.aggrepoint.winlet.site;

import javax.servlet.http.HttpServletRequest;

import com.aggrepoint.winlet.UrlConstructor;
import com.aggrepoint.winlet.site.domain.Branch;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class SiteContext {
	public static final String SITE_CONTEXT_KEY = "SITE_CONTEXT";

	/** 当前页面 */
	private Page page;
	private UrlConstructor uc;

	public SiteContext(HttpServletRequest req, Page p) {
		uc = new UrlConstructor(req);
		page = p;
	}

	public Branch getBranch() {
		return page.getBranch();
	}

	public Page getPage() {
		return page;
	}

	public String getPageUrl(String path) {
		return uc.getPageUrl(path);
	}

	public String getResUrl(String path) {
		return uc.getResourceUrl(path);
	}
}
