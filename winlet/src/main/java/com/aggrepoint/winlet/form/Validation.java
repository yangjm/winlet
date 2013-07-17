package com.aggrepoint.winlet.form;

public interface Validation {
	public boolean isValidateField();

	public String getValidateFieldName();

	public InputImpl getValidateField();

	public boolean hasError();

	public void addError(String name, String msg);

	public boolean validate(String name);
}
