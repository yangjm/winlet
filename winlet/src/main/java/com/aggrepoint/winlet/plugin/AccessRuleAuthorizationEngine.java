package com.aggrepoint.winlet.plugin;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import com.aggrepoint.utils.StringUtils;
import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.AuthorizationEngine;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.site.domain.Area;
import com.aggrepoint.winlet.site.domain.Branch;
import com.aggrepoint.winlet.site.domain.Page;
import com.aggrepoint.winlet.spring.WinletClassLoader;
import com.aggrepoint.winlet.spring.annotation.AccessRule;
import com.aggrepoint.winlet.spring.annotation.Action;
import com.aggrepoint.winlet.spring.annotation.Unspecified;
import com.aggrepoint.winlet.spring.def.WinletDef;

/**
 * <pre>
 * 实现基于AccessRule的访问检查
 * AccessRule可以标注在非Winlet Controller类和方法上
 * check(path)仅适用于path指向Winlet方法的情况
 * </pre>
 * 
 * @author jiangmingyang
 */
public class AccessRuleAuthorizationEngine implements AuthorizationEngine {
	static AccessRuleEngine ruleEngine;
	static HashMap<Class<?>, AccessRule> rulesOnClass = new HashMap<Class<?>, AccessRule>();
	static HashMap<Method, AccessRule> rulesOnMethod = new HashMap<Method, AccessRule>();

	static final Log logger = LogFactory
			.getLog(AccessRuleAuthorizationEngine.class);

	private static AccessRule getRule(Class<?> c) {
		if (rulesOnClass.containsKey(c))
			return rulesOnClass.get(c);

		AccessRule ar = AnnotationUtils.findAnnotation(c, AccessRule.class);
		rulesOnClass.put(c, ar);

		return ar;
	}

	protected static AccessRule getRule(Method method) {
		if (rulesOnMethod.containsKey(method))
			return rulesOnMethod.get(method);

		AccessRule ar = AnnotationUtils
				.findAnnotation(method, AccessRule.class);
		rulesOnMethod.put(method, ar);

		return ar;
	}

	protected static AccessRuleEngine getRuleEngine() {
		if (ruleEngine == null) {
			ruleEngine = ContextUtils.getAccessRuleEngine(ContextUtils
					.getRequest());
		}
		return ruleEngine;
	}

	@Override
	public Class<? extends Exception> check(Branch branch) {
		if (branch.getRule() == null)
			return null;

		try {
			if (getRuleEngine().eval(branch.getRule()))
				return null;
		} catch (Exception e) {
			logger.error(
					"Error evaluating branch access rule \"" + branch.getRule()
							+ "\".", e);
		}

		return Unspecified.class;
	}

	@Override
	public Class<? extends Exception> check(Page page, boolean expand) {
		String rule = expand ? page.getExpandRule() : page.getRule();
boolean debug = false;
if ("/student/".equals(page.getFullPath())) {
	debug = true;
	System.out.println("=================================");
	System.out.print(rule);
	System.out.print(expand);
}
		if (rule == null) {
			if (!expand) {
				// 访问page本身，但page上没有定义访问规则。
				// 如果page中有area，并且area中有引用winlet，则当前用户至少可以访问其中一个winlet才允许访问该页面
				boolean hasWinlet = false;
				for (Area area : page.getAreas()) {
					if (area.isCascade()) // cascade的area不作为判断依据
						continue;

					if (area.getWinletUrls().size() > 0) {
						hasWinlet = true;
						for (String url : area.getWinletUrls()) {
							if (debug)
								System.out.println(url + ": " + check(url));

							if (check(url) == null)
								return null;
						}

						return hasWinlet ? Unspecified.class : null;
					}
				}
			}

			return null;
		}

		try {
			if (getRuleEngine().eval(rule))
				return null;
		} catch (Exception e) {
			logger.error("Error evaluating rule \"" + rule
					+ "\" defined on page \"" + page.getFullPath() + "\".", e);
		}

		return Unspecified.class;
	}

	@Override
	public Class<? extends Exception> check(Class<?> controller) {
		AccessRule rule = getRule(controller);
		if (rule == null)
			return null;

		try {
			if (getRuleEngine().eval(rule.value()))
				return null;
		} catch (Exception e) {
			logger.error("Error evaluating access rule \"" + rule.value()
					+ "\" defined on class " + controller.getName() + "\"", e);
			return rule.exception();
		}

		return rule.exception();
	}

	@Override
	public Class<? extends Exception> check(Class<?> controller, Method method) {
		AccessRule rule = getRule(method);
		if (rule == null)
			return check(controller);

		try {
			if (getRuleEngine().eval(rule.value()))
				return null;
		} catch (Exception e) {
			logger.error("Error evaluating access rule \"" + rule.value()
					+ "\" defined on method \"" + method.getName()
					+ "\" of class \"" + method.getDeclaringClass().getName()
					+ "\"", e);
			return rule.exception();
		}

		return rule.exception();
	}

	static Map<String, Method> methodMap = Collections
			.synchronizedMap(new HashMap<String, Method>());
	static Map<String, Class<?>> classMap = Collections
			.synchronizedMap(new HashMap<String, Class<?>>());

	@Override
	public Class<? extends Exception> check(String url) {
		if (url == null)
			return Unspecified.class;

		// { 分解winlet url和method url，获得完整的URL
		String winletUrl = null;
		String methodUrl = null;

		url = url.trim();
		if (url.startsWith("/"))
			url = url.substring(1);
		int idx = url.indexOf("/");
		if (idx > 0) {
			winletUrl = "/" + url.substring(0, idx).trim();
			methodUrl = "/" + url.substring(idx + 1).trim();
		} else {
			methodUrl = "/" + url.trim();
		}

		if (winletUrl == null) {
			HandlerMethod hm = ContextUtils.getHandlerMethod(ContextUtils
					.getRequest());
			if (hm == null)
				return Unspecified.class;
			WinletDef def = WinletDef.getDef(hm.getBeanType());
			if (def == null)
				return Unspecified.class;

			winletUrl = "/" + def.getName();
		}

		// 获得完整的URL
		url = winletUrl + methodUrl;
		// }

		Method method = methodMap.get(url);
		Class<?> clz = classMap.get(url);

		if (method == null) { // 找到url对应的method
			if (methodMap.containsKey(url))
				return Unspecified.class;

			methodMap.put(url, null);

			// { 找到对应的Winlet类
			clz = WinletClassLoader.getWinletClassByPath(winletUrl);

			if (clz == null)
				return Unspecified.class;
			// }

			// { 找到对应的方法
			for (Method m : clz.getMethods()) {
				RequestMapping rm = AnnotationUtils.findAnnotation(m,
						RequestMapping.class);
				if (rm != null)
					for (String str : rm.value())
						if (methodUrl.equals(str)) {
							method = m;
							break;
						}

				if (method != null)
					break;

				Action action = AnnotationUtils.findAnnotation(m, Action.class);
				if (action != null) {
					String str = action.value();
					if (StringUtils.isEmpty(str))
						str = m.getName();
					str = "/" + str;
					if (methodUrl.equals(str)) {
						method = m;
						break;
					}
				}
			}

			if (method == null)
				return Unspecified.class;
			// }

			methodMap.put(url, method);
			classMap.put(url, clz);
		}

		return check(clz, method);
	}
}
