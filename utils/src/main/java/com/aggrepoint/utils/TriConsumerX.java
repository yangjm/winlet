package com.aggrepoint.utils;

@FunctionalInterface
public interface TriConsumerX<T, U, X> {
	void accept(T t, U u, X x) throws Exception;
}
