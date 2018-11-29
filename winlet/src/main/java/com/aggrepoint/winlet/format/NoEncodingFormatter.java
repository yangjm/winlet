package com.aggrepoint.winlet.format;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

public class NoEncodingFormatter implements Formatter<String> {
	@Override
	public String print(String object, Locale locale) {
		return object;
	}

	@Override
	public String parse(String text, Locale locale) throws ParseException {
		return text;
	}
}
