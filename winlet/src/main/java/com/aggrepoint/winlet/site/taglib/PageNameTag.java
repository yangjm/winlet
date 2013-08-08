package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.site.domain.Page;

/**
 * 输出页面名称
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PageNameTag extends TagSupport {
	static final long serialVersionUID = 0;

	String m_strPage;

	int m_iLevel;

	public PageNameTag() {
		m_strPage = null;
		m_iLevel = -1;
	}

	public void setPage(String page) {
		m_strPage = page;
	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public int doStartTag() throws JspException {
		try {
			Page page = Utils.getPage(this, pageContext, m_strPage, m_iLevel);
			if (page != null)
				pageContext.getOut().print(page.getName());
		} catch (Throwable e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
