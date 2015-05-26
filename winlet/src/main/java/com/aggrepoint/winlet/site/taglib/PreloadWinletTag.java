package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.winlet.ContextUtils;

/**
 * 
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */

public class PreloadWinletTag extends BodyTagSupport {
	static final long serialVersionUID = 0;

	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			getPreviousOut().write(
					AreaContentTag.preloadWinlet(ContextUtils.getReqInfo(),
							body.getString()));
		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}

		body.clearBody();

		return SKIP_BODY;
	}
}
