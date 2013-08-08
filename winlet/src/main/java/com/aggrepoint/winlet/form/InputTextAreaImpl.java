package com.aggrepoint.winlet.form;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class InputTextAreaImpl extends InputImpl {
	@Override
	Object toDisplay(Object val) throws Exception {
		if (val == null)
			return super.toDisplay(val);

		return markupFormat((String) val);
	}
}
