package com.aggrepoint.winlet.taglib;

import java.util.Hashtable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.form.InputImpl;

public class GetInputTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	String strVar;
	String strType;
	String strName;
	Object object;
	String strProperty;
	Object objValue;
	String strValidate;
	String strError;
	boolean disabled;
	Hashtable<String, Object> htAttrs;
	FormTag form;
	InputImpl input;

	public void setVar(String name) {
		strVar = name;
	}

	public void setType(String name) {
		strType = name;
	}

	public void setName(String name) {
		strName = name;
	}

	public void setObject(Object obj) {
		object = obj;
	}

	public void setProperty(String name) {
		strProperty = name;
	}

	public void setValue(Object val) {
		objValue = val;
	}

	public void setValidate(String name) {
		strValidate = name;
	}

	public void setError(String str) {
		strError = str;
	}

	public void setDisabled(Object b) {
		if (b == null)
			disabled = false;
		else if (b instanceof Boolean)
			disabled = ((Boolean) b).booleanValue();
		else {
			String str = b.toString();
			disabled = str.equalsIgnoreCase("y") || str.equalsIgnoreCase("yes");
		}
	}

	public int doStartTag() throws JspException {
		form = (FormTag) TagSupport.findAncestorWithClass(this, FormTag.class);
		input = form.m_form.getInput(strType, strName);
		input.init(object, strProperty, objValue, disabled);
		input.setDefaultError(strError);
		form.m_currentInput = input;
		htAttrs = new Hashtable<String, Object>();
		input.setValidator(strValidate);

		return (EVAL_BODY_BUFFERED);
	}

	public int doAfterBody() {
		for (String key : htAttrs.keySet())
			input.setAttr(key, htAttrs.get(key));

		form.m_currentInput = null;
		pageContext.setAttribute(strVar, input);

		bodyContent.clearBody();
		return SKIP_BODY;
	}
}