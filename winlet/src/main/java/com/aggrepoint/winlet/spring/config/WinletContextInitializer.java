package com.aggrepoint.winlet.spring.config;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import com.aggrepoint.winlet.spring.WinletClassLoader;

/**
 * Enable WinletClassLoader in Spring Boot application.
 * 
 * @author jiangmingyang
 */
public class WinletContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Override
	public void initialize(ConfigurableApplicationContext context) {
		if (context instanceof DefaultResourceLoader) {
			((DefaultResourceLoader) context).setClassLoader(new WinletClassLoader(context.getClassLoader()));
		}
	}
}
