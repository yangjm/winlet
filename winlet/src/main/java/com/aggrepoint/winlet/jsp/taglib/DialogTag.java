package com.aggrepoint.winlet.jsp.taglib;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DialogTag extends BodyTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 1L;

	HashMap<String, String> attributes = new HashMap<String, String>();
	ArrayList<HashMap<String, String>> buttons;

	void addButton(HashMap<String, String> button) {
		buttons.add(button);
	}

	@Override
	public int doStartTag() {
		buttons = new ArrayList<HashMap<String, String>>();
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() {
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspTagException {
		HashMap<String, Object> obj = new HashMap<String, Object>();
		for (String key : attributes.keySet())
			obj.put(key, attributes.get(key));
		if (buttons.size() > 0)
			obj.put("buttons", buttons);

		Writer out = pageContext.getOut();
		try {
			out.write("<div id=\"winlet_dialog\" style=\"display:none\">");
			out.write(new ObjectMapper().writeValueAsString(obj));
			out.write("</div>");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		if (getBodyContent() != null)
			getBodyContent().clearBody();

		attributes = new HashMap<String, String>();
		return EVAL_PAGE;
	}

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value)
			throws JspException {
		attributes.put(localName, value.toString());
	}
}
