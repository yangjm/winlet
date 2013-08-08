package com.aggrepoint.winlet;

import java.lang.reflect.Method;
import java.util.Hashtable;

/**
 * 实现可以在EL中通过win.访问的工具方法
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletEl {
	static final Class<?>[] MODULE_METHOD_PARAM_TYPE = { String.class };
	static Hashtable<String, Method> m_htMethods = new Hashtable<String, Method>();

	String m_strMethod;

	public void setMethod(String method) {
		m_strMethod = method;
	}

	public String getMethod() {
		return m_strMethod;
	}

	public Object execute(String param) {
		try {
			Method method = m_htMethods.get(m_strMethod);
			if (method == null) {
				method = this.getClass().getMethod(m_strMethod,
						MODULE_METHOD_PARAM_TYPE);
				m_htMethods.put(m_strMethod, method);
			}
			return method.invoke(this, new Object[] { param });
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 调用函数<br>
	 * 不支持函数定义，函数定义必须使用TagLib
	 * 
	 * @param param
	 * @return
	 */
	public Object func(String param) {
		ReqInfo reqInfo = ContextUtils.getReqInfo();

		StringBuffer sb = new StringBuffer();
		// if (reqInfo.m_bUseAjax)
		// sb.append("document.");
		sb.append(param);
		sb.append(reqInfo.getWinId());
		return sb.toString();
	}
}
