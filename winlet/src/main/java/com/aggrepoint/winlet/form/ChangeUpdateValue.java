package com.aggrepoint.winlet.form;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ChangeUpdateValue extends Change {
	Object value;

	public ChangeUpdateValue(String input, Object value) {
		super(input, "u");
		this.value = value;
	}

	public ChangeUpdateValue(String input, String[] value) {
		super(input, "u");
		this.value = value;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
