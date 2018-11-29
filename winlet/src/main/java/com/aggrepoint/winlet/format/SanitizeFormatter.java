package com.aggrepoint.winlet.format;

import java.text.ParseException;
import java.util.Locale;

import org.springframework.format.Formatter;

public class SanitizeFormatter implements Formatter<String> {
	public static String format(String object) {
		return HtmlSanitizeUtil.sanitize(object);
	}

	@Override
	public String print(String object, Locale locale) {
		return format(object);
	}

	@Override
	public String parse(String text, Locale locale) throws ParseException {
		return format(text);
	}
}
