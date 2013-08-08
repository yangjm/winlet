package com.aggrepoint.winlet.taglib;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.EnumMarkup;

/**
 * 用于声明使用的标记语言
 * 
 * 虽然AE在请求中会声明请求的标记语言类型，但应用端返回的页面不一定符合请求的标记类型。例如，请求中说明请求XHTML但应用不支持XHTML只能返回HTML
 * 。 页面可以使用本标记明确声明页面的语言类型。若页面没有使用本标记明确声明标记类型，则视为页面标记类型与请求类型一致。
 * 该标记在页面中必须先于其他Winlet标记出现
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class MarkupTag extends TagSupport {
	static final long serialVersionUID = 0;

	private static final String MARKUP_REQ_KEY = MarkupTag.class.getName()
			+ ".MARKUP";

	EnumMarkup markup;

	public void setType(String mk) {
		markup = EnumMarkup.fromName(mk);
	}

	public static EnumMarkup getMarkup(ServletRequest req) {
		Integer i = (Integer) req.getAttribute(MARKUP_REQ_KEY);
		if (i == null)
			return null;
		return EnumMarkup.fromId(i);
	}

	public static String getMarkupId(ServletRequest req) {
		EnumMarkup markup = getMarkup(req);
		if (markup == null)
			return null;
		return markup.getStrId();
	}

	public static String getMarkupName(ServletRequest req) {
		EnumMarkup markup = getMarkup(req);
		if (markup == null)
			return null;
		return markup.getName();
	}

	public static EnumMarkup getMarkup(HttpServletRequest req) {
		Integer i = (Integer) req.getAttribute(MARKUP_REQ_KEY);
		if (i == null)
			return null;
		return EnumMarkup.fromId(i);
	}

	public static String getMarkupId(HttpServletRequest req) {
		EnumMarkup markup = getMarkup(req);
		if (markup == null)
			return null;
		return markup.getStrId();
	}

	public static String getMarkupName(HttpServletRequest req) {
		EnumMarkup markup = getMarkup(req);
		if (markup == null)
			return null;
		return markup.getName();
	}

	public static EnumMarkup getMarkup() {
		return getMarkup(ContextUtils.getRequest());
	}

	public static String getMarkupId() {
		return getMarkupId(ContextUtils.getRequest());
	}

	public static String getMarkupName() {
		return getMarkupName(ContextUtils.getRequest());
	}

	public int doStartTag() throws JspException {
		try {
			pageContext.getRequest().setAttribute(MARKUP_REQ_KEY,
					new Integer(markup.getId()));
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}
