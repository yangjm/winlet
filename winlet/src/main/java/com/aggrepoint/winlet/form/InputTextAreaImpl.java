package com.aggrepoint.winlet.form;


public class InputTextAreaImpl extends InputImpl {
	@Override
	Object toDisplay(Object val) throws Exception {
		if (val == null)
			return super.toDisplay(val);

		return markupFormat((String) val);
	}
}
