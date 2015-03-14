package com.aggrepoint.winlet.spring;

import javax.servlet.ServletContext;

import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import com.aggrepoint.service.ServiceClassLoader;

public class WinletContextLoaderListener extends ContextLoaderListener {
	protected WebApplicationContext createWebApplicationContext(
			ServletContext sc) {
		WebApplicationContext context = super.createWebApplicationContext(sc);
		((XmlWebApplicationContext) context)
				.setClassLoader(new ServiceClassLoader(context.getClassLoader()));
		return context;
	}
}
