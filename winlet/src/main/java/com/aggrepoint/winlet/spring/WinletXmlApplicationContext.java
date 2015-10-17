package com.aggrepoint.winlet.spring;

import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Used by WinletDispatcherServlet as context class, to enable WinletClassLoader
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletXmlApplicationContext extends XmlWebApplicationContext {
	private WinletClassLoader classLoader;

	/**
	 * 容器加载bean时处理Winlet相关的注解
	 */
	@Override
	public ClassLoader getClassLoader() {
		if (classLoader == null) {
			if (!(super.getClassLoader() instanceof WinletClassLoader))
				classLoader = new WinletClassLoader(getParent()
						.getClassLoader());
		}

		return classLoader;
	}
}
