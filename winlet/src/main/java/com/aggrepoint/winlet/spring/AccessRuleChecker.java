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
 * @author Jim
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

	public static boolean evalRule(Class<?> c) {
		try {
			AccessRule rule = getRule(c);
			if (rule == null || rule.value() == null || "".equals(rule.value()))
				return true;

			return getRuleEngine().eval(rule.value());
		} catch (Exception e) {
			logger.error("Error evaluating access rule \"" + getRule(c)
					+ "\" defined on class " + c.getName() + "\"", e);
			return false;
		}
	}

	public static boolean evalRule(Method m) {
		try {
			AccessRule rule = getRule(m);
			if (rule == null || rule.value() == null || "".equals(rule.value()))
				return true;

			return getRuleEngine().eval(rule.value());
		} catch (Exception e) {
			logger.error(
					"Error evaluating access rule \"" + getRule(m)
							+ "\" defined on method \"" + m.getName()
							+ "\" of class \""
							+ m.getDeclaringClass().getName() + "\"", e);
			return false;
		}
	}
}
