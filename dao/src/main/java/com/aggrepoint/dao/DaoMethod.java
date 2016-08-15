package com.aggrepoint.dao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public interface DaoMethod<T> {
	/**
	 * 判断Method是否适用于当前的参数
	 * 
	 * @param args
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public boolean match(Object[] args) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException;

	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable;
}
