package com.aggrepoint.winlet.spring;

import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.aggrepoint.utils.StringUtils;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.LogInfoImpl;
import com.aggrepoint.winlet.ReqInfoImpl;
import com.aggrepoint.winlet.RespHeaderConst;
import com.aggrepoint.winlet.form.FormImpl;
import com.aggrepoint.winlet.spring.annotation.AccessRule;
import com.aggrepoint.winlet.spring.annotation.Action;
import com.aggrepoint.winlet.spring.annotation.Unspecified;
import com.aggrepoint.winlet.spring.annotation.Window;
import com.aggrepoint.winlet.spring.def.ControllerMethodDef;
import com.aggrepoint.winlet.spring.def.ReturnDef;
import com.aggrepoint.winlet.spring.def.WinletDef;

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
	static final String WINLET_FORM_RESP = "WINLET_FORM_RESP:";

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

			WinletDef def = WinletDef.getDef(hm.getBeanType());
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

			WinletDef def = WinletDef.getDef(hm.getBeanType());
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

			Window win = null;
			Action action = null;

			win = AnnotationUtils.findAnnotation(hm.getMethod(), Window.class);
			if (win != null)
				rd = PsnReturnDefFinder.getReturnDef(def.getWindow(win.value())
						.getReturnDef(viewName));
			else {
				action = AnnotationUtils.findAnnotation(hm.getMethod(),
						Action.class);
				if (action != null)
					rd = PsnReturnDefFinder.getReturnDef(def.getAction(
							action.value()).getReturnDef(viewName));
			}

			if (win == null && action == null) // 被调用的既不是Window也不是Action
				return;

			ReqInfoImpl reqInfo = ContextUtils.getReqInfo();
			boolean cache = false;

			if (rd != null) {
				li.setReturnDef(rd);

				FormImpl form = (FormImpl) reqInfo.getForm();
				reqInfo.setReturnDef(rd);

				if (action != null && reqInfo.isValidateField()) {
					// 表单字段校验，返回校验结果
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
						response.setHeader(RespHeaderConst.HEADER_UPDATE,
								rd.getUpdate());
					}

					if (rd.getTarget() != null) {
						response.setHeader(RespHeaderConst.HEADER_TARGET,
								rd.getTarget());
					}

					if (rd.cache()) {
						response.setHeader(RespHeaderConst.HEADER_CACHE, "yes");
						cache = true;
					}

					if (rd.getMsg() != null && !"".equals(rd.getMsg()))
						response.setHeader(RespHeaderConst.HEADER_MSG,
								URLEncoder.encode(rd.getMsg(), "UTF-8"));

					if (rd.isDialog())
						response.setHeader(RespHeaderConst.HEADER_DIALOG, "yes");
					else {
						if (!viewName.startsWith(Const.REDIRECT)
								&& form.isValidateForm() && form.hasError()) { // 表单校验出错
							response.getOutputStream().write(
									(WINLET_FORM_RESP + StringUtils
											.fixJson(form.getJsonChanges()))
											.getBytes("UTF-8"));

							if (modelAndView != null)
								modelAndView.clear();
							return;
						}
					}
				}
			}

			if (modelAndView != null && modelAndView.getViewName() != null) {
				if (modelAndView.getViewName().startsWith(Const.REDIRECT)) {
					response.setHeader(RespHeaderConst.HEADER_REDIRECT,
							modelAndView.getViewName().substring(9));
					modelAndView.clear();
					return;
				}

				if (!"".equals(modelAndView.getViewName())) {
					if (modelAndView.getViewName().indexOf("/") != 0) {
						modelAndView.setViewName(def.getViewPath() + "/"
								+ modelAndView.getViewName());
						return;
					}
				}
			}

			if (modelAndView != null) {
				if (!reqInfo.isFromContainer()
						&& !cache
						&& (modelAndView.getViewName() == null || ""
								.equals(modelAndView.getViewName()))
						&& action != null)
					response.getOutputStream().write(
							reqInfo.getWindowContent(null, null, null,
									modelAndView.getModel(), null).getBytes(
									"UTF-8"));

				if ("".equals(modelAndView.getViewName()))
					modelAndView.clear();
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
