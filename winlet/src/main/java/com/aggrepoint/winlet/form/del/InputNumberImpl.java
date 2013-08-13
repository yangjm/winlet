package com.aggrepoint.winlet.form.del;

import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class InputNumberImpl extends InputTextImpl {
	static Pattern NUMBER_PATTERN = Pattern.compile("^[-+]?[0-9]*\\.?[0-9]+$");
	public String strFormat = "0";
	public boolean bIgnoreError;

	public String getFormat() {
		return strFormat;
	}

	public boolean getIgnoreError() {
		return bIgnoreError;
	}

	@Override
	public void setAttr(String name, Object value) {
		if (name.equals("format")) {
			if (value != null && !value.equals(""))
				strFormat = value.toString();
		} else if (name.equals("ignoreerror")) {
			if (value != null && !value.equals(""))
				bIgnoreError = value.toString().equalsIgnoreCase("yes");
		} else
			super.setAttr(name, value);
	}

	@Override
	Object toDisplay(Object val) throws Exception {
		if (strFormat == null || strFormat.equals("") || val == null)
			return super.toDisplay(val);

		if (val instanceof Short || val instanceof Integer
				|| val instanceof Long)
			return getNumberFormat(strFormat)
					.format(((Number) val).longValue());

		if (val instanceof Number)
			return getNumberFormat(strFormat).format(
					((Number) val).doubleValue());

		return super.toDisplay(val);
	}

	@Override
	Object fromDisplay(String[] value) throws Exception {
		if (value == null || value.length == 0 || value[0].trim().equals(""))
			return null;

		try {
			String val = value[0].trim();

			if (!NUMBER_PATTERN.matcher(val).find())
				throw new ParseException("Invalid number format.", 0);

			return getNumberFormat(strFormat).parse(val);
		} catch (Exception e) {
			if (bIgnoreError)
				return "0";
			throw e;
		}
	}
}
