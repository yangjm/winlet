package com.aggrepoint.winlet.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.MethodArgumentConversionNotSupportedException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.AuthorizationEngine;
import com.aggrepoint.winlet.ConfigProvider;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ListProvider;
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
import com.aggrepoint.winlet.spring.annotation.AccessRule;
import com.aggrepoint.winlet.spring.annotation.Cfg;
import com.aggrepoint.winlet.spring.annotation.PageRefresh;
import com.aggrepoint.winlet.spring.annotation.PageStorageAttr;

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
				|| UserEngine.class.isAssignableFrom(clz)
				|| ConfigProvider.class.isAssignableFrom(clz)
				|| PsnRuleEngine.class.isAssignableFrom(clz)
				|| AuthorizationEngine.class.isAssignableFrom(clz)
				|| AccessRuleEngine.class.isAssignableFrom(clz)
				|| ListProvider.class.isAssignableFrom(clz)
				|| parameter.getParameterAnnotation(Cfg.class) != null
				|| parameter.getParameterAnnotation(PageStorageAttr.class) != null)
			return true;

		if (clz == Boolean.class || clz == boolean.class)
			if (parameter.getParameterAnnotation(PageRefresh.class) != null
					|| parameter.getParameterAnnotation(AccessRule.class) != null)
				return true;

		return false;
	}

	Object checkClass(Object val, Class<?> expected, Exception e)
			throws Exception {
		if (expected.isAssignableFrom(val.getClass()))
			return val;
		if (e == null)
			return null;
		throw e;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		Class<?> clz = parameter.getParameterType();

		PageStorageAttr attr = parameter
				.getParameterAnnotation(PageStorageAttr.class);
		if (attr != null) {
			Object arg = null;

			if (!StringUtils.isEmpty(attr.reqparam())) { // 有定义请求参数，从请求参数中取值
				arg = webRequest.getParameter(attr.reqparam());
				if (StringUtils.isEmpty(arg))
					arg = null;
			}

			PageStorage ps = ContextUtils.getReqInfo().getPageStorage();

			if (arg == null) // 没有定义请求参数，或请求参数中没有值，从PageStorage中取
				arg = ps.getAttribute(attr.value());

			// 转换为参数所需格式。参考了AbstractNamedValueMethodArgumentResolver.resolveArgument中的实现
			if (binderFactory != null) {
				Class<?> paramType = parameter.getParameterType();
				WebDataBinder binder = binderFactory.createBinder(webRequest,
						null, parameter.getParameterName());
				try {
					arg = binder.convertIfNecessary(arg, paramType, parameter);
				} catch (ConversionNotSupportedException ex) {
					throw new MethodArgumentConversionNotSupportedException(
							arg, ex.getRequiredType(),
							parameter.getParameterName(), parameter,
							ex.getCause());
				} catch (TypeMismatchException ex) {
					throw new MethodArgumentTypeMismatchException(arg,
							ex.getRequiredType(), parameter.getParameterName(),
							parameter, ex.getCause());

				}
			}

			if (arg == null && attr.createIfNotExist())
				arg = clz.newInstance();

			ps.setAttribute(attr.value(), arg);

			return arg;
		}

		if (clz == Boolean.class || clz == boolean.class) {
			if (parameter.getParameterAnnotation(PageRefresh.class) != null)
				return ContextUtils.getReqInfo().isPageRefresh();
			AccessRule rule = parameter
					.getParameterAnnotation(AccessRule.class);
			if (rule != null)
				return ContextUtils.getAccessRuleEngine(
						ContextUtils.getRequest()).eval(rule.value());
			return false;
		}

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
			return checkClass(ContextUtils.getUserEngine(req).getUser(req),
					clz, null);

		if (UserEngine.class.isAssignableFrom(clz))
			return checkClass(ContextUtils.getUserEngine(req), clz, null);

		if (ConfigProvider.class.isAssignableFrom(clz))
			return checkClass(ContextUtils.getConfigProvider(req), clz, null);

		if (AuthorizationEngine.class.isAssignableFrom(clz))
			return checkClass(ContextUtils.getAuthorizationEngine(req), clz,
					null);

		if (AccessRuleEngine.class.isAssignableFrom(clz))
			return checkClass(ContextUtils.getAccessRuleEngine(req), clz, null);

		if (PsnRuleEngine.class.isAssignableFrom(clz))
			return checkClass(ContextUtils.getPsnRuleEngine(req), clz, null);

		if (ListProvider.class.isAssignableFrom(clz))
			return checkClass(ContextUtils.getListProvider(req), clz, null);

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
