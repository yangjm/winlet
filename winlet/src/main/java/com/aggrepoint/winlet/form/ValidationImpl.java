package com.aggrepoint.winlet.form;

import com.aggrepoint.winlet.ReqInfo;

public class ValidationImpl implements Validation {
	private ReqInfo reqInfo;

	public ValidationImpl(ReqInfo reqInfo) {
		this.reqInfo = reqInfo;
	}

	public boolean isValidateField() {
		return reqInfo.isValidateField();
	}

	public InputImpl getValidateField() {
		if (reqInfo.getForm() == null)
			return null;

		return reqInfo.getForm().getValidateField();
	}

	public String getValidateFieldName() {
		InputImpl input = getValidateField();
		if (input == null)
			return null;
		return input.getName();
	}

	public boolean hasError() {
		FormImpl form = reqInfo.getForm();
		return form != null && form.hasError();
	}

	public void addError(String name, String msg) {
		FormImpl form = reqInfo.getForm();
		if (form == null)
			return;

		InputImpl input = form.getInputByName(name);
		if (input == null)
			return;

		input.addError(msg);
	}

	public void removeError(String name) {
		FormImpl form = reqInfo.getForm();
		if (form == null)
			return;

		InputImpl input = form.getInputByName(name);
		if (input == null)
			return;

		input.removeError();
	}

	public boolean validate(String name) {
		if (name == null)
			return false;

		if (!reqInfo.isValidateField())
			return true;

		return name.equals(getValidateFieldName());
	}
}
