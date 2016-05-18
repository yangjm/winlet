package com.aggrepoint.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;

public class BeanUtils {
	public static <T> T copyProps(T from, T to, String props) {
		if (props == null || from == null || to == null)
			return to;

		BeanWrapperImpl wFrom = new BeanWrapperImpl(from);
		BeanWrapperImpl wTo = new BeanWrapperImpl(to);

		for (String prop : props.split(", "))
			wTo.setPropertyValue(prop, wFrom.getPropertyValue(prop));

		return to;
	}

	public static <T> T setProps(T obj, HashMap<String, Object> props) {
		if (obj == null || props == null)
			return obj;
		BeanWrapperImpl w = new BeanWrapperImpl(obj);
		for (String key : props.keySet())
			w.setPropertyValue(key, props.get(key));
		return obj;
	}

	public static <T> T setProps(T obj, HashMap<String, ?> props,
			Collection<String> keys) {
		if (obj == null || props == null)
			return obj;
		BeanWrapperImpl w = new BeanWrapperImpl(obj);
		for (String key : keys)
			w.setPropertyValue(key, props.get(key));
		return obj;
	}

	public static <T> T setProps(T obj, HashMap<String, ?> props, String[] keys) {
		if (obj == null || props == null)
			return obj;
		BeanWrapperImpl w = new BeanWrapperImpl(obj);
		for (String key : keys)
			w.setPropertyValue(key, props.get(key));
		return obj;
	}

	public static List<String> getPropertyNames(Class<?> beanClass)
			throws IntrospectionException {
		PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(
				beanClass).getPropertyDescriptors();
		List<String> propertyNames = new ArrayList<String>(
				propertyDescriptors.length);
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			propertyNames.add(propertyDescriptor.getName());
		}

		return propertyNames;
	}

	static Pattern P_PROP_NAME = Pattern.compile("^\\w+");

	/**
	 * 把from在props1或props2中出现的属性拷贝到to。props1中可以包括from中没有的属性名称。
	 * props2主要用于checkbox参数名称。
	 */
	public static <T> T copyProps(T from, T to, Enumeration<String> props1,
			String props2) throws BeansException, IntrospectionException {
		if (props1 == null || from == null || to == null)
			return to;

		BeanWrapperImpl wFrom = new BeanWrapperImpl(from);
		BeanWrapperImpl wTo = new BeanWrapperImpl(to);

		HashSet<String> set = new HashSet<String>();
		while (props1.hasMoreElements()) {
			String prop = props1.nextElement();
			Matcher m = P_PROP_NAME.matcher(prop);
			if (m.find())
				set.add(m.group());
		}

		if (props2 != null)
			for (String prop : props2.split(", "))
				set.add(prop);

		for (String prop : getPropertyNames(from.getClass())) {
			if (!set.contains(prop))
				continue;
			wTo.setPropertyValue(prop, wFrom.getPropertyValue(prop));
		}

		return to;
	}
}
