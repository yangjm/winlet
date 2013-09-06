package com.aggrepoint.dao;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class FunctionNotFoundException extends Exception {
	private static final long serialVersionUID = 1L;

	String name;

	protected FunctionNotFoundException(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
