package com.aggrepoint.winlet.site.taglib;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.winlet.site.SiteContext;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * 
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */

/*
 * <ae:path level="0" name="page" gap="&nbsp;&nbsp;"><a href="<ae:url
 * page="page"/>"><ae:name page="page"/></a></ae:path>
 */

public class PathTag extends BodyTagSupport {
	static final long serialVersionUID = 0;

	int m_iLevel;

	String m_strName;

	String m_strGap;

	Enumeration<Page> m_enum;

	Page m_page;

	public PathTag() {
		m_iLevel = 0;
		m_page = null;
		m_strName = null;
	}

	public void setLevel(int level) {
		if (level >= 0)
			m_iLevel = level;
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

	Object nextElement() {
		while (m_enum.hasMoreElements()) {
			m_page = m_enum.nextElement();
			return m_page;
		}

		m_page = null;
		return null;
	}

	public int doStartTag() throws JspException {
		try {
			SiteContext sc = (SiteContext) pageContext.getRequest()
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);

			Page currentInTree = sc.getPage();

			Vector<Page> vecPages = new Vector<Page>();
			if (currentInTree != null) {
				vecPages.add(currentInTree);
				while (currentInTree.getParent() != null
						&& currentInTree.getParent() != currentInTree) {
					currentInTree = currentInTree.getParent();
					vecPages.add(currentInTree);
				}
			}

			// 将vecPages的顺序调转过来
			Vector<Page> vec = new Vector<Page>();
			for (int i = vecPages.size() - 1 - m_iLevel; i >= 0; i--)
				vec.add(vecPages.elementAt(i));

			m_enum = vec.elements();

			Object next = nextElement();
			if (next == null)
				return SKIP_BODY;

			if (m_strName == null)
				pageContext.setAttribute(m_strName, next);
			return EVAL_BODY_BUFFERED;
		} catch (Exception e) {
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

		try {
			getPreviousOut().write(m_strGap);
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		if (m_strName == null)
			pageContext.setAttribute(m_strName, next);
		return EVAL_BODY_BUFFERED;
	}
}
