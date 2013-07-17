package com.aggrepoint.winlet.form;


public class ChangeUpdateValue extends Change {
	Object value;

	public ChangeUpdateValue(String input, Object value) {
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
