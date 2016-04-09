package com.aggrepoint.winlet.spring;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspApplicationContext;
import javax.servlet.jsp.JspFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.support.WebBindingInitializer;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.aggrepoint.dao.UserContext;
import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.AuthorizationEngine;
import com.aggrepoint.winlet.ConfigProvider;
import com.aggrepoint.winlet.Context;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ListProvider;
import com.aggrepoint.winlet.LogInfoImpl;
import com.aggrepoint.winlet.PsnRuleEngine;
import com.aggrepoint.winlet.ReqConst;
import com.aggrepoint.winlet.RequestLogger;
import com.aggrepoint.winlet.UserEngine;
import com.aggrepoint.winlet.form.FormImpl;
import com.aggrepoint.winlet.jsp.Resolver;
import com.aggrepoint.winlet.plugin.DefaultAccessRuleEngine;
import com.aggrepoint.winlet.plugin.AccessRuleAuthorizationEngine;
import com.aggrepoint.winlet.plugin.DefaultConfigProvider;
import com.aggrepoint.winlet.plugin.DefaultListProvider;
import com.aggrepoint.winlet.plugin.DefaultPsnRuleEngine;
import com.aggrepoint.winlet.plugin.DefaultRequestLogger;
import com.aggrepoint.winlet.plugin.DefaultUserEngine;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletDispatcherServlet extends DispatcherServlet {
	private static final long serialVersionUID = 1L;
	Map<String, RequestLogger> loggers;
	UserEngine userEngine;
	AuthorizationEngine authEngine;
	AccessRuleEngine accessRuleEngine;
	PsnRuleEngine psnRuleEngine;
	ConfigProvider configProvider;
	ListProvider listProvider;

	public WinletDispatcherServlet() {
		this.setContextClass(WinletXmlApplicationContext.class);
	}

	protected void initStrategies(ApplicationContext context) {
		super.initStrategies(context);

		Context.set(context);

		loggers = context.getBeansOfType(RequestLogger.class);
		if (loggers.size() == 0)
			loggers.put(DefaultRequestLogger.class.getName(),
					new DefaultRequestLogger());

		try {
			userEngine = context.getBean(UserEngine.class);
		} catch (Exception e) {
		}
		if (userEngine == null)
			userEngine = new DefaultUserEngine();

		try {
			authEngine = context
					.getBean(AuthorizationEngine.class);
		} catch (Exception e) {
		}
		if (authEngine == null)
			authEngine = new AccessRuleAuthorizationEngine();

		try {
			accessRuleEngine = context.getBean(AccessRuleEngine.class);
		} catch (Exception e) {
		}
		if (accessRuleEngine == null)
			accessRuleEngine = new DefaultAccessRuleEngine();

		try {
			psnRuleEngine = context.getBean(PsnRuleEngine.class);
		} catch (Exception e) {
		}
		if (psnRuleEngine == null)
			psnRuleEngine = new DefaultPsnRuleEngine();

		try {
			configProvider = context.getBean(ConfigProvider.class);
		} catch (Exception e) {
		}
		if (configProvider == null)
			configProvider = new DefaultConfigProvider();

		try {
			listProvider = context.getBean(ListProvider.class);
		} catch (Exception e) {
		}
		if (listProvider == null)
			listProvider = new DefaultListProvider();

		// { 启用Resolver
		ServletContext ctx = this.getServletContext();

		JspApplicationContext jspContext = JspFactory.getDefaultFactory()
				.getJspApplicationContext(ctx);
		jspContext.addELResolver(new Resolver());
		// }

		// { 把Spring MVC的Binding Errors合并到Form中
		RequestMappingHandlerAdapter adapter = context
				.getBean(RequestMappingHandlerAdapter.class);
		if (adapter != null) {
			WebBindingInitializer initializer = adapter
					.getWebBindingInitializer();
			adapter.setWebBindingInitializer((binder, request) -> {
				initializer.initBinder(binder, request);
				((FormImpl) ContextUtils.getReqInfo().getForm())
						.addBinder(binder);
			});
		}
		// }
	}

	protected View resolveViewName(String viewName, Map<String, Object> model,
			Locale locale, HttpServletRequest request) throws Exception {
		View view = super.resolveViewName(viewName, model, locale, request);
		LogInfoImpl.getLogInfo(request, null).setView(view);
		return view;
	}

	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ContextUtils.setDispatcher(req, this);
		ContextUtils.setApplicationContext(req, getWebApplicationContext());
		ContextUtils.setUserEngine(req, userEngine);
		ContextUtils.setAuthorizationEngine(req, authEngine);
		ContextUtils.setAccessRuleEngine(req, accessRuleEngine);
		ContextUtils.setPsnRuleEngine(req, psnRuleEngine);
		ContextUtils.setConfigProvider(req, configProvider);
		ContextUtils.setListProvider(req, listProvider);
		UserContext.setUser(userEngine.getUser(req).getLoginId());

		LogInfoImpl li = LogInfoImpl.getLogInfo(req, resp);

		try {
			super.service(req, resp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			li.complete();
			for (RequestLogger rl : loggers.values())
				rl.log(li);
		}
	}

	public Map<String, Object> runHandler(HttpServletRequest req,
			HttpServletResponse resp, String pageUrl, String url)
			throws Exception {
		Map<String, String> params = new HashMap<String, String>();
		if (pageUrl != null)
			params.put(ReqConst.PARAM_PAGE_URL, pageUrl);

		WinletRequestWrapper wreq = new WinletRequestWrapper(req, null, params,
				null);
		wreq.setServletPath(url);

		HandlerExecutionChain mappedHandler = getHandler(wreq);
		ModelAndView mv = getHandlerAdapter(mappedHandler.getHandler()).handle(
				wreq, resp, mappedHandler.getHandler());
		return mv.getModel();
	}
}
