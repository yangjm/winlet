package com.aggrepoint.winlet;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;

import com.aggrepoint.utils.TypeCast;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 实现可以在EL中通过win.访问的工具方法
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletEl {
	private static final Logger logger = LoggerFactory.getLogger(WinletEl.class);

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
				method = this.getClass().getMethod(m_strMethod, MODULE_METHOD_PARAM_TYPE);
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
		sb.append(reqInfo.getRequestId());
		return sb.toString();
	}

	Map<String, String> webPackResMap = null;

	public String webpackRes(String name) {
		if (webPackResMap == null) {
			webPackResMap = new HashMap<>();
			try {
				InputStream is = new ClassPathResource("webpack.manifest.json").getInputStream();
				ObjectMapper mapper = new ObjectMapper();
				Map<String, String> map = TypeCast.cast(mapper.readValue(is, Map.class));

				Function<String, String> removePath = (str) -> {
					int idx = str.lastIndexOf("/");
					if (idx > 0)
						return str.substring(idx + 1);
					return str;
				};
				map.forEach((key, value) -> {
					webPackResMap.put(removePath.apply(key), removePath.apply(value));
				});
			} catch (Exception e) {
				logger.error("webpack.manifest.json not found", e);
			}
		}

		if (webPackResMap.containsKey(name))
			return webPackResMap.get(name);
		return name;
	}
}
