package com.aggrepoint.winlet.format;

import java.util.HashSet;
import java.util.Set;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

public class SanitizeFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<SanitizeFormat> {
	@Override
	public Set<Class<?>> getFieldTypes() {
		HashSet<Class<?>> set = new HashSet<Class<?>>();
		set.add(String.class);
		return set;
	}

	@Override
	public Printer<?> getPrinter(SanitizeFormat annotation, Class<?> fieldType) {
		return new SanitizeFormatter();
	}

	@Override
	public Parser<?> getParser(SanitizeFormat annotation, Class<?> fieldType) {
		return new SanitizeFormatter();
	}
}
