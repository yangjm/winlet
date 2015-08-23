package com.aggrepoint.winlet.spring.boot;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

import com.aggrepoint.service.ServiceClassLoader;
import com.aggrepoint.winlet.spring.WinletClassLoader;

/**
 * Enable WinletClassLoader and ServiceClassLoader for application context
 * 
 * @author jiangmingyang
 *
 */
public class WinletContextInitializer implements
		ApplicationContextInitializer<ConfigurableApplicationContext> {

	@Override
	public void initialize(ConfigurableApplicationContext context) {
		if (context instanceof DefaultResourceLoader) {
			((DefaultResourceLoader) context)
					.setClassLoader(new ServiceClassLoader(
							new WinletClassLoader(context.getClassLoader())));
		}
	}
}
