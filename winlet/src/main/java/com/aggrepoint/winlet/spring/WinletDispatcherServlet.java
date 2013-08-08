package com.aggrepoint.winlet.spring;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.View;

import com.aggrepoint.dao.UserContext;
import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.CodeMapProvider;
import com.aggrepoint.winlet.ConfigProvider;
import com.aggrepoint.winlet.Context;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.LogInfoImpl;
import com.aggrepoint.winlet.PsnRuleEngine;
import com.aggrepoint.winlet.RequestLogger;
import com.aggrepoint.winlet.UserEngine;
import com.aggrepoint.winlet.plugin.DefaultAccessRuleEngine;
import com.aggrepoint.winlet.plugin.DefaultCodeMapProvider;
import com.aggrepoint.winlet.plugin.DefaultConfigProvider;
import com.aggrepoint.winlet.plugin.DefaultPsnRuleEngine;
import com.aggrepoint.winlet.plugin.DefaultUserEngine;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletDispatcherServlet extends DispatcherServlet {
	private static final long serialVersionUID = 1L;
	Map<String, RequestLogger> loggers;
	UserEngine userEngine;
	AccessRuleEngine accessRuleEngine;
	PsnRuleEngine psnRuleEngine;
	ConfigProvider configProvider;
	CodeMapProvider codeTableProvider;

	public WinletDispatcherServlet() {
		this.setContextClass(WinletXmlApplicationContext.class);
	}

	protected void initStrategies(ApplicationContext context) {
		super.initStrategies(context);

		Context.set(context);

		loggers = context.getBeansOfType(RequestLogger.class);
		try {
			userEngine = context.getBean(UserEngine.class);
		} catch (Exception e) {
		}
		if (userEngine == null)
			userEngine = new DefaultUserEngine();

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
			codeTableProvider = context.getBean(CodeMapProvider.class);
		} catch (Exception e) {
		}
		if (codeTableProvider == null)
			codeTableProvider = new DefaultCodeMapProvider();
	}

	protected View resolveViewName(String viewName, Map<String, Object> model,
			Locale locale, HttpServletRequest request) throws Exception {
		View view = super.resolveViewName(viewName, model, locale, request);
		LogInfoImpl.getLogInfo(request, null).setView(view);
		return view;
	}

	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ContextUtils.setUserEngine(req, userEngine);
		ContextUtils.setAccessRuleEngine(req, accessRuleEngine);
		ContextUtils.setPsnRuleEngine(req, psnRuleEngine);
		ContextUtils.setConfigProvider(req, configProvider);
		ContextUtils.setCodeMapProvider(req, codeTableProvider);
		UserContext.setUser(userEngine.getUser(req).getLoginId());

		LogInfoImpl li = LogInfoImpl.getLogInfo(req, resp);

		try {
			super.service(req, resp);
		} finally {
			li.complete();
			for (RequestLogger rl : loggers.values())
				rl.log(li);
		}
	}
}
