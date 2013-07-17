package com.aggrepoint.winlet.form;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;

import com.icebean.core.beanutil.BeanProperty;
import com.icebean.core.beanutil.PropertyTypeCode;
import com.icebean.core.common.StringUtils;

public class Validator implements PropertyTypeCode {
	/** 日志 */
	static org.apache.log4j.Category m_log = com.icebean.core.common.Log4jIniter
			.getCategory();

	static final Class<?>[] PARAMS = new Class<?>[] { HttpServletRequest.class,
			Input.class };

	InputImpl input;
	String strId;
	String strMethod;
	ValidateResultType passSkip;
	ValidateResultType failSkip;
	String strErrorMsg;
	Vector<String> vecArgs;
	Method method;
	boolean methodWithParam;
	Object methodObject;

	public Validator(InputImpl input, String id, String method,
			String passskip, String failskip, String error, Vector<String> args) {
		this.input = input;
		strId = id;
		if (strId != null)
			strId = strId.trim();
		strMethod = method;
		if ("property".equalsIgnoreCase(passskip)
				|| ValidateResultType.PASS_SKIP_PROPERTY.name()
						.equals(passSkip))
			passSkip = ValidateResultType.PASS_SKIP_PROPERTY;
		else if ("all".equalsIgnoreCase(passskip)
				|| ValidateResultType.PASS_SKIP_ALL.name().equals(passSkip))
			passSkip = ValidateResultType.PASS_SKIP_ALL;
		else
			passSkip = ValidateResultType.PASS_CONTINUE;
		if ("continue".equalsIgnoreCase(failskip)
				|| ValidateResultType.FAILED_CONTINUE.name().equals(failSkip))
			failSkip = ValidateResultType.FAILED_CONTINUE;
		else if ("all".equalsIgnoreCase(failskip)
				|| ValidateResultType.FAILED_SKIP_ALL.name().equals(failSkip))
			failSkip = ValidateResultType.FAILED_SKIP_ALL;
		else
			failSkip = ValidateResultType.FAILED_SKIP_PROPERTY;
		strErrorMsg = error;
		if (strErrorMsg == null || strErrorMsg.equals(""))
			strErrorMsg = input.strDefaultError;
		vecArgs = args;

		String m = strMethod;

		if (strMethod != null && !strMethod.equals("")) {
			try {
				int idx = strMethod.lastIndexOf(".");
				if (idx == -1) {
					methodObject = input.form.winlet;
				} else {
					String name = strMethod.substring(0, idx);
					m = strMethod.substring(idx + 1);

					if (name.indexOf(".") > 0)
						methodObject = PropertyUtils.getNestedProperty(
								input.form.winlet, name);
					else
						methodObject = PropertyUtils.getSimpleProperty(
								input.form.winlet, name);
				}

				try {
					this.method = methodObject.getClass().getMethod(m, PARAMS);
					methodWithParam = true;
				} catch (NoSuchMethodException e) {
					this.method = methodObject.getClass().getMethod(m);
					methodWithParam = false;
				}
			} catch (Exception e) {
				m_log.error("Unable to find validation method \"" + strMethod
						+ "\" in winlet \""
						+ input.form.winlet.getClass().getName() + "\".", e);
			}
		}
	}

	static Hashtable<String, Pattern> PATTERNS = new Hashtable<String, Pattern>();
	static Pattern EMAIL = Pattern.compile(
			"^[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$",
			Pattern.CASE_INSENSITIVE);

	static Pattern getPattern(Vector<String> args) {
		if (args == null || args.size() == 0)
			return null;

		String key = args.elementAt(0);
		if (args.size() > 1)
			key = key + "_" + args.elementAt(1);
		Pattern p = PATTERNS.get(key);
		if (p == null) {
			if (args.size() == 1)
				p = Pattern.compile(args.elementAt(0));
			else
				p = Pattern.compile(args.elementAt(0),
						Integer.parseInt(args.elementAt(1)));
			PATTERNS.put(key, p);
		}
		return p;
	}

	public ValidateResult validate(HttpServletRequest req, Object value) {
		if (strId != null && !strId.equals("")) {
			try {
				if (strId.equalsIgnoreCase("ne")) {
					if (value == null || value.toString().equals(""))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("tne")) {
					if (value == null || value.toString().trim().equals(""))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("maxlen")) { // 最大长度
					int len = 0;

					if (value != null)
						if (vecArgs.size() > 1)
							len = StringUtils.getDBLength(value.toString(),
									Integer.parseInt(vecArgs.elementAt(1)));
						else
							len = value.toString().length();

					if (len > Integer.parseInt(vecArgs.elementAt(0)))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("minlen")) { // 最小长度
					int len = 0;
					if (value != null)
						if (vecArgs.size() > 1)
							len = StringUtils.getDBLength(value.toString(),
									Integer.parseInt(vecArgs.elementAt(1)));
						else
							len = value.toString().length();

					if (len < Integer.parseInt(vecArgs.elementAt(0)))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("regexp")) { // 正则表达式
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					if (!getPattern(vecArgs).matcher(value.toString()).find())
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("email")) { // 电子邮件
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					if (!EMAIL.matcher(value.toString()).find())
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase(">")
						|| strId.equalsIgnoreCase("gt")) { // 大于
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					boolean noValue[] = new boolean[1];
					Double val = (Double) BeanProperty.convert(value, DOUBLE,
							noValue);
					if (noValue[0]
							|| val <= Double.parseDouble(vecArgs.elementAt(0)))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase(">=")
						|| strId.equalsIgnoreCase("gteq")) { // 大于等于
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					boolean noValue[] = new boolean[1];
					Double val = (Double) BeanProperty.convert(value, DOUBLE,
							noValue);
					if (noValue[0]
							|| val < Double.parseDouble(vecArgs.elementAt(0)))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("<")
						|| strId.equalsIgnoreCase("lt")) { // 小于
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					boolean noValue[] = new boolean[1];
					Double val = (Double) BeanProperty.convert(value, DOUBLE,
							noValue);
					if (noValue[0]
							|| val >= Double.parseDouble(vecArgs.elementAt(0)))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("<=")
						|| strId.equalsIgnoreCase("lteq")) { // 小于等于
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					boolean noValue[] = new boolean[1];
					Double val = (Double) BeanProperty.convert(value, DOUBLE,
							noValue);
					if (noValue[0]
							|| val > Double.parseDouble(vecArgs.elementAt(0)))
						return new ValidateResult(failSkip, strErrorMsg);
				} else {
					m_log.error("Unsupported validation id \"" + strId + "\".");
					return new ValidateResult(failSkip, strErrorMsg);
				}
			} catch (Exception e) {
				m_log.error("Error running validation id \"" + strId
						+ "\" in winlet \""
						+ input.form.winlet.getClass().getName() + "\".", e);
				return new ValidateResult(failSkip, strErrorMsg);
			}
		} else if (method != null) {
			try {
				if (methodWithParam)
					return (ValidateResult) method.invoke(methodObject, req,
							input);
				else
					return (ValidateResult) method.invoke(methodObject);
			} catch (Exception e) {
				m_log.error("Error running validation method \"" + strMethod
						+ "\" in winlet \""
						+ input.form.winlet.getClass().getName() + "\".", e);
				return new ValidateResult(failSkip, strErrorMsg);
			}
		}

		return new ValidateResult(passSkip);
	}
}
