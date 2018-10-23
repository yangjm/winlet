package com.aggrepoint.utils;

@FunctionalInterface
public interface ConsumerX<T> {
	void accept(T t) throws Exception;
}
