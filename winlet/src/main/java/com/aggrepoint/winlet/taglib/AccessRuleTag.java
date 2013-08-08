package com.aggrepoint.winlet.taglib;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.WinletConst;

/**
 * 访问控制
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class AccessRuleTag extends BodyTagSupport implements WinletConst {
	private static final long serialVersionUID = 1L;

	static final Log logger = LogFactory.getLog(AccessRuleTag.class);

	protected String rule;

	public void setRule(String rule) {
		this.rule = rule;
	}

	public int doStartTag() throws JspException {
		boolean result = false;

		try {
			result = ContextUtils.getAccessRuleEngine(
					(HttpServletRequest) pageContext.getRequest()).eval(rule);
		} catch (Exception e) {
			logger.error("Error evaluating access rule defined in JSP: \""
					+ rule + "\"", e);
		}

		if (result)
			return EVAL_BODY_BUFFERED;
		else
			return SKIP_BODY;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			body.writeOut(getPreviousOut());
		} catch (IOException e) {
			throw new JspTagException(e.getMessage());
		}

		body.clearBody();
		return SKIP_BODY;
	}
}