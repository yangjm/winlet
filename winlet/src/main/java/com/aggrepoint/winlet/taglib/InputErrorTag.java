package com.aggrepoint.winlet.taglib;

import java.io.StringWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.form.InputImpl;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class InputErrorTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	InputValidatorTag validator;

	InputImpl input;

	public int doStartTag() throws JspException {
		validator = (InputValidatorTag) TagSupport.findAncestorWithClass(this,
				InputValidatorTag.class);
		if (validator == null) {
			FormTag form = (FormTag) TagSupport.findAncestorWithClass(this,
					FormTag.class);
			if (form == null || form.m_currentInput == null)
				return SKIP_BODY;
			input = form.m_currentInput;
		}
		return (EVAL_BODY_BUFFERED);
	}

	public int doAfterBody() {
		StringWriter writer = new StringWriter();
		try {
			bodyContent.writeOut(writer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (validator != null)
			validator.strError = writer.toString();
		else
			input.setDefaultError(writer.toString());
		bodyContent.clearBody();
		return SKIP_BODY;
	}
}