package com.aggrepoint.winlet.site;

import com.aggrepoint.utils.TwoValues;

public class Utils {
	public static TwoValues<String, Integer> getNameAndOrder(String name) {
		int idx = name.indexOf("#");
		if (idx > 0) {
			return new TwoValues<String, Integer>(name.substring(idx + 1), Integer.parseInt(name.substring(0, idx)));
		} else
			return new TwoValues<String, Integer>(name, null);
	}
}
