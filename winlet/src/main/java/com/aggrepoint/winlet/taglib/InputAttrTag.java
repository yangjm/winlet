package com.aggrepoint.winlet.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 
 * @author YJM
 */
public class InputAttrTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	String strName;
	Object objValue;

	public void setName(String str) {
		strName = str;
	}

	public void setValue(Object str) {
		objValue = str;
	}

	public int doStartTag() throws JspException {
		try {
			GetInputTag getInput = (GetInputTag) TagSupport
					.findAncestorWithClass(this, GetInputTag.class);
			if (getInput != null && objValue != null)
				getInput.htAttrs.put(strName, objValue);
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}

		return SKIP_BODY;
	}
}