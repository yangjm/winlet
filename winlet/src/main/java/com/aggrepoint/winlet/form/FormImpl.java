package com.aggrepoint.winlet.form;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ReqConst;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.spring.WinletDefaultFormattingConversionService;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FormImpl implements Form, ReqConst {
	static final String FORM_DATA_SESSION_KEY = FormImpl.class.getName();

	private ArrayList<WebDataBinder> binders = new ArrayList<WebDataBinder>();
	private ReqInfo ri;
	private ArrayList<String> fields = new ArrayList<String>();
	private HashSet<String> groupNames = new HashSet<String>();
	private Hashtable<String, Vector<String>> fieldValues = new Hashtable<String, Vector<String>>();
	private Hashtable<String, ArrayList<String>> fieldErrors = new Hashtable<String, ArrayList<String>>();
	/** bindingErrorCount的值为在全表单校验时BindingError的计数，不管这些Error是否有对应Form中的Field。 */
	/** 通过程序代码提交表单请求时，请求中可能不带表单字段名称列表，即使数据有错误，fieldErrors也可能为空。 */
	/** 一般情况下要确保bindingErrorCount也不为0才可以视为表单数据校验通过。 */
	private int bindingErrorCount;
	private HashSet<String> disabledFields;
	private Vector<Change> vecChanges = new Vector<Change>();

	static Pattern ARRAY = Pattern.compile("^(.+)\\[(\\d+)\\]$");

	public FormImpl(ReqInfo ri) {
		this.ri = ri;

		if (ri.isValidateField()) {
			fields.add(ri.getValidateFieldName());
			disabledFields = new HashSet<String>();
		} else {
			toCollection(fields,
					ri.getRequest().getParameterValues(PARAM_WIN_FORM_FIELDS));
			disabledFields = toSet(ri.getRequest().getParameterValues(
					PARAM_WIN_FORM_DISABLED_FIELD));
		}

		for (String field : fields) {
			Matcher m = ARRAY.matcher(field);
			if (m.find())
				groupNames.add(m.group(1));
		}
	}

	public boolean isValidateField() {
		return ri.isValidateField();
	}

	public String getValidateFieldName() {
		if (ri.isValidateField())
			return ri.getValidateFieldName();
		return null;
	}

	public String getValidateFieldId() {
		if (ri.isValidateField())
			return ri.getValidateFieldId();
		return null;
	}

	public boolean isValidateForm() {
		String val = ri.getParameter(PARAM_WIN_FORM_VALIDATE, "no");
		return "yes".equalsIgnoreCase(val) || "form".equals(val)
				|| "field".equals(val);
	}

	public boolean validate(String field) {
		if (disabledFields != null && disabledFields.contains(field))
			return false;

		return !ri.isValidateField() // 不是单字段校验，对所有字段都校验
				|| fields.contains(field) || groupNames.contains(field);
	}

	public void recordChange(Change change) {
		change.addTo(vecChanges);
	}

	public void removeChange(Change change) {
		vecChanges
				.removeAll(Change.find(vecChanges, change.type, change.input));
	}

	private static HashSet<String> toSet(String[] vals) {
		HashSet<String> set = new HashSet<String>();
		toCollection(set, vals);
		return set;
	}

	private static void toCollection(Collection<String> c, String[] vals) {
		if (vals != null)
			for (String s : vals)
				if (!c.contains(s))
					c.add(s);
	}

	@Override
	public String getValue(String field, String def) {
		processBinders();

		String val = getValue(field);
		return val == null ? def : val;
	}

	@Override
	public String getValue(String field) {
		processBinders();

		String[] val = getValues(field);
		if (val == null || val.length < 1)
			return null;
		return val[0];
	}

	@Override
	public String[] getValues(String field) {
		processBinders();

		String name = field;
		Integer idx = null;

		Matcher m = ARRAY.matcher(field);
		if (m.find()) {
			name = m.group(1);
			idx = Integer.parseInt(m.group(2));
		}

		Vector<String> vals = fieldValues.get(name);

		if (vals != null) {
			if (idx == null)
				return vals.toArray(new String[vals.size()]);

			if (vals.size() > idx)
				return new String[] { vals.get(idx) };
		}

		if (ri.isValidateField()) {
			if (field.equals(ri.getValidateFieldName()))
				return new String[] { ri.getValidateFieldValue() };
		} else {
			String[] val = ri.getRequest().getParameterValues(name);

			if (val == null)
				return null;

			if (idx == null)
				return val;

			if (val.length > idx)
				return new String[] { val[idx] };
		}

		return null;
	}

	@Override
	public void setValue(String field, String value) {
		processBinders();

		String name = field;
		Integer idx = null;

		Matcher m = ARRAY.matcher(field);
		if (m.find()) {
			name = m.group(1);
			idx = Integer.parseInt(m.group(2));
		}

		Vector<String> values = fieldValues.get(name);
		if (values == null) {
			values = new Vector<String>();
			fieldValues.put(name, values);
		}

		if (idx == null) {
			values.clear();
			values.add(value);
		} else {
			for (int i = values.size(); i <= idx; i++) {
				values.add("");
			}
			values.set(idx, value);
		}

		recordChange(new ChangeUpdateValue(field, value));
	}

	@Override
	public void setValue(String field, String[] value) {
		processBinders();

		Vector<String> values = fieldValues.get(field);
		if (values == null) {
			values = new Vector<String>();
			fieldValues.put(field, values);
		}

		values.clear();
		if (value != null)
			for (String v : value)
				values.add(v);

		recordChange(new ChangeUpdateValue(field, value));
	}

	@Override
	public void setSelectOptions(String field,
			Collection<? extends SelectOption> list) {
		processBinders();

		recordChange(new ChangeUpdateList(field, list));
	}

	@Override
	public boolean hasError() {
		processBinders();

		return fieldErrors.size() > 0 || bindingErrorCount > 0;
	}

	@Override
	public boolean hasErrorOrValidateField() {
		return hasError() || isValidateField();
	}

	@Override
	public boolean hasError(boolean fieldErrorsOnly) {
		processBinders();

		if (fieldErrorsOnly)
			return fieldErrors.size() > 0;

		return fieldErrors.size() > 0 || bindingErrorCount > 0;
	}

	@Override
	public boolean hasError(String field) {
		processBinders();

		ArrayList<String> errors = fieldErrors.get(field);
		if (errors == null || errors.size() == 0)
			return false;

		return true;
	}

	@Override
	public String[] getErrors(String field) {
		processBinders();

		ArrayList<String> errors = fieldErrors.get(field);
		if (errors == null || errors.size() == 0)
			return null;

		return errors.toArray(new String[errors.size()]);
	}

	@Override
	public void addError(String field, String error) {
		processBinders();

		ArrayList<String> errors = fieldErrors.get(field);
		if (errors == null) {
			errors = new ArrayList<String>();
			fieldErrors.put(field, errors);
		}

		errors.add(error);

		recordChange(new ChangeUpdateValidateResult(field, error));
	}

	@Override
	public void clearErrors() {
		processBinders();

		fieldErrors.clear();
		vecChanges.clear();
	}

	@Override
	public void clearError(String field) {
		processBinders();

		fieldErrors.remove(field);

		removeChange(new ChangeUpdateValidateResult(field, ""));
	}

	@Override
	public void setDisabled(String field) {
		processBinders();

		disabledFields.add(field);
		clearError(field);
		recordChange(new ChangeDisable(field));
	}

	@Override
	public void setEnabled(String field) {
		processBinders();

		disabledFields.remove(field);
		recordChange(new ChangeEnable(field));
	}

	@Override
	public void show(String selector) {
		processBinders();

		recordChange(new ChangeShow(selector));
	}

	@Override
	public void hide(String selector) {
		processBinders();

		recordChange(new ChangeHide(selector));
	}

	@Override
	public String[] getDisabledFields() {
		processBinders();

		return disabledFields.toArray(new String[disabledFields.size()]);
	}

	private static Hashtable<Method, ArrayList<FormValidator>> HT_VALIDATORS = new Hashtable<Method, ArrayList<FormValidator>>();

	private ArrayList<FormValidator> getValidators(Object controller, Method m) {
		ArrayList<FormValidator> validators = HT_VALIDATORS.get(m);

		if (validators == null) {
			validators = new ArrayList<FormValidator>();
			HT_VALIDATORS.put(m, validators);

			Validate[] ann = m.getAnnotationsByType(Validate.class);
			ArrayList<Validate> vs = new ArrayList<Validate>();

			if (ann != null) {
				for (Validate vl : ann)
					if (!vl.after())
						vs.add(vl);
				for (Validate vl : ann)
					if (vl.after())
						vs.add(vl);

				for (Validate vl : vs)
					validators.add(new FormValidator(controller, vl.name(), vl
							.pattern(), vl.id(), vl.method(), vl.passskip()
							.name(), vl.failskip().name(), vl.error(),
							new Vector<String>(Arrays.asList(vl.args()))));
			}
		}

		return validators;
	}

	public void validate(ReqInfo ri, Object controller, Method method) {
		processBinders();

		ArrayList<FormValidator> vs = getValidators(controller, method);

		for (String field : fields) {
			if (disabledFields.contains(field))
				continue;

			String[] values = this.getValues(field);
			String value = values == null || values.length < 1 ? null
					: values[0];

			for (Iterator<FormValidator> itv = vs.iterator(); itv.hasNext();) {
				FormValidator dv = itv.next();

				if (dv.matches(field)) {
					ValidateResult vr = dv.validate(ri.getRequest(), this,
							value);
					switch (vr.getType()) {
					case PASS_CONTINUE:
						continue;
					case PASS_SKIP_PROPERTY:
						break;
					case PASS_SKIP_ALL:
						return;
					case FAILED_CONTINUE:
						addError(field, vr.getMsg());
						continue;
					case FAILED_SKIP_PROPERTY:
						addError(field, vr.getMsg());
						break;
					case FAILED_SKIP_ALL:
						addError(field, vr.getMsg());
						return;
					}
				}
			}
		}
	}

	public String getJsonChanges() throws JsonGenerationException,
			JsonMappingException, IOException {
		processBinders();

		if (vecChanges == null)
			return "{}";

		return new ObjectMapper().writeValueAsString(vecChanges
				.toArray(new Change[vecChanges.size()]));
	}

	/**
	 * 获取Spring为控制器生成参数值过程中建立的binder，以便将binder处理中遇到的错误合并到form中，
	 * 并且利用binder来获得经过conversion和format处理之后的参数值
	 * 
	 * @param binder
	 */
	public void addBinder(WebDataBinder binder) {
		binders.add(binder);
	}

	protected void mergeBindingResult(String field,
			Collection<BindingResult> values) {
		if (disabledFields.contains(field))
			return;

		HttpServletRequest request = ContextUtils.getRequest();
		WebApplicationContext context = RequestContextUtils
				.findWebApplicationContext(request);
		Locale locale = RequestContextUtils.getLocale(request);

		for (BindingResult val : values) {
			if (!(val instanceof BeanPropertyBindingResult))
				continue;

			BeanPropertyBindingResult br = (BeanPropertyBindingResult) val;

			if (br.getTarget() != null && br.getFieldType(field) != null
					|| br.getObjectName().equals(field)) {
				// merge Spring conversion & validation errors
				List<FieldError> errors = br.getFieldErrors(field);
				if (errors != null && errors.size() > 0) {
					errors.forEach(p -> {
						addError(field, context.getMessage(p, locale));
					});
				} else if (getErrors(field) == null && br.getTarget() != null) {
					// no error, apply Spring formatter
					String value = WinletDefaultFormattingConversionService
							.format(br.getPropertyAccessor(), br.getTarget(),
									field);
					if (value != null)
						setValue(field, value.toString());
				}
				break;
			}
		}
	}

	/**
	 * Merge Spring conversion / format / validation errors into form
	 * 
	 * @param values
	 */
	protected void mergeBindingResult(Collection<BindingResult> values) {
		if (ri.isValidateField()) {
			mergeBindingResult(ri.getValidateFieldName(), values);
		} else {
			if (values != null) {
				values.forEach(p -> bindingErrorCount += p.getErrorCount());
				fields.forEach(p -> mergeBindingResult(p, values));
			}
		}
	}

	protected void processBinders() {
		Collection<BindingResult> col = binders.stream()
				.map(p -> p.getBindingResult()).collect(Collectors.toList());
		binders.clear();
		mergeBindingResult(col);
	}

	@Override
	public boolean hasField(String field) {
		return fields.contains(field);
	}

	@Override
	public Form addError(String field, Function<String, Boolean> when,
			String error, boolean validateEvenErrorExist) {
		if (!validate(field))
			return this;
		if (hasError(field) && !validateEvenErrorExist)
			return this;

		if (when.apply(getValue(field)))
			addError(field, error);

		return this;
	}

	@Override
	public Form addError(String field, boolean when, String error,
			boolean validateEvenErrorExist) {
		if (!validate(field))
			return this;
		if (hasError(field) && !validateEvenErrorExist)
			return this;

		if (when)
			addError(field, error);

		return this;
	}

	@Override
	public Form addError(String field, Function<String, Boolean> when,
			String error) {
		return addError(field, when, error, false);
	}

	@Override
	public Form addError(String field, boolean when, String error) {
		return addError(field, when, error, false);
	}

	@Override
	public String mapStatus(String vf, String vfError, String error,
			String passed) {
		if (isValidateField())
			if (hasError())
				return vfError;
			else
				return vf;
		if (hasError())
			return error;
		return passed;
	}

	@Override
	public String mapStatus(String vf, String vfError, String error) {
		return mapStatus(vf, vfError, error, null);
	}

	@Override
	public Form noError(Process process) throws Exception {
		if (!hasError() && !isValidateField())
			process.run();
		return this;
	}
}
