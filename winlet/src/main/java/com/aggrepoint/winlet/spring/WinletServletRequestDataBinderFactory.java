package com.aggrepoint.winlet.spring;

import java.util.List;

import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.form.FormImpl;

/**
 * For merging Spring binding result to Winlet form before invoking handler
 * method
 */
public class WinletServletRequestDataBinderFactory extends
		ServletRequestDataBinderFactory {
	public WinletServletRequestDataBinderFactory(
			List<InvocableHandlerMethod> binderMethods,
			WebBindingInitializer initializer) {
		super(binderMethods, initializer);
	}

	@Override
	protected ServletRequestDataBinder createBinderInstance(Object target,
			String objectName, NativeWebRequest request) {
		ServletRequestDataBinder binder = super.createBinderInstance(target,
				objectName, request);
		((FormImpl) ContextUtils.getReqInfo().getForm()).addBinder(binder);
		return binder;
	}
}
