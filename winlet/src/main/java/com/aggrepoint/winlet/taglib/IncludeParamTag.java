package com.aggrepoint.winlet.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class IncludeParamTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	String name;
	String value;

	public void setName(String str) {
		name = str;
	}

	public void setValue(String str) {
		value = str;
	}

	public int doStartTag() throws JspException {
		IncludeTag include = (IncludeTag) TagSupport.findAncestorWithClass(
				this, IncludeTag.class);
		include.m_params.put(name, value);
		return SKIP_BODY;
	}
}
