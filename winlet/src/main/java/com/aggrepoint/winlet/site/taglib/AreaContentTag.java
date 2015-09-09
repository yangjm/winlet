package com.aggrepoint.winlet.site.taglib;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ReqInfo;
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

			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			SiteContext sc = (SiteContext) request
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);
			ReqInfo ri = ContextUtils.getReqInfo();

			StringBuffer sbContent = new StringBuffer();

			boolean inPreloadWinletTag = TagSupport.findAncestorWithClass(this,
					PreloadWinletTag.class) != null;

			List<Area> areas = sc.getPage().getAreas(m_strName);
			if (areas != null)
				for (Area area : areas)
					try {
						if (inPreloadWinletTag) // 包含在PreloadWinletTag中，由PreloadWinletTag来处理预加载的情况
							sbContent.append(area.getContent());
						else
							sbContent.append(PreloadWinletTag.preloadWinlet(ri,
									area.getContent(), false));
					} catch (Exception e) {
						e.printStackTrace();
					}

			out.print(sbContent.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
