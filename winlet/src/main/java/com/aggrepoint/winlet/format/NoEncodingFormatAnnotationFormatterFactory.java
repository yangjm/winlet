package com.aggrepoint.winlet.format;

import java.util.HashSet;
import java.util.Set;

import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

public class NoEncodingFormatAnnotationFormatterFactory implements AnnotationFormatterFactory<NoEncodingFormat> {
	@Override
	public Set<Class<?>> getFieldTypes() {
		HashSet<Class<?>> set = new HashSet<Class<?>>();
		set.add(String.class);
		return set;
	}

	@Override
	public Printer<?> getPrinter(NoEncodingFormat annotation, Class<?> fieldType) {
		return new NoEncodingFormatter();
	}

	@Override
	public Parser<?> getParser(NoEncodingFormat annotation, Class<?> fieldType) {
		return new NoEncodingFormatter();
	}
}
