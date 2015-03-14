package com.aggrepoint.winlet.utils;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Validator;
import org.owasp.esapi.errors.ValidationException;

/**
 * @author: Yang Jiang Ming
 */
public class ValidateUtils {
	private static Validator validator = ESAPI.validator();

	static String getContext() {
		return "";
	}

	/**
	 * Integer Validation
	 * 
	 * @param context
	 * @param input
	 * @param minValue
	 * @param maxValue
	 * @param allowNull
	 * @return
	 */
	public static Integer validateInteger(final String context,
			final String input, final int minValue, final int maxValue,
			final boolean allowNull) throws ValidationException {
		return validator.getValidInteger(context, input, minValue, maxValue,
				allowNull);
	}

	public static Integer validateInteger(final String input,
			final int minValue, final int maxValue, final boolean allowNull)
			throws ValidationException {
		return validateInteger(getContext(), input, minValue, maxValue,
				allowNull);
	}

	public static Integer validateInteger(String input, boolean allowNull)
			throws ValidationException {
		return validateInteger(getContext(), input, Integer.MIN_VALUE,
				Integer.MAX_VALUE, allowNull);
	}

	public static Integer validateInteger(String input)
			throws ValidationException {
		return validateInteger(getContext(), input, Integer.MIN_VALUE,
				Integer.MAX_VALUE, true);
	}

	/**
	 * Double Validation
	 * 
	 * @param context
	 * @param input
	 * @param minValue
	 * @param maxValue
	 * @param allowNull
	 * @return
	 */
	public static Double validateDouble(final String context,
			final String input, final double minValue, final double maxValue,
			final boolean allowNull) throws ValidationException {
		return validator.getValidDouble(context, input, minValue, maxValue,
				allowNull);
	}

	public static Double validateDouble(final String input,
			final double minValue, final double maxValue,
			final boolean allowNull) throws ValidationException {
		return validateDouble(getContext(), input, minValue, maxValue,
				allowNull);
	}

	public static Double validateDouble(String input, boolean allowNull)
			throws ValidationException {
		return validateDouble(getContext(), input, Double.MIN_VALUE,
				Double.MAX_VALUE, allowNull);
	}

	public static Double validateDouble(String input)
			throws ValidationException {
		return validateDouble(getContext(), input, Double.MIN_VALUE,
				Double.MAX_VALUE, true);
	}

	/**
	 * Number Validation
	 * 
	 * @param context
	 * @param input
	 * @param minValue
	 * @param maxValue
	 * @param allowNull
	 * @return
	 */
	public static Double validateNumber(final String context,
			final String input, final long minValue, final long maxValue,
			final boolean allowNull) throws ValidationException {
		return validator.getValidNumber(context, input, minValue, maxValue,
				allowNull);
	}

	public static Double validateNumber(final String input,
			final long minValue, final long maxValue, final boolean allowNull)
			throws ValidationException {
		return validateNumber(getContext(), input, minValue, maxValue,
				allowNull);
	}

	public static Double validateNumber(String input, boolean allowNull)
			throws ValidationException {
		return validateNumber(getContext(), input, Long.MIN_VALUE,
				Long.MAX_VALUE, allowNull);
	}

	public static Double validateNumber(String input)
			throws ValidationException {
		return validateNumber(getContext(), input, Long.MIN_VALUE,
				Long.MAX_VALUE, true);
	}

	/**
	 * Regular Expression Validation
	 * 
	 * @param context
	 * @param input
	 * @param type
	 * @param maxLength
	 * @param allowNull
	 * @param canonicalize
	 * @return
	 */
	public static String validateInput(final String context,
			final String input, final String type, final int maxLength,
			final boolean allowNull, final boolean canonicalize)
			throws ValidationException {
		return validator.getValidInput(context, input, type, maxLength,
				allowNull, canonicalize);
	}

	public static String validateInput(final String input, final String type,
			final int maxLength, final boolean allowNull,
			final boolean canonicalize) throws ValidationException {
		return validateInput(getContext(), input, type, maxLength, allowNull,
				canonicalize);
	}

	public static String validateInput(final String input, final String type,
			final int maxLength) throws ValidationException {
		return validateInput(getContext(), input, type, maxLength, true, true);
	}

	public static String validateInput(final String input, final String type)
			throws ValidationException {
		return validateInput(getContext(), input, type, Integer.MAX_VALUE,
				true, true);
	}

	public static String validateUrl(final String input)
			throws ValidationException {
		return validateInput(getContext(), input, "URL", Integer.MAX_VALUE,
				true, true);
	}

	public static String validateUrl(final Object input)
			throws ValidationException {
		if (input == null)
			return null;

		return validateInput(getContext(), input.toString(), "URL",
				Integer.MAX_VALUE, true, true);
	}

	public static String validateOrderBy(final String input)
			throws ValidationException {
		return validateInput(getContext(), input, "SQL.ORDER_BY",
				Integer.MAX_VALUE, true, true);
	}
}
