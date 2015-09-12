package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.site.SiteContext;

/**
 * 构造页面的URL
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PathUrlTag extends TagSupport {
	static final long serialVersionUID = 0;

	String m_strPath;

	public PathUrlTag() {
		m_strPath = null;
	}

	public void setPage(String page) {
		m_strPath = page;
	}

	public int doStartTag() throws JspException {
		try {
			SiteContext sc = (SiteContext) pageContext.getRequest()
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);

			pageContext.getOut().print(sc.getPageUrl(m_strPath));
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
