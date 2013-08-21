package com.aggrepoint.winlet.site.taglib;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.site.SiteContext;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * 
 *         <ae:eval type="focus" page="page0" name="bbbb"/> <% if
 *         (bbbb.booleanValue()) { %>aaa<% } else { %>bbb<% } %>
 *         
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class EveluateTag extends TagSupport {
	static final long serialVersionUID = 0;

	String m_strType;

	String m_strPage;

	String m_strName;

	int m_iLevel;

	public EveluateTag() {
		m_strType = "";
		m_strName = null;
		m_strPage = null;
		m_iLevel = -1;
	}

	public void setType(String type) {
		m_strType = type;
	}

	public void setPage(String page) {
		m_strPage = page;
	}

	public void setLevel(int level) {
		m_iLevel = level;
	}

	public void setName(String name) {
		m_strName = name;
	}

	public int doStartTag() throws JspException {
		try {
			SiteContext sc = (SiteContext) pageContext.getRequest()
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);
			AccessRuleEngine re = ContextUtils
					.getAccessRuleEngine((HttpServletRequest) pageContext
							.getRequest());

			Page currentPage = sc.getPage();

			boolean bResult = false;

			Page page = Utils.getPage(this, pageContext, m_strPage, m_iLevel);

			if (m_strType.equals("hassub")) { // 判断指定的页是否包含子页面
				if (page.getPages(re, false, true).size() > 0)
					bResult = true;
			} else if (m_strType.equals("current")) { // 判断指定的页是否当前页
				if (currentPage == page)
					bResult = true;
			} else if (m_strType.equals("focus")) { // 判断指定的页是否当前页或当前页的直接上级页面
				while (currentPage != null
						&& currentPage.getParent() != currentPage
						&& currentPage != page)
					currentPage = currentPage.getParent();
				if (currentPage == page)
					bResult = true;
			} else if (m_strType.equals("first")) { // 用于在tree标记中，判断当前页面是否第一个页面
				TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(this,
						TreeTag.class);
				if (tt != null)
					if (tt.isFirst())
						bResult = true;
			} else if (m_strType.equals("last")) { // 用于在tree标记中，判断当前页面是否还有后续页面
				TreeTag tt = (TreeTag) TagSupport.findAncestorWithClass(this,
						TreeTag.class);
				if (tt != null)
					if (!tt.hasNext())
						bResult = true;
			}

			pageContext.setAttribute(m_strName, new Boolean(bResult));
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
