package com.aggrepoint.winlet.spring.def;

import java.lang.reflect.Method;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class BaseDef extends ReturnDefList {
	private String name;
	private WinletDef winletDef;
	private Method method;

	public BaseDef(String name, WinletDef def, Method method) {
		super(method);

		this.name = name;
		winletDef = def;
		this.method = method;
	}

	public String getName() {
		return name;
	}

	public Method getMethod() {
		return method;
	}

	public WinletDef getWinletDef() {
		return winletDef;
	}
}
