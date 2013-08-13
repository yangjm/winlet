package com.aggrepoint.winlet.spring;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.LogInfoImpl;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.ReqInfoImpl;
import com.aggrepoint.winlet.RespHeaderConst;
import com.aggrepoint.winlet.form.FormImpl;
import com.aggrepoint.winlet.spring.annotation.AccessRule;
import com.aggrepoint.winlet.spring.annotation.Action;
import com.aggrepoint.winlet.spring.annotation.Unspecified;
import com.aggrepoint.winlet.spring.annotation.Window;
import com.aggrepoint.winlet.spring.def.ControllerMethodDef;
import com.aggrepoint.winlet.spring.def.ReturnDef;
import com.aggrepoint.winlet.spring.def.WidgetDef;
import com.icebean.core.common.StringUtils;

/**
 * <pre>
 * Winlet的View和Action方法的返回处理
 * 
 * 1. 如果返回值为空，则不处理
 * 
 * </pre>
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletHandlerInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		LogInfoImpl.getLogInfo(request, response).setHandler(handler);

		// 表单处理
		if (handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) handler;

			AccessRule rule = AccessRuleChecker.evalRule(hm.getBeanType());
			if (rule == null)
				rule = AccessRuleChecker.evalRule(hm.getMethod());
			if (rule != null) {
				if (rule.exception() == Unspecified.class)
					return false;
				throw rule.exception().newInstance();
			}

			WidgetDef def = WidgetDef.getDef(hm.getBeanType());
			if (def == null) // 不是Winlet
				return true;
			Action action = AnnotationUtils.findAnnotation(hm.getMethod(),
					Action.class); // 不是Action
			if (action == null)
				return true;

			ReqInfoImpl ri = ContextUtils.getReqInfo();
			FormImpl form = ((FormImpl) ri.getForm());
			form.validate(ri, hm.getBean(), hm.getMethod());
		}

		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		LogInfoImpl li = LogInfoImpl.getLogInfo(request, response)
				.setHandler(handler).setModelAndView(modelAndView);

		if (handler instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) handler;

			String viewName = null;
			if (modelAndView == null)
				viewName = "";
			else
				viewName = modelAndView.getViewName();

			if (viewName == null) // 直接指定了View对象而不是ViewName，不是Winlet的开发方式，不加处理
				return;

			ReturnDef rd = null;

			WidgetDef def = WidgetDef.getDef(hm.getBeanType());
			if (def == null) { // 不是Winlet
				// 处理普通Spring MVC Controller方法上定义的@Return
				ControllerMethodDef mdef = ControllerMethodDef.getDef(hm
						.getMethod());
				rd = PsnReturnDefFinder.getReturnDef(mdef
						.getReturnDef(viewName));
				if (rd != null) {
					li.setReturnDef(rd);

					if (rd.getViewName() != null && modelAndView != null)
						modelAndView.setViewName(rd.getViewName());
				}
				return;
			}

			Window view = null;
			Action action = null;

			view = AnnotationUtils.findAnnotation(hm.getMethod(), Window.class);
			if (view != null)
				rd = PsnReturnDefFinder.getReturnDef(def.getView(view.value())
						.getReturnDef(viewName));
			else {
				action = AnnotationUtils.findAnnotation(hm.getMethod(),
						Action.class);
				if (action != null)
					rd = PsnReturnDefFinder.getReturnDef(def.getAction(
							action.value()).getReturnDef(viewName));
			}

			if (view == null && action == null)
				return;

			if (rd != null) {
				li.setReturnDef(rd);

				ReqInfoImpl reqInfo = ContextUtils.getReqInfo();
				FormImpl form = (FormImpl) reqInfo.getForm();
				reqInfo.setReturnDef(rd);

				if (action != null && reqInfo.isValidateField()) {
					response.setHeader("Content-Type",
							"application/json; charset=UTF-8");
					response.getOutputStream().write(
							StringUtils.fixJson(form.getJsonChanges())
									.getBytes("UTF-8"));

					if (modelAndView != null)
						modelAndView.clear();
					return;
				}

				if (rd.getViewName() != null)
					if (modelAndView != null)
						modelAndView.setViewName(rd.getViewName());

				if (rd.getTitle() != null)
					response.setHeader(RespHeaderConst.HEADER_TITLE,
							URLEncoder.encode(rd.getTitle(), "UTF-8"));

				if (action != null) {
					if (rd.getUpdate() != null) {
						ReqInfo ri = ContextUtils.getReqInfo();
						response.setHeader(
								RespHeaderConst.HEADER_UPDATE,
								ri.getViewInstance().translateUpdateViews(ri,
										rd.getUpdate()));
					}

					if (rd.cache())
						response.setHeader(RespHeaderConst.HEADER_CACHE, "yes");

					if (rd.isDialog())
						response.setHeader(RespHeaderConst.HEADER_DIALOG, "yes");
					else {
						if (modelAndView != null
								&& !viewName.startsWith(Const.REDIRECT)) {
							modelAndView.clear();

							if (form.isValidateForm() && form.hasError())
								response.getOutputStream().write(
										StringUtils.fixJson(
												form.getJsonChanges())
												.getBytes("UTF-8"));

							return;
						}
					}
				} else {
					if ("".equals(viewName)) {
						if (modelAndView != null)
							modelAndView.clear();
					}
				}
			} else {
				if (action != null || "".equals(viewName)) {
					if (modelAndView != null)
						modelAndView.clear();
				}
			}

			if (modelAndView != null && modelAndView.getViewName() != null) {
				if (modelAndView.getViewName().startsWith(Const.REDIRECT)) {
					response.setHeader(RespHeaderConst.HEADER_REDIRECT,
							modelAndView.getViewName().substring(9));
					modelAndView.clear();
					return;
				}

				modelAndView.setViewName(def.getName() + "/"
						+ modelAndView.getViewName());
			}
		}
	}

	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		LogInfoImpl.getLogInfo(request, response).setException(ex).complete();
	}
}
