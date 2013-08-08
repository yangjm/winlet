package com.aggrepoint.winlet.form;

/**
 * Validation method should return this
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ValidateResult {
	public static ValidateResult PASS = new ValidateResult(
			ValidateResultType.PASS_CONTINUE);
	public static ValidateResult PASS_SKIP_PROPERTY = new ValidateResult(
			ValidateResultType.PASS_SKIP_PROPERTY);
	public static ValidateResult PASS_SKIP_ALL = new ValidateResult(
			ValidateResultType.PASS_SKIP_ALL);
	public static ValidateResult FAILED = new ValidateResult(
			ValidateResultType.FAILED_CONTINUE);
	public static ValidateResult FAILED_SKIP_PROPERTY = new ValidateResult(
			ValidateResultType.FAILED_SKIP_PROPERTY);
	public static ValidateResult FAILED_SKIP_ALL = new ValidateResult(
			ValidateResultType.FAILED_SKIP_ALL);

	ValidateResultType type;
	String msg;

	public ValidateResult(ValidateResultType type, String msg) {
		this.type = type;
		this.msg = msg;
	}

	public ValidateResult(ValidateResultType type) {
		this(type, null);
	}

	public ValidateResultType getType() {
		return type;
	}

	public String getMsg() {
		return msg;
	}
}
