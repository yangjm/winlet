package com.aggrepoint.winlet;

import org.springframework.context.ApplicationContext;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Context {
	private static ApplicationContext ctx;

	public static ApplicationContext get() {
		return ctx;
	}

	public static void set(ApplicationContext context) {
		ctx = context;
	}
}
