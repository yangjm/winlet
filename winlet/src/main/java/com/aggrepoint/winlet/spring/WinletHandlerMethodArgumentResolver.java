package com.aggrepoint.winlet.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.ListProvider;
import com.aggrepoint.winlet.ConfigProvider;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.PageStorage;
import com.aggrepoint.winlet.PsnRuleEngine;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.ReqInfoImpl;
import com.aggrepoint.winlet.SharedPageStorage;
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
				|| clz.isAssignableFrom(PageStorage.class)
				|| clz.isAssignableFrom(SharedPageStorage.class)
				|| clz.isAssignableFrom(Form.class)
				|| UserProfile.class.isAssignableFrom(clz)
				|| clz.isAssignableFrom(UserEngine.class)
				|| clz.isAssignableFrom(ConfigProvider.class)
				|| clz.isAssignableFrom(PsnRuleEngine.class)
				|| clz.isAssignableFrom(AccessRuleEngine.class)
				|| clz.isAssignableFrom(ListProvider.class)
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

		if (clz.isAssignableFrom(PageStorage.class)) {
			ReqInfo reqInfo = ContextUtils.getReqInfo();
			PageStorage ps = reqInfo.getPageStorage();
			if (reqInfo.isPageRefresh())
				ps.refresh();
			return ps;
		}

		if (clz.isAssignableFrom(SharedPageStorage.class)) {
			ReqInfo reqInfo = ContextUtils.getReqInfo();
			SharedPageStorage sps = reqInfo.getSharedPageStorage();
			if (reqInfo.isPageRefresh())
				sps.refresh();
			return sps;
		}

		HttpServletRequest req = ContextUtils.getRequest();

		if (UserProfile.class.isAssignableFrom(clz))
			return ContextUtils.getUserEngine(req).getUser(req);

		if (clz.isAssignableFrom(UserEngine.class))
			return ContextUtils.getUserEngine(req);

		if (clz.isAssignableFrom(ConfigProvider.class))
			return ContextUtils.getConfigProvider(req);

		if (clz.isAssignableFrom(AccessRuleEngine.class))
			return ContextUtils.getAccessRuleEngine(req);

		if (clz.isAssignableFrom(PsnRuleEngine.class))
			return ContextUtils.getPsnRuleEngine(req);

		if (clz.isAssignableFrom(ListProvider.class))
			return ContextUtils.getListProvider(req);

		Cfg cfg = parameter.getParameterAnnotation(Cfg.class);
		if (cfg != null) {
			String value = ContextUtils.getConfigProvider(req).getStr(
					cfg.value());
			if (value == null)
				value = cfg.def();
			return value;
		}

		Storage s = parameter.getParameterAnnotation(Storage.class);
		if (s != null)
			return ContextUtils.getReqInfo().getPageStorage()
					.getAttribute(s.value());

		return null;
	}
}
