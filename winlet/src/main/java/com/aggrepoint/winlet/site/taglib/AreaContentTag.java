package com.aggrepoint.winlet.site.taglib;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.site.SiteContext;
import com.aggrepoint.winlet.site.domain.Area;

/**
 * 栏位
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class AreaContentTag extends TagSupport {
	static final long serialVersionUID = 0;

	String m_strName;

	public AreaContentTag() {
	}

	public void setName(String name) {
		m_strName = name;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			SiteContext sc = (SiteContext) pageContext.getRequest()
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);

			StringBuffer sb = new StringBuffer();

			List<Area> areas = sc.getPage().getAreas(m_strName);
			if (areas != null)
				for (Area area : areas)
					sb.append(area.getContent());

			out.print(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
