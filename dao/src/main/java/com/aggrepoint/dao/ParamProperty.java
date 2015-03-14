package com.aggrepoint.dao;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public class ParamProperty {
	String param;
	String property;
	boolean bNested;

	public ParamProperty(String param, String property) {
		this.param = param;
		this.property = property;
		bNested = property.indexOf('.') > 0;
	}

	public String getParam() {
		return param;
	}

	public String getProperty() {
		return property;
	}

	public boolean isbNested() {
		return bNested;
	}

	public Object getValue(Object obj) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException {
		if (obj == null)
			return null;

		if (bNested)
			return PropertyUtils.getProperty(obj, property);
		else
			return PropertyUtils.getNestedProperty(obj, property);
	}
}
