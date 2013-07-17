package com.aggrepoint.winlet.taglib;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ReqInfo;

/**
 * 用于生成可支持Ajax表单提交Javascript脚本
 * 
 * @author YJM
 */
public class JSSubmitFormTag extends TagSupport {
	private static final long serialVersionUID = 1L;

	/** 用于指定要提交的表单的名称 */
	String m_strForm = null;

	/** 用于指定要提交的表单的对象 */
	String m_strName = null;

	public void setName(String name) {
		m_strName = name;
	}

	public void setForm(String form) {
		m_strForm = form;
	}

	public int doStartTag() throws JspException {
		try {
			ReqInfo reqInfo = ContextUtils.getReqInfo();

			if (m_strName == null && m_strForm == null)
				throw new JspException("必须指定name或者form属性。");

			if (reqInfo != null) {
				JspWriter out = pageContext.getOut();

				String str = null;
				if (m_strForm != null) {
					str = "document." + m_strForm;
					str += reqInfo.getWinId();
					str += reqInfo.getViewId();
				} else
					str = m_strName;

				// if (reqInfo.m_markup == EnumMarkup.HTML) {
				out.print("if (");
				out.print(str);
				out.print(".onsubmit()) ");
				// }
				out.print(str);
				out.print(".submit();");
			}
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}
}
