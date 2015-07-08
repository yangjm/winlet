package com.aggrepoint.winlet.taglib;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.DynamicAttributes;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ReqConst;
import com.aggrepoint.winlet.ReqInfo;

/**
 * Form
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class FormTag extends BodyTagSupport implements DynamicAttributes {
	private static final long serialVersionUID = 1L;

	HashMap<String, String> attributes = new HashMap<String, String>();

	protected String m_strName;

	protected String m_strAction = "";

	protected String m_strMethod;

	protected String m_strFocus;

	protected String m_strValidate;

	protected Object m_objResetRef;

	protected boolean m_bHideLoading;

	protected String m_strUpdate;

	public void setName(String name) {
		m_strName = name;
	}

	public void setAction(String action) {
		m_strAction = action;
	}

	public void setMethod(String str) {
		m_strMethod = str;
	}

	public void setFocus(String focus) {
		m_strFocus = focus;
	}

	public String getName() {
		return m_strName;
	}

	public void setValidate(String val) {
		m_strValidate = val;
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

	public int doStartTag() throws JspException {
		ReqInfo ri = ContextUtils.getReqInfo();

		try {
			JspWriter out = pageContext.getOut();

			out.print("<form");

			// Name
			if (m_strName != null) {
				out.print(" name=\"");
				out.print(m_strName);
				out.print("\" id=\"");
				out.print(m_strName);
				out.print(ri.getRequestId());
				out.print("\"");
			}

			out.print(" action=\"");
			out.print(ri.getPath());
			out.print("?");
			out.print(ReqConst.PARAM_WIN_ACTION);
			out.print("=");
			out.print(m_strAction);
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

			// wid
			out.print(" data-winlet-form=\"yes\"");

			// focus
			if (m_strFocus != null) {
				out.print(" data-winlet-focus=\"");
				out.print(m_strFocus);
				out.print("\"");
			}

			// update
			if (m_strUpdate != null) {
				out.print(" data-winlet-update=\"");
				out.print(m_strUpdate);
				out.print("\"");
			}

			// validate
			if (m_strValidate != null && !m_strValidate.trim().equals(""))
				out.print(" data-winlet-validate=\"" + m_strValidate + "\"");

			// hide loading
			if (m_bHideLoading)
				out.print(" data-winlet-hideloading=\"yes\"");

			for (String key : attributes.keySet())
				out.print(" " + key + "=\"" + attributes.get(key) + "\"");

			out.println(">");
		} catch (Exception e) {
			throw new JspException(e.getMessage());
		}

		attributes = new HashMap<String, String>();
		return (EVAL_BODY_BUFFERED);
	}

	public int doAfterBody() {
		try {
			JspWriter out = getPreviousOut();
			bodyContent.writeOut(out);
			out.println("</form>");
		} catch (IOException e) {
			e.printStackTrace();
			return SKIP_BODY;
		}

		bodyContent.clearBody();
		return SKIP_BODY;
	}

	@Override
	public void setDynamicAttribute(String uri, String localName, Object value)
			throws JspException {
		attributes.put(localName, value.toString());
	}
}