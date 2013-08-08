package com.aggrepoint.winlet.taglib;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Element extends BodyTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 1L;

	String elm;
	Vector<String> vecAttrNames = new Vector<String>();
	Vector<String> vecAttrValues = new Vector<String>();

	public void setElm(String elm) {
		this.elm = elm;
	}

	public void setDynamicAttribute(String uri, String localName, Object value)
			throws JspException {
		vecAttrNames.add(localName);
		vecAttrValues.add(value.toString());
	}

	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			Writer out = getPreviousOut();
			out.write("<");
			out.write(elm);
			Enumeration<String> enum1 = vecAttrNames.elements();
			Enumeration<String> enum2 = vecAttrValues.elements();
			for (; enum1.hasMoreElements() && enum2.hasMoreElements();) {
				out.write(" ");
				out.write(enum1.nextElement());
				out.write("=\"");
				out.write(enum2.nextElement());
				out.write("\"");
			}
			out.write(">");
			body.writeOut(out);
			out.write("</");
			out.write(elm);
			out.write(">");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		body.clearBody();
		vecAttrNames.clear();
		vecAttrValues.clear();
		return SKIP_BODY;
	}
}
