package com.aggrepoint.winlet.form;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;

import com.aggrepoint.winlet.EnumMarkup;
import com.aggrepoint.winlet.taglib.MarkupTag;
import com.icebean.core.beanutil.BeanProperty;
import com.icebean.core.common.StringUtils;

/**
 * 表单里的输入项
 * 
 * @author Jim
 */
public abstract class InputImpl implements Input {
	static org.apache.log4j.Category m_log = com.icebean.core.common.Log4jIniter
			.getCategory();

	/** 所有Input公用的数字格式化类 */
	protected static Hashtable<String, DecimalFormat> m_htNumFormat = new Hashtable<String, DecimalFormat>();
	/** 所有Input公用的日期格式化类 */
	protected static Hashtable<String, SimpleDateFormat> m_htDateFormat = new Hashtable<String, SimpleDateFormat>();

	protected boolean bInited;
	protected String strType;
	/** 请求参数名称 */
	protected String strName;
	/** 参数值 */
	protected Object objValue;
	/** 属性 */
	protected String strProperty;
	/** 属性对象 */
	protected BeanProperty property;
	/** 是否嵌套属性 */
	protected boolean bNested;
	/** 缺省错误信息 */
	protected String strDefaultError = "";
	/** 引发错误的值 */
	protected String strErrorValue;
	/** 是否被禁用 */
	protected boolean bDisabled;
	/** 错误信息，没有错误为NULL */
	protected Vector<String> vecErrors;
	/** 指定的校验方法的名称。如果没有指定，则从Action的注解上取校验规则 */
	String validatorName;
	/** 校验方法 */
	Vector<Validator> vecValidators;
	/** 对应的Winlet */
	Object winlet;
	/** 属性所在对象 */
	Object propObject;
	/** 是否存在校验错误 */
	protected boolean bHasError;
	/** 对应表单 */
	FormImpl form;

	protected InputImpl() {
	}

	public void init(Object propObject, String prop, Object val,
			boolean disabled) {
		if (!bInited) {
			objValue = val;
			this.propObject = propObject;
			strProperty = prop;
			if (strProperty == null || strProperty.trim().equals(""))
				strProperty = null;
			else
				bNested = strProperty.indexOf(".") > 0;
			bDisabled = disabled;
			bInited = true;
		}
	}

	public void setAttr(String name, Object value) {
		if (name == null)
			return;

		if (name.equals("name"))
			strName = value.toString();
		else if (name.equals("value")) {
			if (value != null && !value.equals(""))
				objValue = value;
		}
	}

	@Override
	public String getType() {
		return strType;
	}

	@Override
	public String getName() {
		return strName;
	}

	public String getProperty() {
		return strProperty;
	}

	@Override
	public boolean isHasError() {
		return bHasError;
	}

	@Override
	public boolean isDisabled() {
		return bDisabled;
	}

	public void setDisabled(boolean disabled) {
		bDisabled = disabled;
	}

	@Override
	public String getErrorValue() {
		return strErrorValue;
	}

	public String getDefaultError() {
		return strDefaultError;
	}

	public void setDefaultError(String str) {
		strDefaultError = str == null ? "" : str;
	}

	@Override
	public void clearErrors() {
		vecErrors = null;
		bHasError = false;
	}

	@Override
	public Form getForm() {
		return form;
	}

	@Override
	public Vector<String> getErrors() {
		return vecErrors;
	}

	public void addError(String str) {
		if (vecErrors == null)
			vecErrors = new Vector<String>();
		vecErrors.add(str);
		bHasError = true;
		form.bHasError = true;

		form.recordChange(new ChangeUpdateValidateResult(strName, getError()));
	}

	@Override
	public String getError() {
		if (vecErrors == null)
			return "";

		StringBuffer sb = new StringBuffer();
		for (String s : vecErrors)
			sb.append(s);
		return sb.toString();
	}

	/**
	 * 在input上指定了校验方法
	 * 
	 * @param name
	 */
	public void setValidator(String name) {
		// 避免重复处理
		if (vecValidators != null)
			return;

		if (name != null && !name.trim().equals("")) {
			validatorName = name;
			addValidator(null, name, "", "", "", null);
		}
	}

	static HashMap<String, Pattern> PatternCache = new HashMap<String, Pattern>();

	/**
	 * 判断Validate与input的name是否匹配
	 * 
	 * @param v
	 * @param name
	 * @return
	 */
	static boolean isMatch(Validate v, String name) {
		if (!v.name().equals(""))
			return v.name().equalsIgnoreCase(name);

		if (!PatternCache.containsKey(v.pattern())) {
			Pattern p = null;
			try {
				p = Pattern.compile(v.pattern());
			} catch (Exception e) {
			}

			PatternCache.put(v.pattern(), p);
		}

		Pattern p = PatternCache.get(v.pattern());
		if (p == null)
			return false;

		synchronized (p) {
			if (p.matcher(name).find())
				return true;
		}

		return false;
	}

	public void bindAction(Method method) {
		// 若input上指定了校验方法，或者已经绑定过，则不取action注解的校验规则
		if (vecValidators != null)
			return;

		// 取action注解上定义的校验规则
		vecValidators = new Vector<Validator>();

		Validates ann = method.getAnnotation(Validates.class);
		if (ann == null)
			return;

		for (Validate vl : ann.value()) {
			if (!vl.after() && isMatch(vl, strName))
				addValidator(vl.id(), vl.method(), vl.passskip().name(), vl
						.failskip().name(), vl.error(), new Vector<String>(
						Arrays.asList(vl.args())));
		}
		for (Validate vl : ann.value()) {
			if (vl.after() && isMatch(vl, strName))
				addValidator(vl.id(), vl.method(), vl.passskip().name(), vl
						.failskip().name(), vl.error(), new Vector<String>(
						Arrays.asList(vl.args())));
		}
	}

	public void addValidator(String id, String method, String passskip,
			String failskip, String error, Vector<String> args) {
		if (vecValidators == null)
			vecValidators = new Vector<Validator>();

		Validator v = new Validator(this, id, method, passskip, failskip,
				error, args);

		if (v.method == null) {
			vecValidators.add(v);
			return;
		}

		Validates ann = v.method.getAnnotation(Validates.class);
		if (ann == null)
			vecValidators.add(v);
		else {
			for (Validate vl : ann.value()) {
				if (!vl.after() && vl.name().equalsIgnoreCase(strName))
					addValidator(vl.id(), vl.method(), vl.passskip().name(), vl
							.failskip().name(), vl.error(), new Vector<String>(
							Arrays.asList(vl.args())));
			}
			vecValidators.add(v);
			for (Validate vl : ann.value()) {
				if (vl.after() && vl.name().equalsIgnoreCase(strName))
					addValidator(vl.id(), vl.method(), vl.passskip().name(), vl
							.failskip().name(), vl.error(), new Vector<String>(
							Arrays.asList(vl.args())));
			}
		}
	}

	protected BeanProperty getProp(Class<?> clz, String name) throws Exception {
		return new BeanProperty(clz, name, true, true, null);
	}

	protected BeanProperty getProp() throws Exception {
		if (strProperty == null)
			return null;

		if (property == null) {
			int idx = strProperty.lastIndexOf(".");
			if (idx == -1) {
				propObject = propObject == null ? winlet : propObject;
				property = getProp(propObject.getClass(), strProperty);
			} else {
				String name = strProperty.substring(0, idx);

				if (name.indexOf(".") > 0)
					propObject = PropertyUtils.getNestedProperty(
							propObject == null ? winlet : propObject, name);
				else
					propObject = PropertyUtils.getSimpleProperty(
							propObject == null ? winlet : propObject, name);

				property = getProp(propObject.getClass(),
						strProperty.substring(idx + 1));
			}
		}

		return property;
	}

	@Override
	public Object getPropValue() throws Exception {
		if (strProperty == null)
			return objValue;

		return getProp().get(propObject);
	}

	protected void setPropValue(Object val) throws Exception {
		if (strProperty == null) {
			objValue = val;
			return;
		}

		getProp().set(propObject, val);
		form.recordChange(new ChangeUpdateValue(strName, toDisplay(val)));
	}

	Object toDisplay(Object val) throws Exception {
		if (val == null)
			return "";
		return val.toString();
	}

	Object fromDisplay(String[] value) throws Exception {
		if (value == null || value.length == 0)
			return null;
		return value[0];
	}

	@Override
	public Object getValue() {
		// { 如果有输入错误的值则返回
		if (bHasError)
			return strErrorValue;
		// }

		try {
			return toDisplay(getPropValue());
		} catch (Exception e) {
			m_log.error("Error getting value of property \"" + strProperty
					+ "\" of winlet \"" + winlet.getClass().getName()
					+ "\" for display.", e);
			return "";
		}
	}

	@Override
	public ValidateResultType populate(HttpServletRequest req) {
		return populate(req, req.getParameterValues(getName()));
	}

	@Override
	public ValidateResultType populate(HttpServletRequest req, String value) {
		return populate(req, new String[] { value });
	}

	ValidateResultType validate(HttpServletRequest req, Object val) {
		// 校验
		if (vecValidators != null)
			for (Validator v : vecValidators) {
				ValidateResult vs = v.validate(req, val);
				switch (vs.getType()) {
				case PASS_CONTINUE:
					break;
				case PASS_SKIP_PROPERTY:
					return ValidateResultType.PASS_CONTINUE;
				case PASS_SKIP_ALL:
					return ValidateResultType.PASS_SKIP_ALL;
				case FAILED_CONTINUE:
					addError(vs.getMsg());
					break;
				case FAILED_SKIP_PROPERTY:
					addError(vs.getMsg());
					return ValidateResultType.FAILED_CONTINUE;
				case FAILED_SKIP_ALL:
					addError(vs.getMsg());
					return ValidateResultType.FAILED_SKIP_ALL;
				}
			}

		if (bHasError)
			return ValidateResultType.FAILED_CONTINUE;
		else
			return ValidateResultType.PASS_CONTINUE;
	}

	@Override
	public ValidateResultType populate(HttpServletRequest req, String[] value) {
		strErrorValue = value == null || value.length == 0 ? null : value[0];
		clearErrors();

		if (bDisabled)
			return ValidateResultType.PASS_CONTINUE;

		try {
			Object val = fromDisplay(value);

			// { 如果值没有发生改变则无需设值
			Object currVal = getPropValue();
			if (!(val == null && currVal == null || val != null
					&& currVal != null && val.equals(currVal))) {
				// 设值
				setPropValue(val);
			}
			// }

			// 校验
			return validate(req, val);
		} catch (Exception e) {
			addError(strDefaultError);
			m_log.error("Error setting value \"" + value + "\" to property \""
					+ strProperty + "\" of winlet \""
					+ winlet.getClass().getName() + "\".");
			return ValidateResultType.FAILED_CONTINUE;
		}
	}

	/**
	 * 获取数字格式化类
	 * 
	 * @param format
	 * @return
	 */
	public static DecimalFormat getNumberFormat(String format) {
		synchronized (m_htNumFormat) {
			DecimalFormat fmt = m_htNumFormat.get(format);
			if (fmt != null)
				return fmt;

			fmt = new DecimalFormat(format);
			m_htNumFormat.put(format, fmt);
			return fmt;
		}

	}

	/**
	 * 获取日期格式化类
	 * 
	 * @param format
	 * @return
	 */
	public static SimpleDateFormat getSimpleDateFormat(String format) {
		synchronized (m_htDateFormat) {
			SimpleDateFormat fmt = m_htDateFormat.get(format);
			if (fmt != null)
				return fmt;

			fmt = new SimpleDateFormat(format);
			m_htDateFormat.put(format, fmt);
			return fmt;
		}
	}

	public static String markupFormat(String str) {
		EnumMarkup markup = MarkupTag.getMarkup();
		if (markup == null || markup == EnumMarkup.HTML)
			return StringUtils.toHTML(str, true);
		else if (markup == EnumMarkup.WML)
			return StringUtils.toWML(str);
		return str;
	}

	static InputImpl getInstance(FormImpl form, String type, Object winlet,
			String name) {
		if (type == null)
			return null;

		InputImpl input = null;
		if (type.equalsIgnoreCase("text"))
			input = new InputTextImpl();
		else if (type.equalsIgnoreCase("number"))
			input = new InputNumberImpl();
		else if (type.equalsIgnoreCase("date"))
			input = new InputDateImpl();
		else if (type.equalsIgnoreCase("hidden"))
			input = new InputHiddenImpl();
		else if (type.equalsIgnoreCase("checkbox"))
			input = new InputCheckBoxImpl();
		else if (type.equalsIgnoreCase("file"))
			input = new InputFileImpl();
		else if (type.equalsIgnoreCase("radio"))
			input = new InputRadioImpl();
		else if (type.equalsIgnoreCase("password"))
			input = new InputPasswordImpl();
		else if (type.equalsIgnoreCase("select"))
			input = new InputSelectImpl();
		else if (type.equalsIgnoreCase("textarea"))
			input = new InputTextAreaImpl();

		if (input == null)
			return null;
		input.form = form;
		input.strType = type;
		input.winlet = winlet;
		input.strName = name;
		return input;
	}

	public static String getClassName(String type) {
		if (type.equalsIgnoreCase("text"))
			return InputTextImpl.class.getName();
		if (type.equalsIgnoreCase("number"))
			return InputNumberImpl.class.getName();
		if (type.equalsIgnoreCase("date"))
			return InputDateImpl.class.getName();
		if (type.equalsIgnoreCase("hidden"))
			return InputHiddenImpl.class.getName();
		if (type.equalsIgnoreCase("checkbox"))
			return InputCheckBoxImpl.class.getName();
		if (type.equalsIgnoreCase("file"))
			return InputFileImpl.class.getName();
		if (type.equalsIgnoreCase("radio"))
			return InputRadioImpl.class.getName();
		if (type.equalsIgnoreCase("password"))
			return InputPasswordImpl.class.getName();
		if (type.equalsIgnoreCase("select"))
			return InputSelectImpl.class.getName();
		if (type.equalsIgnoreCase("textarea"))
			return InputTextAreaImpl.class.getName();
		return InputImpl.class.getName();
	}
}
