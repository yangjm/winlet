package com.aggrepoint.dao.spring.boot;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.aggrepoint.dao.DaoScannerConfigurer;

@Configuration
@ConditionalOnMissingBean(DaoScannerConfigurer.class)
public class DaoScanAutoConfiguration implements ApplicationContextAware {
	ApplicationContext context;

	@Bean
	public DaoScannerConfigurer getScannerConfigurer() {
		// Try to use @EnableAutoConfiguration base packages
		List<String> basePackages = null;
		try {
			basePackages = AutoConfigurationPackages.get(context);
		} catch (Exception e) {
		}
		if (basePackages == null || basePackages.size() == 0)
			return null;

		DaoScannerConfigurer cfg = new DaoScannerConfigurer();
		cfg.setBasePackage(basePackages.stream().collect(
				Collectors.joining(",")));
		return cfg;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		context = applicationContext;
	}
}
