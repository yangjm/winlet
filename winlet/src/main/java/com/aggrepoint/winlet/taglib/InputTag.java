package com.aggrepoint.winlet.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class InputTag extends TagSupport {
	static final long serialVersionUID = 0;
	private String accept;
	private String alt;
	private String checked;
	private String disabled;
	private String maxlength;
	private String name;
	private String readonly;
	private String size;
	private String src;
	private String type;
	private String value;

	public void setAccept(String accept) {
		this.accept = accept;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public void setDisabled(String disabled) {
		this.disabled = disabled;
	}

	public void setMaxlength(String maxlength) {
		this.maxlength = maxlength;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setReadonly(String readonly) {
		this.readonly = readonly;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int doStartTag() throws JspException {
		try {
			StringBuffer sb = new StringBuffer();

			sb.append("<input type=\"").append(type).append("\"");

			if (name != null)
				sb.append(" name=\"" + name + "\"");
			if (value != null)
				sb.append(" value=\"" + value + "\"");
			if (size != null)
				sb.append(" size=\"" + size + "\"");
			if (maxlength != null)
				sb.append(" maxlength=\"" + maxlength + "\"");
			if (checked != null)
				sb.append(" checked=\"" + checked + "\"");
			if (disabled != null)
				sb.append(" disabled=\"" + disabled + "\"");
			if (readonly != null)
				sb.append(" readonly=\"" + readonly + "\"");
			if (accept != null)
				sb.append(" accept=\"" + accept + "\"");
			if (alt != null)
				sb.append(" alt=\"" + alt + "\"");
			if (src != null)
				sb.append(" src=\"" + src + "\"");

			sb.append("/>");

			pageContext.getOut().print(sb.toString());
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}
