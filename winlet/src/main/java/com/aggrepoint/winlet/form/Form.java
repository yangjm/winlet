package com.aggrepoint.winlet.form;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface Form {
	public boolean isValidateField();

	public String getValue(String field);

	public String getValue(String field, String def);

	public String[] getValues(String field);

	public void setValue(String field, String value);

	public void setValue(String fields, String[] value);

	public boolean hasError();

	public String[] getErrors(String field);

	public void addError(String field, String error);

	public void clearError(String field);

	public void setDisabled(String field);

	public void setEnabled(String field);

	public String[] getDisabledFields();
}
