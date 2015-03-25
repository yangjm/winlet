package com.aggrepoint.dao;

import java.lang.reflect.Method;

import org.springframework.cache.interceptor.SimpleKeyGenerator;

/**
 * 将Dao类名和方法名称以及参数一起作为cache的key
 * 
 * @author jiangmingyang
 */
public class DaoCacheKeyGenerator extends SimpleKeyGenerator {
	@Override
	public Object generate(Object clz, Method method, Object... params) {
		return generateKey(clz.toString(), method.getName(), params);
	}
}
