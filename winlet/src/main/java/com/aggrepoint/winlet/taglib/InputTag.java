package com.aggrepoint.winlet.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * 用于在Window页面中构造Form
 * 
 * @author YJM
 */
public class InputTag extends SimpleTagSupport {
	protected String m_strType;

	protected String m_strName;

	protected String m_strValue;

	protected String m_strChecked;

	protected String m_strOnclick;

	public void setName(String name) {
		m_strName = name;
	}

	public void setType(String type) {
		m_strType = type;
	}

	public void setValue(String str) {
		m_strValue = str;
	}

	public void setChecked(String checked) {
		m_strChecked = checked;
	}

	public void setOnclick(String onclick) {
		m_strOnclick = onclick;
	}

	public void doTag() throws JspException {
		try {
			JspWriter out = getJspContext().getOut();
			out.print("<input type=\"");
			out.print(m_strType);
			out.print("\" name=\"");
			out.print(m_strName);
			if (m_strValue != null) {
				out.print("\" value=\"");
				out.print(m_strValue);
			}
			if (m_strChecked != null && m_strChecked.equalsIgnoreCase("true"))
				out.print("\" checked=\"checked");
			if (m_strOnclick != null) {
				out.print("\" onclick=\"");
				out.print(m_strOnclick);
			}
			out.print("\"/>");
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
	}
}