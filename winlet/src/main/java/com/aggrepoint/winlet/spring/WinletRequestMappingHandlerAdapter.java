package com.aggrepoint.winlet.spring;

import java.util.List;

import org.springframework.web.method.annotation.InitBinderDataBinderFactory;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * For merging Spring binding result to Winlet form before invoking handler
 * method
 * 
 * @author Jim Yang
 */
public class WinletRequestMappingHandlerAdapter extends
		RequestMappingHandlerAdapter {
	protected InitBinderDataBinderFactory createDataBinderFactory(
			List<InvocableHandlerMethod> binderMethods) throws Exception {
		return new WinletServletRequestDataBinderFactory(binderMethods,
				getWebBindingInitializer());
	}
}
