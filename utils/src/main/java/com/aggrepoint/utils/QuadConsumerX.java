package com.aggrepoint.utils;

@FunctionalInterface
public interface QuadConsumerX<T, U, X, Y> {
	void accept(T t, U u, X x, Y y) throws Exception;
}
