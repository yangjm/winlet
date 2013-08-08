package com.aggrepoint.winlet.site.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.site.SiteContext;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Utils {
	/**
	 * 找到与当前标记相关的页面
	 * 
	 * @param tag
	 * @param context
	 * @param strPage
	 * @param iLevel
	 * @return
	 */
	public static Page getPage(TagSupport tag, PageContext context,
			String strPage, int iLevel) {
		Page page = null;

		SiteContext sc = (SiteContext) context.getRequest().getAttribute(
				SiteContext.SITE_CONTEXT_KEY);
		AccessRuleEngine re = ContextUtils
				.getAccessRuleEngine((HttpServletRequest) context.getRequest());

		if (strPage != null) {
			if (strPage.equals("AE_ROOT"))
				page = sc.getBranch().getHome(re);
			else
				page = (Page) context.getAttribute(strPage);
		} else if (iLevel >= 0) {
			Page currentInTree = sc.getPage();
			if (iLevel <= currentInTree.getLevel()) {
				page = currentInTree;
				while (iLevel < page.getLevel())
					page = page.getParent();
			}
		} else {
			TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(tag,
					TreeTag.class);
			if (tt != null)
				page = tt.getPage();
			else {
				PathTag pt = (PathTag) TagSupport.findAncestorWithClass(tag,
						PathTag.class);
				if (pt != null)
					page = pt.getPage();
			}
		}

		if (page == null)
			page = sc.getPage();

		return page;
	}
}
