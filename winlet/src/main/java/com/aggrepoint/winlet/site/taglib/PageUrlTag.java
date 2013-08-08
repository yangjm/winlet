package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.site.SiteContext;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * 构造页面的URL
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PageUrlTag extends TagSupport {
	static final long serialVersionUID = 0;

	String m_strPage;

	int m_iLevel;

	public PageUrlTag() {
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
			JspWriter out = pageContext.getOut();

			SiteContext sc = (SiteContext) pageContext.getRequest()
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);

			Page page = Utils.getPage(this, pageContext, m_strPage, m_iLevel);

			if (page != null) {
				if (page.getLink() != null)
					out.print(page.getLink());
				else
					out.print(sc.getPageUrl(page.getFullPath()));
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
