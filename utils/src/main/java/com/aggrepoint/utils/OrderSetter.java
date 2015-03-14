package com.aggrepoint.utils;

@FunctionalInterface
public interface OrderSetter<T> {
	void apply(T t, Integer v);
}
