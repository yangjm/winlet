package com.aggrepoint.winlet.taglib;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.Resolver;
import com.aggrepoint.winlet.UrlConstructor;
import com.aggrepoint.winlet.utils.BeanProperty;
import com.aggrepoint.winlet.utils.EncodeUtils;
import com.aggrepoint.winlet.utils.StringUtils;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ELFunction {
	static Hashtable<String, SimpleDateFormat> m_htSDFs = new Hashtable<String, SimpleDateFormat>();
	static Hashtable<String, DecimalFormat> m_htDFs = new Hashtable<String, DecimalFormat>();
	static Hashtable<String, BeanProperty> m_htProperties = new Hashtable<String, BeanProperty>();

	static final Log logger = LogFactory.getLog(ELFunction.class);

	static String strBOM;
	static {
		try {
			strBOM = new String(new byte[] { (byte) 0xef, (byte) 0xbb,
					(byte) 0xbf }, "UTF-8");
		} catch (Exception e) {
		}
	}

	public static String bom() {
		return strBOM;
	}

	public static String htmlEncode(String str) {
		try {
			return EncodeUtils.html(str);
		} catch (Exception e) {
			return str;
		}
	}

	public static String textAreaEncode(String str) {
		try {
			return StringUtils.toHTML(str, true);
		} catch (Exception e) {
			return str;
		}
	}

	public static String wmlEncode(String str) {
		try {
			return StringUtils.toWML(str);
		} catch (Exception e) {
			return str;
		}
	}

	public static String urlEncode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (Exception e) {
			return str;
		}
	}

	public static String jsonEncode(String str) {
		try {
			return StringUtils.toJson(str);
		} catch (Exception e) {
			return str;
		}
	}

	public static Object funcReqAttr(String name) {
		return ContextUtils.getRequest().getAttribute(name);
	}

	public static String funcMarkup() {
		return MarkupTag.getMarkupName(ContextUtils.getRequest());
	}

	public static String funcEncodeNameSpace(String text) throws IOException {
		if (text == null)
			return null;

		ReqInfo ri = ContextUtils.getReqInfo();

		if (ri != null)
			text = text + ri.getRequestId();
		return text;
	}

	public static String funcIf(Boolean b, String s) {
		if (b != null && b)
			return s;
		return "";
	}

	public static String funcIfElse(Boolean b, String s, String e) {
		if (b != null && b)
			return s;
		return e;
	}

	public static String funcDateFormat(String fmt, Date date) {
		if (fmt == null || date == null)
			return "";

		Locale locale = ContextUtils.getRequest().getLocale();

		SimpleDateFormat sdf = m_htSDFs.get(fmt + "_" + locale == null ? ""
				: locale.toString());
		if (sdf == null) {
			sdf = new SimpleDateFormat(fmt, locale);
			m_htSDFs.put(fmt, sdf);
		}

		return sdf.format(date);
	}

	public static String funcDecimalFormat(String fmt, java.lang.Double val) {
		if (fmt == null)
			return "";

		DecimalFormat df = m_htDFs.get(fmt);
		if (df == null) {
			df = new DecimalFormat(fmt);
			m_htDFs.put(fmt, df);
		}

		return df.format(new BigDecimal(val.doubleValue()).setScale(5,
				RoundingMode.HALF_EVEN));
	}

	public static String funcPercent(Double val, Double compTo) {
		if (compTo == 0.0d)
			return "";

		return funcDecimalFormat("#,###,###,###,##0.00", val / compTo * 100.0);
	}

	public static String funcChangePercent(Double newval, Double oldval) {
		if (oldval == 0.0d)
			return "";

		double percent = new BigDecimal((newval - oldval) / Math.abs(oldval)
				* 100.0).setScale(2, RoundingMode.HALF_EVEN).doubleValue();
		if (percent > 0.0d)
			return "+" + funcDecimalFormat("#,###,###,###,##0.00", percent);
		else
			return funcDecimalFormat("#,###,###,###,##0.00", percent);
	}

	public static Object funcGet(Object o, String prop) throws Exception {
		String key = o.getClass().getName() + "_" + prop;
		BeanProperty bp = m_htProperties.get(key);
		if (bp == null) {
			bp = new BeanProperty(o.getClass(), prop, true, false, null);
			m_htProperties.put(key, bp);
		}
		return bp.get(o);
	}

	public static Object funcSet(Object o, String prop, Object val)
			throws Exception {
		String key = o.getClass().getName() + "_" + prop;
		BeanProperty bp = m_htProperties.get(key);
		if (bp == null) {
			bp = new BeanProperty(o.getClass(), prop, true, false, null);
			m_htProperties.put(key, bp);
		}
		bp.set(o, val);
		return o;
	}

	public static Object funcListGet(List<?> o, Integer i) {
		return o.get(i);
	}

	private static Method findMethod(Class<?> cls, String name,
			Class<?>... params) {
		for (Method method : cls.getMethods()) {
			if (!method.getName().equals(name))
				continue;
			Class<?>[] paramTypes = method.getParameterTypes();
			if (paramTypes.length != params.length)
				continue;
			int i = 0;
			for (Class<?> c : paramTypes) {
				if (!c.equals(params[i]) && !c.isAssignableFrom(params[i]))
					break;
				i++;
			}
			if (i >= params.length)
				return method;
		}
		return null;
	}

	public static Object funcExec(Object o, String method) throws Exception {
		return o.getClass().getMethod(method).invoke(o);
	}

	public static Object funcExec1(Object o, String method, Object param)
			throws Exception {
		try {
			return findMethod(o.getClass(), method, param.getClass()).invoke(o,
					param);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static Object funcExec2(Object o, String method, Object param1,
			Object param2) throws Exception {
		return findMethod(o.getClass(), method, param1.getClass(),
				param2.getClass()).invoke(o, param1, param2);
	}

	public static Object funcExec3(Object o, String method, Object param1,
			Object param2, Object param3) throws Exception {
		return findMethod(o.getClass(), method, param1.getClass(),
				param2.getClass(), param3.getClass()).invoke(o, param1, param2,
				param3);
	}

	public static Object funcExec4(Object o, String method, Object param1,
			Object param2, Object param3, Object param4) throws Exception {
		return findMethod(o.getClass(), method, param1.getClass(),
				param2.getClass(), param3.getClass(), param4.getClass())
				.invoke(o, param1, param2, param3, param4);
	}

	public static Object funcExec5(Object o, String method, Object param1,
			Object param2, Object param3, Object param4, Object param5)
			throws Exception {
		return findMethod(o.getClass(), method, param1.getClass(),
				param2.getClass(), param3.getClass(), param4.getClass(),
				param5.getClass()).invoke(o, param1, param2, param3, param4,
				param5);
	}

	public static Object w(String property) throws Exception {
		Object winlet = null;

		try {
			winlet = ContextUtils.getReqInfo().getWinlet();
		} catch (Exception e) {
		}

		if (winlet != null)
			return Resolver.getObjectValue(winlet, property);

		return null;
	}

	public static Boolean access(String rule) throws Exception {
		try {
			return ContextUtils.getAccessRuleEngine(ContextUtils.getRequest())
					.eval(rule);
		} catch (Exception e) {
			logger.error("Error evaluating access rule defined in JSP: \""
					+ rule + "\"", e);
		}

		return false;
	}

	public static Boolean psn(String rule) throws Exception {
		try {
			return ContextUtils.getPsnRuleEngine(ContextUtils.getRequest())
					.eval(rule);
		} catch (Exception e) {
			logger.error(
					"Error evaluating personalization rule defined in JSP: \""
							+ rule + "\"", e);
		}

		return false;
	}

	public static String cfg(String name) {
		return ContextUtils.getConfigProvider(ContextUtils.getRequest())
				.getStr(name);
	}

	public static String cfgdef(String name, String def) {
		String cfg = ContextUtils.getConfigProvider(ContextUtils.getRequest())
				.getStr(name);
		return cfg == null ? def : cfg;
	}

	public static Boolean contains(Object col, Object o) {
		if (col == null || o == null)
			return false;

		if (col instanceof Collection)
			return ((Collection<?>) col).contains(o);
		else if (col.getClass().isArray()) {
			for (int i = 0; i < Array.getLength(col); i++) {
				Object val = Array.get(col, i);

				if (val instanceof Number && o instanceof Number)
					if (((Number) val).doubleValue() == ((Number) o)
							.doubleValue())
						return true;

				if (o.equals(Array.get(col, i)))
					return true;
			}
		}

		return false;
	}

	// ///////////////////////////////////////////////////////
	//
	// 以下部分是Winlet专用
	//
	// ///////////////////////////////////////////////////////

	/**
	 * 生成函数定义<br>
	 * 
	 * @param name
	 * @return
	 */
	public static String funcdef(String name) {
		ReqInfo reqInfo = ContextUtils.getReqInfo();

		StringBuffer sb = new StringBuffer();
		sb.append("document.");
		sb.append(name);
		sb.append(reqInfo.getRequestId());
		sb.append(" = function");

		return sb.toString();
	}

	/**
	 * 生成函数调用<br>
	 * 
	 * @param name
	 * @return
	 */
	public static String func(String name) {
		ReqInfo reqInfo = ContextUtils.getReqInfo();

		StringBuffer sb = new StringBuffer();
		sb.append("document.");
		sb.append(name);
		sb.append(reqInfo.getRequestId());

		return sb.toString();
	}

	public static String funcResurl(String param) {
		HttpServletRequest req = ContextUtils.getRequest();

		if (param.startsWith("/"))
			return req.getContextPath() + param;
		else
			return req.getContextPath() + "/" + param;
	}

	public static String funcPageUrl(String param) {
		ReqInfo reqInfo = ContextUtils.getReqInfo();
		return new UrlConstructor(reqInfo.getRequest()).getPageUrl(param);
	}

	public static int funcToInt(Object val) {
		if (val instanceof Double)
			return ((Double) val).intValue();
		if (val instanceof Float)
			return ((Float) val).intValue();
		if (val instanceof Long)
			return ((Long) val).intValue();
		if (val instanceof Integer)
			return ((Integer) val).intValue();
		if (val instanceof Short)
			return ((Short) val).intValue();
		return 0;
	}

	// public static String resurl(String param, boolean isStatic) {
	// return ResourceUrlTag.getUrl(WinletReqInfo
	// .getInfo((HttpServletRequest) ThreadContext
	// .getAttribute(THREAD_ATTR_REQUEST)), param, null, null,
	// isStatic);
	// }
	//
	// public static String resurl(String param) {
	// return resurl(param, true);
	// }
	//
	// public static String actionurl(String param) {
	// return ActionUrlTag.getUrl(WinletReqInfo
	// .getInfo((HttpServletRequest) ThreadContext
	// .getAttribute(THREAD_ATTR_REQUEST)), param);
	// }
	//
	// public static String resproxy(String res) throws Exception {
	// return ResProxyTag.getUrl(WinletReqInfo
	// .getInfo((HttpServletRequest) ThreadContext
	// .getAttribute(THREAD_ATTR_REQUEST)), res);
	// }
}
