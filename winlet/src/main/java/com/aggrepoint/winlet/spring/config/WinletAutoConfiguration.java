package com.aggrepoint.winlet.spring.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.aggrepoint.winlet.spring.WinletDispatcherServlet;
import com.aggrepoint.winlet.spring.WinletHandlerMethodArgumentResolver;
import com.aggrepoint.winlet.spring.WinletRequestMappingHandlerMapping;

/**
 * Spring Boot configuration
 * 
 * @author jiangmingyang
 *
 */
@Configuration
@ComponentScan("com.aggrepoint.winlet.site")
public class WinletAutoConfiguration implements WebMvcConfigurer {
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		return new WinletRequestMappingHandlerMapping();
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(new WinletHandlerMethodArgumentResolver());
	}

	@Bean
	public InternalResourceViewResolver jspResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setViewNames("/WEB-INF/*.jsp");
		resolver.setOrder(100);
		return resolver;
	}

	@Bean
	public InternalResourceViewResolver winletViewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".jsp");
		resolver.setOrder(110);
		return resolver;
	}

	@Bean
	public DispatcherServlet dispatcherServlet() {
		return new WinletDispatcherServlet();
	}
}
