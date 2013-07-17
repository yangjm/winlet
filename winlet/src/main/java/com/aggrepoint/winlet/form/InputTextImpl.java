package com.aggrepoint.winlet.form;


public class InputTextImpl extends InputImpl {
	@Override
	Object toDisplay(Object val) throws Exception {
		if (val == null)
			return super.toDisplay(val);

		return markupFormat(val.toString());
	}
}
