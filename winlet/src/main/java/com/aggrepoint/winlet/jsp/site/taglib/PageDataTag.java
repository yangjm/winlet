package com.aggrepoint.winlet.jsp.site.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.site.domain.Page;

/**
 * 输出页面数据
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PageDataTag extends TagSupport {
	static final long serialVersionUID = 0;

	String strPage;

	int iLevel;

	String strName;

	public PageDataTag() {
		strPage = null;
		iLevel = -1;
	}

	public void setPage(String page) {
		strPage = page;
	}

	public void setLevel(int level) {
		iLevel = level;
	}

	public void setName(String name) {
		strName = name;
	}

	public int doStartTag() throws JspException {
		try {
			Page page = Utils.getPage(this, pageContext, strPage, iLevel);
			String data = page.getData(strName);
			if (page != null)
				pageContext.getOut().print(data == null ? "" : data);
		} catch (Throwable e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
