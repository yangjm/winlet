package com.aggrepoint.utils;

import java.util.function.Function;

public class EnumUtils {
	public static <T> T fromString(String str, Function<String, T> valueof,
			T def) {

		if (str == null)
			return def;
		try {
			return valueof.apply(str);
		} catch (Exception e) {
		}

		return def;
	}
}
