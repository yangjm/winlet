package com.aggrepoint.winlet.spring;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.annotation.AnnotationUtils;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.spring.annotation.AccessRule;

/**
 * 负责检查定义在类或方法上的AccessRule
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class AccessRuleChecker {
	static AccessRuleEngine ruleEngine;
	static HashMap<Class<?>, AccessRule> rulesOnClass = new HashMap<Class<?>, AccessRule>();
	static HashMap<Method, AccessRule> rulesOnMethod = new HashMap<Method, AccessRule>();

	static final Log logger = LogFactory.getLog(AccessRuleChecker.class);

	private static AccessRule getRule(Class<?> c) {
		if (rulesOnClass.containsKey(c))
			return rulesOnClass.get(c);

		AccessRule ar = AnnotationUtils.findAnnotation(c, AccessRule.class);
		rulesOnClass.put(c, ar);

		return ar;
	}

	private static AccessRule getRule(Method method) {
		if (rulesOnMethod.containsKey(method))
			return rulesOnMethod.get(method);

		AccessRule ar = AnnotationUtils
				.findAnnotation(method, AccessRule.class);
		rulesOnMethod.put(method, ar);

		return ar;
	}

	private static AccessRuleEngine getRuleEngine() {
		if (ruleEngine == null) {
			ruleEngine = ContextUtils.getAccessRuleEngine(ContextUtils
					.getRequest());
		}
		return ruleEngine;
	}

	public static AccessRule evalRule(Class<?> c) {
		AccessRule rule = getRule(c);
		try {
			if (rule == null || rule.value() == null || "".equals(rule.value()))
				return null;

			return getRuleEngine().eval(rule.value()) ? null : rule;
		} catch (Exception e) {
			logger.error("Error evaluating access rule \"" + rule.value()
					+ "\" defined on class " + c.getName() + "\"", e);
			return rule;
		}
	}

	public static AccessRule evalRule(Method m) {
		AccessRule rule = getRule(m);

		try {
			if (rule == null || rule.value() == null || "".equals(rule.value()))
				return null;

			return getRuleEngine().eval(rule.value()) ? null : rule;
		} catch (Exception e) {
			logger.error(
					"Error evaluating access rule \"" + rule.value()
							+ "\" defined on method \"" + m.getName()
							+ "\" of class \""
							+ m.getDeclaringClass().getName() + "\"", e);
			return rule;
		}
	}
}
