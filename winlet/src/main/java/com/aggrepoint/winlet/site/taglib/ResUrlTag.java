package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.site.SiteContext;

/**
 * 构造模板资源的URL
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ResUrlTag extends TagSupport {
	static final long serialVersionUID = 0;

	String m_strName;

	public void setName(String name) {
		m_strName = name;
		if (m_strName.startsWith("/"))
			m_strName = m_strName.substring(1);
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			SiteContext sc = (SiteContext) pageContext.getRequest()
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);

			out.print(sc.getResUrl(m_strName));
		} catch (Exception e) {
			e.printStackTrace();

			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
