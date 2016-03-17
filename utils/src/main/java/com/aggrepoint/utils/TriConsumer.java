package com.aggrepoint.utils;

@FunctionalInterface
public interface TriConsumer<T, U, X> {
	void accept(T t, U u, X x);
}
