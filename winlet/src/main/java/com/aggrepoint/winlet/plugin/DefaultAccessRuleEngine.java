package com.aggrepoint.winlet.plugin;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.aggrepoint.winlet.AccessRuleEngine;
import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.UserProfile;

/**
 * <pre>
 * 缺省的访问规则引擎
 * 以当前用户身份为root对象执行SpEL规则表达式
 * </pre>
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DefaultAccessRuleEngine implements AccessRuleEngine {
	Hashtable<String, Expression> htExpressionCache = new Hashtable<String, Expression>();

	protected Expression getExpression(String rule) {
		Expression exp = null;

		synchronized (htExpressionCache) {
			exp = htExpressionCache.get(rule);
			if (exp == null) {
				exp = new SpelExpressionParser().parseExpression(rule);
				htExpressionCache.put(rule, exp);
			}
		}

		return exp;
	}

	@Override
	public boolean eval(String rule) throws Exception {
		if (rule == null)
			return true;

		Expression exp = getExpression(rule);
		HttpServletRequest req = ContextUtils.getRequest();
		UserProfile user = ContextUtils.getUser(req);
		synchronized (exp) {
			return exp.getValue(user, Boolean.class);
		}
	}
}
