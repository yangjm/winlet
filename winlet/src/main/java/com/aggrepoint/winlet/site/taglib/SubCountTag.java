package com.aggrepoint.winlet.site.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.site.SiteContext;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * 计算在指定栏目下包含多少个子栏目
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
/*
 * <ae:subcount level="0"/>
 * 
 * <ae:tree level="0" name="page0"> <ae:tree root="page0" name="page1">
 * <ae:subcount page="page1"/> </ae:tree> </ae:tree>
 * 
 * <ae:subcount level="0" name="ccc"/> <%= ccc * 3 %>
 */
public class SubCountTag extends TagSupport {
	static final long serialVersionUID = 0;

	int m_iLevel;

	String m_strPage;

	String m_strName;

	public SubCountTag() {
		m_strPage = null;
		m_strName = null;
		m_iLevel = -1;
	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public void setPage(String page) {
		m_strPage = page;
	}

	public void setName(String name) {
		m_strName = name;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();
			int count = 0;

			SiteContext sc = (SiteContext) pageContext.getRequest()
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);
			AccessRuleEngine re = ContextUtils
					.getAccessRuleEngine((HttpServletRequest) pageContext
							.getRequest());

			Page page = null;
			Page currentInTree = sc.getPage();

			if (m_strPage != null) // 直接指定了要展现的根栏目
				page = (Page) pageContext.getAttribute(m_strPage);
			else if (m_iLevel > -1) { // 指定了要展现的级别
				if (m_iLevel <= currentInTree.getLevel()) {
					page = currentInTree;
					while (m_iLevel < page.getLevel())
						page = page.getParent();
				}
			} else { // 没有指定根栏目
				TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(this,
						TreeTag.class);
				if (tt != null)
					page = tt.getPage();
				else
					page = sc.getBranch().getHome(re);
			}

			if (page != null)
				count = page.getPages(re, false, true).size();

			if (m_strName == null)
				out.print(count);
			else
				pageContext.setAttribute(m_strName, new Integer(count));
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
