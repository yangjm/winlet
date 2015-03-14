package com.aggrepoint.winlet.spring;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.util.StringValueResolver;

import com.aggrepoint.winlet.ContextUtils;

public class WinletDefaultFormattingConversionService extends
		DefaultFormattingConversionService {
	private HashSet<Class<? extends Annotation>> fmtAnnotations;

	private HashSet<Class<? extends Annotation>> getFmtAnnotations() {
		if (fmtAnnotations == null)
			fmtAnnotations = new HashSet<Class<? extends Annotation>>();
		return fmtAnnotations;
	}

	public WinletDefaultFormattingConversionService(
			StringValueResolver embeddedValueResolver,
			boolean registerDefaultFormatters) {
		super(embeddedValueResolver, registerDefaultFormatters);
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addFormatterForFieldAnnotation(
			AnnotationFormatterFactory annotationFormatterFactory) {
		Class<? extends Annotation> annotationType = (Class<? extends Annotation>) GenericTypeResolver
				.resolveTypeArgument(annotationFormatterFactory.getClass(),
						AnnotationFormatterFactory.class);
		if (annotationType != null)
			getFmtAnnotations().add(annotationType);

		super.addFormatterForFieldAnnotation(annotationFormatterFactory);
	}

	public boolean hasFmtAnnotation(TypeDescriptor td) {
		for (Annotation ann : td.getAnnotations())
			if (getFmtAnnotations().contains(ann.annotationType()))
				return true;
		return false;
	}

	// ////////////////////////////////////////
	//
	// 以下方法供Winlet模块直接调用
	//
	// ////////////////////////////////////////

	static HashMap<String, Boolean> cache = new HashMap<String, Boolean>();

	public static WinletDefaultFormattingConversionService get() {
		ConversionService cs = ContextUtils.getApplicationContext(
				ContextUtils.getRequest()).getBean(ConversionService.class);

		if (cs == null)
			return null;
		if (cs instanceof WinletDefaultFormattingConversionService)
			return (WinletDefaultFormattingConversionService) cs;
		return null;
	}

	/**
	 * 判断对象属性是否有格式可以应用。调用format()前必须先调用canFormat()
	 * 
	 * @param obj
	 * @param prop
	 * @return
	 */
	public static boolean canFormat(ConfigurablePropertyAccessor bw, Object obj, String prop) {
		String key = obj.getClass().getName() + "_" + prop;

		if (!cache.containsKey(key)) {
			cache.put(key, false);

			TypeDescriptor td = bw.getPropertyTypeDescriptor(prop);
			if (td != null) {
				WinletDefaultFormattingConversionService wcs = get();
				if (wcs != null
						&& wcs.hasFmtAnnotation(td)
						&& wcs.canConvert(td,
								TypeDescriptor.valueOf(String.class)))
					cache.put(key, true);
			}
		}

		return cache.get(key);
	}

	public static boolean canFormat(Object obj, String prop) {
		return canFormat(new BeanWrapperImpl(obj), obj, prop);
	}

	/**
	 * 如果有格式可应用则应用
	 * 
	 * @param bw
	 * @param obj
	 * @param prop
	 * @return
	 */
	public static String format(ConfigurablePropertyAccessor bw, Object obj, String prop) {
		String key = obj.getClass().getName() + "_" + prop;

		if (!cache.containsKey(key))
			canFormat(bw, obj, prop);

		Object val = bw.getPropertyValue(prop);

		if (val == null)
			return null;

		if (!cache.get(key))
			return val.toString();

		TypeDescriptor td = bw.getPropertyTypeDescriptor(prop);
		val = get().convert(val, td, TypeDescriptor.valueOf(String.class));

		return val == null ? null : val.toString();
	}

	public static String format(Object obj, String prop) {
		return format(new BeanWrapperImpl(obj), obj, prop);
	}
}
