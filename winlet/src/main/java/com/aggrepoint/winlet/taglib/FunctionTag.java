package com.aggrepoint.winlet.taglib;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ReqInfo;

/**
 * 用于生成可支持Ajax页面替换的Javascript函数名称 函数名称必须由"document."开头
 * 
 * @author YJM
 */
public class FunctionTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;

	/** 用于声明函数时指定函数的名称 */
	String m_strName;

	/** 用于引用函数时指定函数的名称 */
	String m_strRef;

	/** 用于在<win:form>中自动生成设置form字段值的脚本：脚本参数对应的表单字段名 */
	Vector<String> m_vecParams;

	/** 用于在<win:form>中自动生成设置form字段值的脚本：设置完毕是否提交表单 */
	boolean m_bSubmit = true;

	public void setName(String name) {
		m_strName = name;
	}

	public void setRef(String ref) {
		m_strRef = ref;
	}

	public void setParam(String param) {
		if (param != null) {
			StringTokenizer st = new StringTokenizer(param, " ,");
			m_vecParams = new Vector<String>();
			while (st.hasMoreTokens()) {
				m_vecParams.add(st.nextToken());
			}
		}
	}

	public void setSubmit(String submit) {
		if (submit != null
				&& (submit.equalsIgnoreCase("n") || submit
						.equalsIgnoreCase("no")))
			m_bSubmit = false;
		else
			m_bSubmit = true;
	}

	/**
	 * 生成跟随在FormTag中的脚本
	 * 
	 * @param reqInfo
	 * @param winiid
	 * @param formName
	 * @param body
	 * @return
	 */
	void genScript(FormTag form, String name, ReqInfo reqInfo, String winiid,
			String formName, String body) {
		StringBuffer sb = new StringBuffer();

		if (body == null && reqInfo.m_bUseAjax) {
			// 在Ajax模式下而且内容为空，使用 winform生成函数
			sb.append("{name: '").append(name).append("', params: [");
			if (m_vecParams != null) {
				boolean bFirst = true;
				for (String param : m_vecParams) {
					if (bFirst)
						bFirst = false;
					else
						sb.append(", ");

					sb.append("'").append(param).append("'");
				}
			}
			sb.append("]");
			if (m_bSubmit)
				sb.append(", submit:true");
			sb.append("}");

			form.setFormScript(name, sb.toString());
		} else {
			if (!reqInfo.m_bUseAjax) { // 非Ajax模式
				sb.append("function ").append(m_strName).append(winiid);
				sb.append(reqInfo.getViewId());
			} else { // Ajax模式
				sb.append("document.").append(m_strName).append(winiid);
				sb.append(reqInfo.getViewId());
				sb.append(" = function");
			}

			sb.append("(");
			if (m_vecParams != null)
				for (int i = m_vecParams.size(); i > 0; i--) {
					sb.append("p_").append(Integer.toString(i));

					if (i > 1)
						sb.append(", ");
				}
			sb.append(") {\r\n");

			sb.append("var form = document.").append(formName).append(winiid);
			sb.append(reqInfo.getViewId());
			sb.append(";\r\n");

			if (m_vecParams != null) {
				int i = m_vecParams.size();
				for (Enumeration<String> enu = m_vecParams.elements(); enu
						.hasMoreElements(); i--)
					sb.append("form.").append(enu.nextElement())
							.append(".value = ").append("p_")
							.append(Integer.toString(i)).append(";\r\n");
			}

			if (body != null)
				sb.append(body);

			if (m_bSubmit)
				sb.append("$(form).submit();\r\n");

			sb.append("}");

			form.setScript(name, sb.toString());
		}
	}

	public int doStartTag() throws JspException {
		try {
			if (m_strName == null && m_strRef == null)
				throw new JspException("必须指定name或者ref属性。");

			ReqInfo reqInfo = ContextUtils.getReqInfo();
			if (reqInfo == null)
				return (SKIP_BODY);

			String winiid = reqInfo.getWinId();

			if (m_strName != null) {
				FormTag form = (FormTag) TagSupport.findAncestorWithClass(this,
						FormTag.class);
				if (form != null) {
					// 先生成不包含BODY的版本
					// 以<win:func name="xxx"/>方式使用时，doAfterBody()不会被调用
					// 因此在这里先生成一次
					genScript(form, m_strName, reqInfo, winiid, form.getName(),
							null);
					return (EVAL_BODY_BUFFERED);
				}
			}

			// 输出脚本的声明或引用
			JspWriter out = pageContext.getOut();

			if (!reqInfo.m_bUseAjax) { // 非Ajax模式
				if (m_strName != null) {
					out.print("function ");
					out.print(m_strName);
				} else
					out.print(m_strRef);

				out.print(winiid);
				out.print(reqInfo.getViewId());
			} else { // Ajax模式
				out.print("document.");
				if (m_strName != null)
					out.print(m_strName);
				else
					out.print(m_strRef);

				out.print(winiid);
				out.print(reqInfo.getViewId());

				if (m_strName != null)
					out.print(" = function");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return (SKIP_BODY);
	}

	public int doAfterBody() {
		try {
			ReqInfo reqInfo = ContextUtils.getReqInfo();
			if (reqInfo == null)
				return SKIP_BODY;

			String winiid = reqInfo.getWinId();

			// 生成整个脚本
			FormTag form = (FormTag) TagSupport.findAncestorWithClass(this,
					FormTag.class);
			if (form != null) {
				StringWriter sw = new StringWriter();
				bodyContent.writeOut(sw);
				genScript(form, m_strName, reqInfo, winiid, form.getName(),
						sw.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
			return SKIP_BODY;
		}

		bodyContent.clearBody();
		return SKIP_BODY;
	}
}
