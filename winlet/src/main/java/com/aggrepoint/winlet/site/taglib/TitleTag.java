package com.aggrepoint.winlet.site.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.winlet.site.SiteContext;

/**
 * 显示页面title。如果cfg.cfg中没有指定title，则显示缺省的title。
 * 
 * <site:title>缺省Title</site:title>
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class TitleTag extends BodyTagSupport {
	static final long serialVersionUID = 0;

	public int doStartTag() throws JspException {
		SiteContext sc = (SiteContext) pageContext.getRequest().getAttribute(
				SiteContext.SITE_CONTEXT_KEY);
		if (sc.getPage().getTitle() != null) {
			try {
				pageContext.getOut().print(
						"<title>" + sc.getPage().getTitle() + "</title>");
			} catch (Exception e) {
				throw new JspException(e.getMessage());
			}
			return SKIP_BODY;
		}
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			JspWriter out = getPreviousOut();
			out.write("<title>");
			body.writeOut(out);
			out.write("</title>");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		body.clearBody();
		return SKIP_BODY;
	}
}
