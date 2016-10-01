package com.aggrepoint.winlet.spring;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

import javax.servlet.http.HttpServletRequest;

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
import com.aggrepoint.winlet.spring.annotation.DateParameter;
import com.aggrepoint.winlet.spring.annotation.IntegerParameter;
import com.aggrepoint.winlet.spring.annotation.PageRefresh;
import com.aggrepoint.winlet.spring.annotation.PageStorageAttr;
import com.aggrepoint.winlet.spring.annotation.StringParameter;

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
				|| parameter.getParameterAnnotation(PageStorageAttr.class) != null
				|| parameter.getParameterAnnotation(StringParameter.class) != null
				|| parameter.getParameterAnnotation(IntegerParameter.class) != null
				|| parameter.getParameterAnnotation(DateParameter.class) != null)
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

	static final HashMap<String, SimpleDateFormat> SDFS = new HashMap<String, SimpleDateFormat>();

	private SimpleDateFormat getSDF(String format) {
		if (StringUtils.isEmpty(format))
			return null;
		SimpleDateFormat sdf = SDFS.get(format);
		if (sdf == null) {
			sdf = new SimpleDateFormat(format);
			SDFS.put(format, sdf);
		}
		return sdf;
	}

	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		Class<?> clz = parameter.getParameterType();

		StringParameter rs = parameter
				.getParameterAnnotation(StringParameter.class);
		if (rs != null) {
			String val = webRequest.getParameter(rs.value());
			if (val != null && rs.options() != null)
				for (String str : rs.options()) {
					if (rs.caseInsensitive() && val.equalsIgnoreCase(str)
							|| !rs.caseInsensitive() && val.equals(str))
						return str;
				}

			return StringUtils.isEmpty(rs.def()) && rs.options() != null
					&& rs.options().length > 0 ? rs.options()[0] : rs.def();
		}

		IntegerParameter ri = parameter
				.getParameterAnnotation(IntegerParameter.class);
		if (ri != null) {
			int val = 0;

			try {
				val = Integer.parseInt(webRequest.getParameter(ri.value()));
			} catch (Exception e) {
				return ri.def();
			}

			if (val < ri.min() || val > ri.max())
				return ri.def();
			return val;
		}

		DateParameter rd = parameter
				.getParameterAnnotation(DateParameter.class);
		if (rd != null) {
			Date val = null;

			try {
				val = getSDF(rd.format()).parse(
						webRequest.getParameter(rd.value()));
			} catch (Exception e) {
				return null;
			}

			return val;
		}

		// 转换为参数所需格式。参考了AbstractNamedValueMethodArgumentResolver.resolveArgument中的实现
		Function<Object, Object> convert = (arg) -> {
			if (binderFactory != null) {
				Class<?> paramType = parameter.getParameterType();
				try {
					WebDataBinder binder = binderFactory.createBinder(
							webRequest, null, parameter.getParameterName());
					return binder.convertIfNecessary(arg, paramType, parameter);
				} catch (TypeMismatchException ex) {
					throw new MethodArgumentTypeMismatchException(arg,
							ex.getRequiredType(), parameter.getParameterName(),
							parameter, ex.getCause());
				} catch (Exception ex) {
					throw new MethodArgumentConversionNotSupportedException(
							arg, paramType, parameter.getParameterName(),
							parameter, ex.getCause());
				}
			}
			return arg;
		};

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

			arg = convert.apply(arg);

			if (arg == null && attr.createIfNotExist())
				arg = clz.newInstance();

			ps.setAttribute(attr.value(), arg);

			return arg;
		}

		HttpServletRequest req = ContextUtils.getRequest();

		Cfg cfg = parameter.getParameterAnnotation(Cfg.class);
		if (cfg != null) {
			String value = ContextUtils.getConfigProvider(req).getStr(
					cfg.value());
			if (value == null)
				value = cfg.def();
			return convert.apply(value);
		}

		if (clz == Boolean.class || clz == boolean.class) {
			if (parameter.getParameterAnnotation(PageRefresh.class) != null)
				return ContextUtils.getReqInfo().isPageRefresh();
			AccessRule rule = parameter
					.getParameterAnnotation(AccessRule.class);
			if (rule != null)
				return ContextUtils.getAccessRuleEngine(
						ContextUtils.getRequest()).eval(rule.value());
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

		return null;
	}
}
