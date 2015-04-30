package com.aggrepoint.winlet.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.ConfigProvider;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ListProvider;
import com.aggrepoint.winlet.PsnRuleEngine;
import com.aggrepoint.winlet.ReqInfoImpl;
import com.aggrepoint.winlet.UserEngine;
import com.aggrepoint.winlet.UserProfile;
import com.aggrepoint.winlet.form.Form;
import com.aggrepoint.winlet.form.Validation;
import com.aggrepoint.winlet.form.ValidationImpl;
import com.aggrepoint.winlet.spring.annotation.Cfg;
import com.aggrepoint.winlet.spring.annotation.PageRefresh;
import com.aggrepoint.winlet.spring.annotation.Storage;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletHandlerMethodArgumentResolver implements
		HandlerMethodArgumentResolver {
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		Class<?> clz = parameter.getParameterType();

		if (clz.isAssignableFrom(Validation.class)
				|| clz.isAssignableFrom(ReqInfoImpl.class)
				|| clz.isAssignableFrom(Form.class)
				|| UserProfile.class.isAssignableFrom(clz)
				|| UserEngine.class.isAssignableFrom(clz)
				|| ConfigProvider.class.isAssignableFrom(clz)
				|| PsnRuleEngine.class.isAssignableFrom(clz)
				|| AccessRuleEngine.class.isAssignableFrom(clz)
				|| ListProvider.class.isAssignableFrom(clz)
				|| parameter.getParameterAnnotation(Cfg.class) != null
				|| parameter.getParameterAnnotation(Storage.class) != null)
			return true;

		if (clz == Boolean.class || clz == boolean.class)
			if (parameter.getParameterAnnotation(PageRefresh.class) != null)
				return true;

		return false;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		Class<?> clz = parameter.getParameterType();

		if (clz == Boolean.class || clz == boolean.class)
			return ContextUtils.getReqInfo().isPageRefresh();

		if (clz.isAssignableFrom(Validation.class))
			return new ValidationImpl(ContextUtils.getReqInfo());

		if (clz.isAssignableFrom(ReqInfoImpl.class))
			return ContextUtils.getReqInfo();

		if (clz.isAssignableFrom(Form.class))
			return ContextUtils.getReqInfo().getForm();

		HttpServletRequest req = ContextUtils.getRequest();

		if (UserProfile.class.isAssignableFrom(clz))
			return ContextUtils.getUserEngine(req).getUser(req);

		if (UserEngine.class.isAssignableFrom(clz))
			return ContextUtils.getUserEngine(req);

		if (ConfigProvider.class.isAssignableFrom(clz))
			return ContextUtils.getConfigProvider(req);

		if (AccessRuleEngine.class.isAssignableFrom(clz))
			return ContextUtils.getAccessRuleEngine(req);

		if (PsnRuleEngine.class.isAssignableFrom(clz))
			return ContextUtils.getPsnRuleEngine(req);

		if (ListProvider.class.isAssignableFrom(clz))
			return ContextUtils.getListProvider(req);

		Cfg cfg = parameter.getParameterAnnotation(Cfg.class);
		if (cfg != null) {
			String value = ContextUtils.getConfigProvider(req).getStr(
					cfg.value());
			if (value == null)
				value = cfg.def();
			return value;
		}

		return null;
	}
}
