package com.aggrepoint.winlet.form;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface Validation {
	public boolean isValidateField();

	public String getValidateFieldName();

	public boolean hasError();

	public void addError(String name, String msg);

	public void removeError(String name);

	public boolean validate(String name);
}
