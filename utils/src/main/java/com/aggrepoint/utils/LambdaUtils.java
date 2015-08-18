package com.aggrepoint.utils;

import java.util.function.Consumer;
import java.util.function.Function;

public class LambdaUtils {
	public static <T> void consumeIfNotNull(T val, Consumer<T> consumer) {
		if (val != null)
			consumer.accept(val);
	}

	public static <T, R> R ifNotNull(T val, Function<T, R> func) {
		if (val != null)
			return func.apply(val);
		return null;
	}
}