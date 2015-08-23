package com.aggrepoint.winlet.spring.boot;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.PsnRuleEngine;
import com.aggrepoint.winlet.RequestLogger;
import com.aggrepoint.winlet.UserEngine;
import com.aggrepoint.winlet.plugin.DefaultAccessRuleEngine;
import com.aggrepoint.winlet.plugin.DefaultPsnRuleEngine;
import com.aggrepoint.winlet.plugin.DefaultRequestLogger;
import com.aggrepoint.winlet.plugin.DefaultUserEngine;
import com.aggrepoint.winlet.spring.WinletDispatcherServlet;
import com.aggrepoint.winlet.spring.WinletHandlerMethodArgumentResolver;
import com.aggrepoint.winlet.spring.WinletRequestMappingHandlerMapping;

@Configuration
@ComponentScan("com.aggrepoint.winlet.site")
public class WinletAutoConfiguration extends WebMvcConfigurerAdapter {
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		return new WinletRequestMappingHandlerMapping();
	}

	@Override
	public void addArgumentResolvers(
			List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new WinletHandlerMethodArgumentResolver());
	}

	@Bean
	public DispatcherServlet dispatcherServlet() {
		return new WinletDispatcherServlet();
	}

	@Bean
	public RequestLogger getRequestLogger() {
		return new DefaultRequestLogger();
	}

	@Bean
	public AccessRuleEngine getAccessRuleEngine() {
		return new DefaultAccessRuleEngine();
	}

	@Bean
	public PsnRuleEngine getPsnRuleEngine() {
		return new DefaultPsnRuleEngine();
	}

	@Bean
	public UserEngine getUserEngine() {
		return new DefaultUserEngine();
	}
}
