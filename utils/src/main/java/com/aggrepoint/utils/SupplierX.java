package com.aggrepoint.utils;

@FunctionalInterface
public interface SupplierX<T> {
	T get() throws Exception;
}
