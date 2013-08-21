package com.aggrepoint.winlet.site.taglib;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.site.SiteContext;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * 
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
/*
 * <site:tree level="0" name="page0"> <site:name page="page0"/> <site:url
 * page="page0"/> <site:tree from="page0" name="page1"> <site:name
 * page="page1"/> </site:tree> </site:tree>
 */

public class TreeTag extends BodyTagSupport {
	static final long serialVersionUID = 0;

	int m_iLevel;

	String m_strParent;

	String m_strName;

	Enumeration<Page> m_enum;

	Page m_page;

	Page m_pagePreFetch;

	String m_strGap;

	/** 是否第一个页面 */
	boolean m_bFirst;

	public TreeTag() {
		m_iLevel = -1;
		m_strParent = null;
		m_strName = null;
		m_page = m_pagePreFetch = null;

	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public void setFrom(String parent) {
		m_strParent = parent;
	}

	public void setName(String name) {
		m_strName = name;
	}

	public void setGap(String gap) {
		m_strGap = gap;
	}

	public Page getPage() {
		return m_page;
	}

	/**
	 * 是否还有下一个页面
	 * 
	 * @return
	 */
	public boolean hasNext() {
		return m_enum.hasMoreElements();
	}

	/**
	 * 是否第一个页面
	 * 
	 * @return
	 */
	public boolean isFirst() {
		return m_bFirst;
	}

	Page nextElement() {
		if (m_enum.hasMoreElements())
			m_page = m_enum.nextElement();
		else
			m_page = null;

		return m_page;
	}

	public int doStartTag() throws JspException {
		try {
			SiteContext sc = (SiteContext) pageContext.getRequest()
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);
			AccessRuleEngine re = ContextUtils
					.getAccessRuleEngine((HttpServletRequest) pageContext
							.getRequest());

			Page page = null;
			Page currentInTree = sc.getPage();

			if (m_strParent != null) // 直接指定了要展现的根栏目
				page = (Page) pageContext.getAttribute(m_strParent);
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

			if (page == null)
				return SKIP_BODY;

			m_enum = Collections.enumeration(page.getPages(re, false, true));
			Object next = nextElement();
			if (next == null)
				return SKIP_BODY;

			m_bFirst = true;

			if (m_strName != null)
				pageContext.setAttribute(m_strName, next);
			return EVAL_BODY_BUFFERED;
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			body.writeOut(getPreviousOut());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		body.clearBody();

		Object next = nextElement();
		if (next == null)
			return SKIP_BODY;

		m_bFirst = false;

		try {
			if (m_strGap != null)
				body.write(m_strGap);
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		if (m_strName != null)
			pageContext.setAttribute(m_strName, next);
		return EVAL_BODY_BUFFERED;
	}
}
