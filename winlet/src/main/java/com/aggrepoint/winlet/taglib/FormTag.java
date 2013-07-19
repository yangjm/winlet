package com.aggrepoint.winlet.taglib;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ReqConst;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.WinletConst;
import com.aggrepoint.winlet.WinletManager;
import com.aggrepoint.winlet.form.FormImpl;
import com.aggrepoint.winlet.form.InputImpl;

/**
 * Form
 * 
 * @author YJM
 */
public class FormTag extends BodyTagSupport implements WinletConst {
	private static final long serialVersionUID = 1L;

	protected String m_strName;

	protected String m_strAction = "";

	protected String m_strMethod;

	protected String m_strEncType;

	protected String m_strFocus;

	protected boolean m_bValidate;

	protected Object m_objResetRef;

	protected boolean m_bHideLoading;

	protected String m_strUpdate;

	/** 表单对象 */
	protected FormImpl m_form;

	protected InputImpl m_currentInput;

	/** 由嵌套在FormTag中的FunctionTag生成的脚本 */
	Hashtable<String, String> m_htScripts;
	/** 由嵌套在FormTag中的FunctionTag生成的，要用winform函数生成的函数 */
	Hashtable<String, String> m_htFormScripts;

	public void setName(String name) {
		m_strName = name;
	}

	public void setAction(String action) {
		m_strAction = action;
	}

	public void setMethod(String str) {
		m_strMethod = str;
	}

	public void setEnctype(String str) {
		m_strEncType = str;
	}

	public void setFocus(String focus) {
		m_strFocus = focus;
	}

	public String getName() {
		return m_strName;
	}

	public void setValidate(String val) {
		m_bValidate = "yes".equalsIgnoreCase(val);
	}

	public void setResetref(Object obj) {
		m_objResetRef = obj;
	}

	public void setHideloading(String val) {
		m_bHideLoading = "yes".equalsIgnoreCase(val);
	}

	public void setUpdate(String update) {
		m_strUpdate = update;
	}

	public void setScript(String func, String content) {
		if (m_htScripts == null) {
			m_htScripts = new Hashtable<String, String>();
			m_htFormScripts = new Hashtable<String, String>();
		}
		m_htScripts.put(func, content);
		m_htFormScripts.remove(func);
	}

	public void setFormScript(String func, String def) {
		if (m_htFormScripts == null) {
			m_htScripts = new Hashtable<String, String>();
			m_htFormScripts = new Hashtable<String, String>();
		}
		m_htFormScripts.put(func, def);
		m_htScripts.remove(func);
	}

	public int doStartTag() throws JspException {
		m_htScripts = null;
		m_htFormScripts = null;

		ReqInfo ri = ContextUtils.getReqInfo();

		m_form = WinletManager.getForm(ri, m_strName);
		m_form.setAction(m_strAction);
		if (m_objResetRef != null && !(m_objResetRef == m_form.getResetRef())) {
			m_form.reset();
			m_form.setResetRef(m_objResetRef);
		}

		try {
			JspWriter out = pageContext.getOut();

			// {Name
			out.print("<form name=\"");
			out.print(m_strName);
			out.print(ri.getWinId());
			out.print(ri.getViewId());
			// }

			out.print("\" id=\"");
			out.print(m_strName);
			out.print(ri.getWinId());
			out.print(ri.getViewId());
			out.print("\"");

			if (m_bHideLoading)
				out.print(" hideloading=\"yes\"");

			out.print(" action=\"");
			out.print(ri.getPath());
			out.print("?");
			out.print(ReqConst.PARAM_WIN_ACTION);
			out.print("=");
			out.print(ri.getWinId());
			out.print("!");
			out.print(ri.getViewId());
			out.print("!");
			out.print(m_strAction);
			out.print("!");
			out.print(m_form.getId());
			out.print("\"");

			// {Method
			if (m_strMethod == null)
				out.print(" method=\"get\"");
			else {
				out.print(" method=\"");
				out.print(m_strMethod);
				out.print("\"");
			}
			// }

			// Enctype
			if (m_strEncType != null && !m_strEncType.equals("")) {
				out.print(" enctype=\"");
				out.print(m_strEncType);
				out.print("\"");
			}

			out.println(">");
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}
		return (EVAL_BODY_BUFFERED);
	}

	public int doAfterBody() {
		try {
			ReqInfo ri = ContextUtils.getReqInfo();

			String name = m_strName + ri.getWinId() + ri.getViewId();

			JspWriter out = getPreviousOut();

			bodyContent.writeOut(out);
			out.println("</form>");

			out.println("<script language=\"javascript\" defer>");
			if (m_htScripts != null) {
				// 输出由FunctionTag生成的脚本
				for (Iterator<String> it = m_htScripts.values().iterator(); it
						.hasNext();)
					out.println(it.next());
			}

			StringBuffer sb = new StringBuffer();
			sb.append("iid: '" + ri.getWinId() + "', vid: '" + ri.getViewId()
					+ "'");
			if (m_strFocus != null)
				sb.append(", focus: '" + m_strFocus + "'");
			if (m_strUpdate != null)
				sb.append(", update: '" + m_strUpdate + "'");
			if (m_bValidate)
				sb.append(", validate: 'yes', formid: '" + m_form.getId() + "'");
			if (m_htFormScripts != null) {
				boolean bFirst = true;

				sb.append(", func :[");
				for (String func : m_htFormScripts.values()) {
					if (bFirst)
						bFirst = false;
					else
						sb.append(", ");
					sb.append(func);
				}
				sb.append("]");
			}

			out.print("$(function() {$(\"#");
			out.print(name);
			out.print("\").winform({" + sb.toString() + "});});");

			out.print("</script>");
		} catch (IOException e) {
			e.printStackTrace();
			return SKIP_BODY;
		}

		bodyContent.clearBody();
		return SKIP_BODY;
	}
}