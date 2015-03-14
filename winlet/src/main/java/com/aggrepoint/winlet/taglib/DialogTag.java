package com.aggrepoint.winlet.taglib;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.codehaus.jackson.map.ObjectMapper;

import com.aggrepoint.winlet.WinletConst;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DialogTag extends BodyTagSupport implements WinletConst {
	private static final long serialVersionUID = 1L;

	String title;
	String close;
	ArrayList<HashMap<String, String>> buttons;

	public void setTitle(String title) {
		this.title = title;
	}

	public void setClose(String close) {
		this.close = close;
	}

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
		if (title != null)
			obj.put("title", title);
		if (close != null)
			obj.put("close", close);
		if (buttons.size() > 0)
			obj.put("buttons", buttons);

		Writer out = getPreviousOut();
		try {
			out.write("<div id=\"ap_dialog\">");
			out.write(new ObjectMapper().writeValueAsString(obj));
			out.write("</div>");
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}
		getBodyContent().clearBody();
		return EVAL_PAGE;
	}
}
