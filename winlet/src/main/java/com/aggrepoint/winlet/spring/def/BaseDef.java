package com.aggrepoint.winlet.spring.def;

import java.lang.reflect.Method;

import org.springframework.core.annotation.AnnotationUtils;

import com.aggrepoint.winlet.spring.annotation.AccessRule;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class BaseDef extends ReturnDefList {
	private String name;
	private String accessRule;
	private WinletDef winletDef;
	private Method method;

	public BaseDef(String name, WinletDef def, Method method) {
		super(method);

		this.name = name;
		winletDef = def;
		this.method = method;

		AccessRule ar = AnnotationUtils
				.findAnnotation(method, AccessRule.class);
		if (ar != null)
			accessRule = ar.value();
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

	public String getAccessRule() {
		return accessRule;
	}
}
