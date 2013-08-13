package com.aggrepoint.winlet.form;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import net.sf.json.JSONNull;
import net.sf.json.JSONSerializer;

import com.aggrepoint.winlet.ReqConst;
import com.aggrepoint.winlet.ReqInfo;

public class FormImpl implements Form, ReqConst {
	static final String FORM_DATA_SESSION_KEY = FormImpl.class.getName();

	private ReqInfo ri;
	private ArrayList<String> fields = new ArrayList<String>();
	private Hashtable<String, Vector<String>> fieldValues = new Hashtable<String, Vector<String>>();
	private Hashtable<String, ArrayList<String>> fieldErrors = new Hashtable<String, ArrayList<String>>();
	private HashSet<String> disabledFields;
	private Vector<Change> vecChanges = new Vector<Change>();

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
	}

	public boolean isValidateField() {
		return ri.isValidateField();
	}

	public boolean isValidateForm() {
		return "yes".equalsIgnoreCase(ri.getParameter(PARAM_WIN_FORM_VALIDATE,
				"no"));
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
		String val = getValue(field);
		return val == null ? def : val;
	}

	@Override
	public String getValue(String field) {
		String[] val = getValues(field);
		if (val == null || val.length < 1)
			return null;
		return val[0];
	}

	@Override
	public String[] getValues(String field) {
		Vector<String> vals = fieldValues.get(field);
		if (vals != null)
			return vals.toArray(new String[vals.size()]);

		String[] val = null;
		if (ri.isValidateField()) {
			if (field.equals(ri.getValidateFieldName()))
				val = new String[] { ri.getValidateFieldValue() };
		} else
			val = ri.getRequest().getParameterValues(field);

		return val;
	}

	@Override
	public void setValue(String field, String value) {
		Vector<String> values = fieldValues.get(field);
		if (values == null) {
			values = new Vector<String>();
			fieldValues.put(field, values);
		}

		values.clear();
		values.add(value);

		recordChange(new ChangeUpdateValue(field, value));
	}

	@Override
	public void setValue(String field, String[] value) {
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
	public boolean hasError() {
		return fieldErrors.size() > 0;
	}

	@Override
	public String[] getErrors(String field) {
		ArrayList<String> errors = fieldErrors.get(field);
		if (errors == null)
			return null;

		return errors.toArray(new String[errors.size()]);
	}

	@Override
	public void addError(String field, String error) {
		ArrayList<String> errors = fieldErrors.get(field);
		if (errors == null) {
			errors = new ArrayList<String>();
			fieldErrors.put(field, errors);
		}

		errors.add(error);

		recordChange(new ChangeUpdateValidateResult(field, error));
	}

	@Override
	public void clearError(String field) {
		fieldErrors.remove(field);

		removeChange(new ChangeUpdateValidateResult(field, ""));
	}

	@Override
	public void setDisabled(String field) {
		disabledFields.add(field);
		clearError(field);
		recordChange(new ChangeDisable(field));
	}

	@Override
	public void setEnabled(String field) {
		disabledFields.remove(field);
		recordChange(new ChangeEnable(field));
	}

	@Override
	public String[] getDisabledFields() {
		return disabledFields.toArray(new String[disabledFields.size()]);
	}

	private static Hashtable<Method, ArrayList<FormValidator>> HT_VALIDATORS = new Hashtable<Method, ArrayList<FormValidator>>();

	private ArrayList<FormValidator> getValidators(Object controller, Method m) {
		ArrayList<FormValidator> validators = HT_VALIDATORS.get(m);

		if (validators == null) {
			validators = new ArrayList<FormValidator>();
			HT_VALIDATORS.put(m, validators);

			Validates ann = m.getAnnotation(Validates.class);
			ArrayList<Validate> vs = new ArrayList<Validate>();

			if (ann != null) {
				for (Validate vl : ann.value())
					if (!vl.after())
						vs.add(vl);
				for (Validate vl : ann.value())
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

	public String getJsonChanges() {
		if (vecChanges == null)
			return JSONSerializer.toJSON(JSONNull.getInstance()).toString();

		return JSONSerializer.toJSON(
				vecChanges.toArray(new Change[vecChanges.size()])).toString();
	}
}
