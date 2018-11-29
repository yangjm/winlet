package com.aggrepoint.dao.spring.boot;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import com.aggrepoint.service.ServiceClassLoader;

/**
 * Enable ServiceClassLoader in Spring Boot application.
 * 
 * @author jiangmingyang
 */
public class DaoContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
	@Override
	public void initialize(ConfigurableApplicationContext context) {
		if (context instanceof DefaultResourceLoader) {
			((DefaultResourceLoader) context).setClassLoader(new ServiceClassLoader(context.getClassLoader()));
		}
	}
}