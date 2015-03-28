package com.aggrepoint.winlet.taglib;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.IncludeResult;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.WinletConst;
import com.aggrepoint.winlet.spring.WinletClassLoader;
import com.aggrepoint.winlet.spring.def.WinletDef;
import com.aggrepoint.winlet.utils.TypeCast;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class IncludeTag extends BodyTagSupport implements WinletConst {
	private static final long serialVersionUID = 1L;

	String var;

	String vars;

	String m_strWindow;

	String m_strUniqueId;

	WinletDef winletDef;

	Hashtable<String, String> m_params;

	public void setVar(String var) {
		this.var = var;
	}

	public void setVars(String var) {
		this.vars = var;
	}

	public void setWindow(String window) {
		m_strWindow = window;
	}

	public void setUniqueId(String id) {
		m_strUniqueId = id;
	}

	public void setWinlet(String winlet) {
		Class<?> clz = WinletClassLoader.getWinletClassByPath(winlet);
		this.winletDef = WinletDef.getDef(clz);
	}

	@Override
	public int doStartTag() {
		m_params = new Hashtable<String, String>();
		return EVAL_BODY_BUFFERED;
	}

	@Override
	public int doAfterBody() {
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspTagException {
		try {
			Vector<String> v = null;
			if (vars != null) {
				v = TypeCast.cast(pageContext.getAttribute(vars));
				if (v == null) {
					v = new Vector<String>();
					pageContext.setAttribute(vars, v);
				}
			}

			StringBuffer all = new StringBuffer();

			for (String win : m_strWindow.split(", ")) {
				ReqInfo ri = ContextUtils.getReqInfo();
				IncludeResult result = ri.include(winletDef, win, m_params,
						m_strUniqueId);

				StringBuffer sb = new StringBuffer();
				sb.append("<div class=\"ap_child_window\" id=\"ap_win_")
						.append(result.getChildWindow().getId()).append("\">");
				sb.append(result.getResponse());
				sb.append("</div>");

				if (v != null)
					v.add(sb.toString());
				else
					all.append(sb);
			}

			if (v == null)
				if (var != null)
					pageContext.setAttribute(var, all.toString());
				else
					getPreviousOut().write(all.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspTagException(e.getMessage());
		}

		return EVAL_PAGE;
	}
}
