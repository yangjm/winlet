package com.aggrepoint.winlet.spring;

import java.lang.reflect.Method;

/**
 * 配合实现客户端发起的、将view名称翻译为对应wid的请求
 * 
 * @author jiangmingyang
 */
public class TranslateUpdate {
	private static TranslateUpdate instance;
	private static Method method;

	public static TranslateUpdate getInstance() {
		if (instance == null)
			instance = new TranslateUpdate();
		return instance;
	}

	public static Method getMethod() {
		if (method == null) {
			try {
				method = TranslateUpdate.class.getMethod("dummy");
			} catch (Exception e) {
			}
		}

		return method;
	}

	public void dummy() {
	}
}
