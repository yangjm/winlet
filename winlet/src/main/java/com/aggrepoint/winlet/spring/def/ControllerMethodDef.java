package com.aggrepoint.winlet.spring.def;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * 普通Spring MVC控制器方法的定义
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ControllerMethodDef extends ReturnDefList {
	public ControllerMethodDef(Method method) {
		super(method);
	}

	private static HashMap<Method, ControllerMethodDef> htDefs = new HashMap<Method, ControllerMethodDef>();

	public static ControllerMethodDef getDef(Method m) {
		if (htDefs.containsKey(m))
			return htDefs.get(m);

		ControllerMethodDef def = new ControllerMethodDef(m);
		htDefs.put(m, def);

		return def;
	}
}
