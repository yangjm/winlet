package com.aggrepoint.winlet.form;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
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
