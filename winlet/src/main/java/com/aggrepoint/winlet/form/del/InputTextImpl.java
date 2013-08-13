package com.aggrepoint.winlet.form.del;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class InputTextImpl extends InputImpl {
	@Override
	Object toDisplay(Object val) throws Exception {
		if (val == null)
			return super.toDisplay(val);

		return markupFormat(val.toString());
	}
}
