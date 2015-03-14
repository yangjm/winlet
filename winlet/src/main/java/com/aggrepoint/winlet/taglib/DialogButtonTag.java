package com.aggrepoint.winlet.taglib;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Button的label和onclick属性会被框架处理，其他属性直接被带到对话框的上（如果对话框控件支持的话）。
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DialogButtonTag extends BodyTagSupport implements
		DynamicAttributes {
	private static final long serialVersionUID = 1L;

	HashMap<String, String> button = new HashMap<String, String>();

	public void setDynamicAttribute(String uri, String localName, Object value)
			throws JspException {
		button.put(localName, value.toString());
	}

	@Override
	public int doStartTag() throws JspException {
		return (EVAL_BODY_BUFFERED);
	}

	public int doAfterBody() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(baos, "UTF-8");
			bodyContent.writeOut(writer);
			writer.flush();
			button.put("label", baos.toString("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		DialogTag dialog = (DialogTag) TagSupport.findAncestorWithClass(this,
				DialogTag.class);
		dialog.addButton(button);
		button = new HashMap<String, String>();
		bodyContent.clearBody();

		return SKIP_BODY;
	}
}
