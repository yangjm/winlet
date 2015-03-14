package com.aggrepoint.winlet.utils;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

import org.apache.commons.beanutils.PropertyUtils;

/**
 * 利用PropertyUtils实现对多级属性的支持
 * 
 * @author Jim
 */
public class NestedBeanProperty extends BeanProperty {
	/** 是否需要先通过PropertyUtils获得实际的取值对象 */
	protected boolean bUserPropertyUtils;
	/** 使用PorpertyUtils时的property参数 */
	protected String strUtilsProperty;
	/** 是否需要使用PropertyUtils的Nested方法 */
	protected boolean bNested;

	public NestedBeanProperty(Object obj, String propertyName,
			boolean readable, boolean writable) throws BeanPropertyException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		int idx = propertyName.lastIndexOf(".");

		if (idx >= 0) {
			bUserPropertyUtils = true;
			strUtilsProperty = propertyName.substring(0, idx);
			bNested = strUtilsProperty.indexOf(".") > 0;

			if (bNested)
				init(PropertyUtils.getNestedProperty(obj, strUtilsProperty)
						.getClass(), propertyName.substring(propertyName
						.lastIndexOf(".") + 1), readable, writable, null);
			else
				init(PropertyUtils.getSimpleProperty(obj, strUtilsProperty)
						.getClass(), propertyName.substring(propertyName
						.lastIndexOf(".") + 1), readable, writable, null);
		} else {
			bUserPropertyUtils = false;
			init(obj.getClass(), propertyName, readable, writable, null);
		}
	}

	Object getObject(Object obj) throws IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		if (bUserPropertyUtils) {
			if (bNested)
				return PropertyUtils.getNestedProperty(obj, strUtilsProperty);
			else
				return PropertyUtils.getSimpleProperty(obj, strUtilsProperty);
		}

		return obj;
	}

	@Override
	public Object get(Object obj, Object... args)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, BeanPropertyException,
			NoSuchMethodException {
		return super.get(getObject(obj), args);
	}

	@Override
	public void set(Object obj, Object val, Object... args)
			throws SQLException, IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			BeanPropertyException, NoSuchMethodException {
		super.set(getObject(obj), val, args);
	}

	@Override
	public long getNum(Object obj) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			BeanPropertyException, NoSuchMethodException {
		return super.getNum(getObject(obj));
	}

	@Override
	public void setNum(Object obj, long val) throws IllegalArgumentException,
			IllegalAccessException, InvocationTargetException,
			BeanPropertyException, NoSuchMethodException {
		super.setNum(getObject(obj), val);
	}
}
