package com.aggrepoint.winlet.taglib;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.codehaus.jackson.map.ObjectMapper;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.WinletConst;
import com.aggrepoint.winlet.WinletManager;
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
				String cwid = WinletManager.getChildWindowId(ri.getWindowId(),
						ri.getRequest());
				String windowUrl = ri.getWindowUrl(winletDef, win);
				String response = ri.getWindowContent(cwid, windowUrl,
						m_params, null);

				StringBuffer sb = new StringBuffer();
				sb.append("<div class=\"ap_child_window\" data-winlet-id=\"")
						.append(cwid).append("\" data-winlet-url=\"")
						.append(ri.getRequest().getContextPath())
						.append(windowUrl).append("\"");
				if (m_params.size() > 0)
					sb.append(" data-winlet-params=\"")
							.append(new ObjectMapper().writeValueAsString(
									m_params).replaceAll("\"", "&quot;"))
							.append("\"");
				sb.append(">");
				sb.append(response);
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
					pageContext.getOut().write(all.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspTagException(e.getMessage());
		}

		return EVAL_PAGE;
	}
}
