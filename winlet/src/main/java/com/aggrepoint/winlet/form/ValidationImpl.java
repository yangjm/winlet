package com.aggrepoint.winlet.form;

import com.aggrepoint.winlet.ReqInfo;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ValidationImpl implements Validation {
	private ReqInfo reqInfo;

	public ValidationImpl(ReqInfo reqInfo) {
		this.reqInfo = reqInfo;
	}

	public boolean isValidateField() {
		return reqInfo.isValidateField();
	}

	public String getValidateFieldName() {
		if (reqInfo.isValidateField())
			return reqInfo.getValidateFieldName();
		return null;
	}

	public boolean hasError() {
		Form form = reqInfo.getForm();
		return form != null && form.hasError();
	}

	public void addError(String name, String msg) {
		Form form = reqInfo.getForm();
		if (form == null)
			return;

		form.addError(name, msg);
	}

	public void removeError(String name) {
		Form form = reqInfo.getForm();
		if (form == null)
			return;

		form.clearError(name);
	}

	public boolean validate(String name) {
		if (name == null)
			return false;

		if (!reqInfo.isValidateField())
			return true;

		return name.equals(getValidateFieldName());
	}
}
