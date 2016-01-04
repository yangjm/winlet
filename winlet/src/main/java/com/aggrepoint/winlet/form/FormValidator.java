package com.aggrepoint.winlet.form;

import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.utils.StringUtils;
import com.aggrepoint.winlet.spring.WinletExceptionResolver;
import com.aggrepoint.winlet.utils.BeanProperty;
import com.aggrepoint.winlet.utils.PropertyTypeCode;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class FormValidator implements PropertyTypeCode {
	/** 日志 */
	static final Log logger = LogFactory.getLog(WinletExceptionResolver.class);

	static final Class<?>[] PARAMS = new Class<?>[] { HttpServletRequest.class,
			Form.class };

	Object controller;
	String strName;
	Pattern pattern;
	String strId;
	String strMethod;
	ValidateResultType passSkip;
	ValidateResultType failSkip;
	String strErrorMsg;
	Vector<String> vecArgs;
	Method method;
	boolean methodWithParam;
	Object methodObject;

	public FormValidator(Object c, String n, String p, String id,
			String method, String ps, String fs, String error,
			Vector<String> args) {
		controller = c;
		strName = n;
		if ("".equals(strName))
			pattern = Pattern.compile(p);
		strId = id;
		if (strId != null)
			strId = strId.trim();
		strMethod = method;
		if ("property".equalsIgnoreCase(ps)
				|| ValidateResultType.PASS_SKIP_PROPERTY.name().equals(ps))
			passSkip = ValidateResultType.PASS_SKIP_PROPERTY;
		else if ("all".equalsIgnoreCase(ps)
				|| ValidateResultType.PASS_SKIP_ALL.name().equals(ps))
			passSkip = ValidateResultType.PASS_SKIP_ALL;
		else
			passSkip = ValidateResultType.PASS_CONTINUE;
		if ("continue".equalsIgnoreCase(fs)
				|| ValidateResultType.FAILED_CONTINUE.name().equals(fs))
			failSkip = ValidateResultType.FAILED_CONTINUE;
		else if ("all".equalsIgnoreCase(fs)
				|| ValidateResultType.FAILED_SKIP_ALL.name().equals(fs))
			failSkip = ValidateResultType.FAILED_SKIP_ALL;
		else
			failSkip = ValidateResultType.FAILED_SKIP_PROPERTY;
		strErrorMsg = error;
		if (strErrorMsg == null)
			strErrorMsg = "";
		vecArgs = args;

		String m = strMethod;

		if (strMethod != null && !strMethod.equals("")) {
			try {
				int idx = strMethod.lastIndexOf(".");
				if (idx == -1) {
					methodObject = controller;
				} else {
					String name = strMethod.substring(0, idx);
					m = strMethod.substring(idx + 1);

					if (name.indexOf(".") > 0)
						methodObject = PropertyUtils.getNestedProperty(
								controller, name);
					else
						methodObject = PropertyUtils.getSimpleProperty(
								controller, name);
				}

				try {
					this.method = methodObject.getClass().getMethod(m, PARAMS);
					methodWithParam = true;
				} catch (NoSuchMethodException e) {
					this.method = methodObject.getClass().getMethod(m);
					methodWithParam = false;
				}
			} catch (Exception e) {
				logger.error("Unable to find validation method \"" + strMethod
						+ "\" in winlet \"" + controller.getClass().getName()
						+ "\".", e);
			}
		}
	}

	public boolean matches(String field) {
		if (pattern != null)
			return pattern.matcher(field).find();
		return strName.equals(field);
	}

	static Hashtable<String, Pattern> PATTERNS = new Hashtable<String, Pattern>();
	static Pattern EMAIL = Pattern.compile(
			"^[A-Z0-9._%-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$",
			Pattern.CASE_INSENSITIVE);
	static Pattern INT = Pattern.compile("^\\d+$");
	static Pattern FLOAT = Pattern.compile("^\\d+(\\.\\d+)?$");

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

	public ValidateResult validate(HttpServletRequest req, Form data,
			Object value) {
		if (strId != null && !strId.equals("")) {
			try {
				if (strId.equalsIgnoreCase("ne")) {
					if (value == null || value.toString().equals(""))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("tne")) {
					if (value == null || value.toString().trim().equals(""))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("eq")) {
					if (value == null && !vecArgs.elementAt(0).equals(""))
						return new ValidateResult(failSkip, strErrorMsg);

					if (value != null
							&& !vecArgs.elementAt(0).equals(value.toString()))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("neq")) {
					if (value != null
							&& vecArgs.elementAt(0).equals(value.toString()))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("teq")) {
					if (value == null && !vecArgs.elementAt(0).equals(""))
						return new ValidateResult(failSkip, strErrorMsg);

					if (value != null
							&& !vecArgs.elementAt(0).equals(
									value.toString().trim()))
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
				} else if (strId.equalsIgnoreCase("int")) { // 整数
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					if (!INT.matcher(value.toString().replaceAll(",", ""))
							.find())
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("float")) { // 浮点数
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					// 接受,在数字中
					if (!FLOAT.matcher(value.toString().replaceAll(",", ""))
							.find())
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase(">")
						|| strId.equalsIgnoreCase("gt")) { // 大于
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					boolean noValue[] = new boolean[1];
					boolean error = false;
					Double val = 0d;
					try {
						val = (Double) BeanProperty.convert(value, DOUBLE,
								noValue);
					} catch (Exception e) {
						error = true;
					}
					if (error || noValue[0]
							|| val <= Double.parseDouble(vecArgs.elementAt(0)))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase(">=")
						|| strId.equalsIgnoreCase("gteq")) { // 大于等于
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					boolean noValue[] = new boolean[1];
					boolean error = false;
					Double val = 0d;
					try {
						val = (Double) BeanProperty.convert(value, DOUBLE,
								noValue);
					} catch (Exception e) {
						error = true;
					}
					if (error || noValue[0]
							|| val < Double.parseDouble(vecArgs.elementAt(0)))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("<")
						|| strId.equalsIgnoreCase("lt")) { // 小于
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					boolean noValue[] = new boolean[1];
					boolean error = false;
					Double val = 0d;
					try {
						val = (Double) BeanProperty.convert(value, DOUBLE,
								noValue);
					} catch (Exception e) {
						error = true;
					}
					if (error || noValue[0]
							|| val >= Double.parseDouble(vecArgs.elementAt(0)))
						return new ValidateResult(failSkip, strErrorMsg);
				} else if (strId.equalsIgnoreCase("<=")
						|| strId.equalsIgnoreCase("lteq")) { // 小于等于
					if (value == null)
						return new ValidateResult(failSkip, strErrorMsg);

					boolean noValue[] = new boolean[1];
					boolean error = false;
					Double val = 0d;
					try {
						val = (Double) BeanProperty.convert(value, DOUBLE,
								noValue);
					} catch (Exception e) {
						error = true;
					}
					if (error || noValue[0]
							|| val > Double.parseDouble(vecArgs.elementAt(0)))
						return new ValidateResult(failSkip, strErrorMsg);
				} else {
					logger.error("Unsupported validation id \"" + strId + "\".");
					return new ValidateResult(failSkip, strErrorMsg);
				}
			} catch (Exception e) {
				logger.error("Error running validation id \"" + strId
						+ "\" in winlet \"" + controller.getClass().getName()
						+ "\".", e);
				return new ValidateResult(failSkip, strErrorMsg);
			}
		} else if (method != null) {
			try {
				if (methodWithParam)
					return (ValidateResult) method.invoke(methodObject, req,
							data);
				else
					return (ValidateResult) method.invoke(methodObject);
			} catch (Exception e) {
				logger.error("Error running validation method \"" + strMethod
						+ "\" in winlet \"" + controller.getClass().getName()
						+ "\".", e);
				return new ValidateResult(failSkip, strErrorMsg);
			}
		}

		return new ValidateResult(passSkip);
	}
}
