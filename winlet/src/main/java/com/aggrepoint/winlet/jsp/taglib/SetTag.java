package com.aggrepoint.winlet.jsp.taglib;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class SetTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	String var;

	public void setVar(String var) {
		this.var = var;
	}

	@Override
	public int doEndTag() throws JspTagException {
		try {
			if (getBodyContent() != null) {
				pageContext.setAttribute(var, getBodyContent().getString());
				getBodyContent().clearBody();
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspTagException(e.getMessage());
		}

		return EVAL_PAGE;
	}
}
