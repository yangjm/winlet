package com.aggrepoint.winlet.utils;

/**
 * 与BeanProperty相关的异常
 * 
 * Creation date: (2002-11-27)
 * 
 * @author: Yang Jiang Ming
 */
public class BeanPropertyException extends Exception {
	static final long serialVersionUID = 0;

	public BeanPropertyException() {
		super();
	}

	public BeanPropertyException(Exception e) {
		super(e);
	}

	public BeanPropertyException(String s) {
		super(s);
	}

	public BeanPropertyException(String source, String exception) {
		super(source + ": " + exception);
	}
}