package com.aggrepoint.winlet.form.del;

import java.util.Date;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class InputDateImpl extends InputTextImpl {
	public String strFormat;

	public String getFormat() {
		return strFormat;
	}

	@Override
	public void setAttr(String name, Object value) {
		if (name.equals("format")) {
			if (value != null && !value.equals(""))
				strFormat = value.toString();
		} else
			super.setAttr(name, value);
	}

	@Override
	Object toDisplay(Object val) throws Exception {
		if (strFormat == null || strFormat.equals("") || val == null)
			return super.toDisplay(val);

		if (strFormat != null && !strFormat.equals("")
				&& Date.class.isAssignableFrom(val.getClass()))
			return getSimpleDateFormat(strFormat).format((Date) val);

		return super.toDisplay(val);
	}

	@Override
	Object fromDisplay(String[] value) throws Exception {
		if (value == null || value.length == 0 || value[0].trim().equals(""))
			return null;

		return getSimpleDateFormat(strFormat).parse(value[0]);
	}
}
