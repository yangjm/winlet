package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 获取页面对象
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PageTag extends TagSupport {
	static final long serialVersionUID = 0;

	int m_iLevel;

	String m_strPage;

	String m_strName;

	public PageTag() {
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
			pageContext.setAttribute(m_strName,
					Utils.getPage(this, pageContext, m_strPage, m_iLevel));
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
