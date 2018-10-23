package com.aggrepoint.utils;

@FunctionalInterface
public interface BiConsumerX<T, U> {
	void accept(T t, U u) throws Exception;
}
