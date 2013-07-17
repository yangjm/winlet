package com.aggrepoint.winlet.form;

public class ChangeUpdateValidateResult extends Change {
	String message;

	public ChangeUpdateValidateResult(String input, String message) {
		super(input, "v");
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
