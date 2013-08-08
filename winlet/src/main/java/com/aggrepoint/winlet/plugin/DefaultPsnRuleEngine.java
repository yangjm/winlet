package com.aggrepoint.winlet.plugin;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.PsnRuleEngine;
import com.aggrepoint.winlet.UserProfile;

/**
 * <pre>
 * 缺省的个性化规则引擎，可用的对象包括：
 * user		当前用户
 * req		当前HttpServletRequest
 * </pre>
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DefaultPsnRuleEngine implements PsnRuleEngine {
	Hashtable<String, Expression> htExpressionCache = new Hashtable<String, Expression>();

	@Override
	public boolean eval(String rule, Hashtable<String, Object> variables)
			throws Exception {
		if (rule == null)
			return true;

		Expression exp = null;

		synchronized (htExpressionCache) {
			exp = htExpressionCache.get(rule);
			if (exp == null) {
				exp = new SpelExpressionParser().parseExpression(rule);
				htExpressionCache.put(rule, exp);
			}
		}

		HttpServletRequest req = ContextUtils.getRequest();
		UserProfile user = ContextUtils.getUser(req);

		EvaluationContext ctx = new StandardEvaluationContext();
		ctx.setVariable("user", user);
		ctx.setVariable("req", req);

		if (variables != null)
			for (String key : variables.keySet())
				ctx.setVariable(key, variables.get(key));

		synchronized (exp) {
			return exp.getValue(ctx, Boolean.class);
		}
	}

	@Override
	public boolean eval(String rule) throws Exception {
		return eval(rule, null);
	}
}
