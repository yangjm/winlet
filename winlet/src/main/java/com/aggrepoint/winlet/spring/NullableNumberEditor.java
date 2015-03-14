package com.aggrepoint.winlet.spring;

import java.text.NumberFormat;

import org.springframework.beans.propertyeditors.CustomNumberEditor;

/**
 * Treat NULL or empty string as zero
 * 
 * @author Jim
 */
public class NullableNumberEditor extends CustomNumberEditor {
	public NullableNumberEditor(Class<? extends Number> numberClass,
			NumberFormat numberFormat, boolean allowEmpty)
			throws IllegalArgumentException {
		super(numberClass, numberFormat, allowEmpty);
	}

	public NullableNumberEditor(Class<? extends Number> numberClass,
			boolean allowEmpty) throws IllegalArgumentException {
		super(numberClass, allowEmpty);
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		super.setAsText(text == null || text.equals("") ? "0" : text);
	}
}
