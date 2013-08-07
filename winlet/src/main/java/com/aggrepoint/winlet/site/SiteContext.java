package com.aggrepoint.winlet.site;

import com.aggrepoint.winlet.site.domain.Branch;
import com.aggrepoint.winlet.site.domain.Page;

public class SiteContext {
	public static final String SITE_CONTEXT_KEY = "SITE_CONTEXT";

	private String pageUrlRoot;
	private String resUrlRoot;
	/** 当前分支 */
	private Branch branch;
	/** 当前页面 */
	private Page page;

	public SiteContext(String pageRoot, String resRoot, Branch b, Page p) {
		pageUrlRoot = pageRoot;
		resUrlRoot = resRoot;
		branch = b;
		page = p;
	}

	public Branch getBranch() {
		return branch;
	}

	public Page getPage() {
		return page;
	}

	public String getPageUrl(String path) {
		return pageUrlRoot + path;
	}

	public String getResUrl(String name) {
		if (name.startsWith("/"))
			return resUrlRoot + name;
		else
			return resUrlRoot + "/" + name;
	}
}
