package com.mmec.winlet.plugin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.UserProfile;
import com.aggrepoint.winlet.plugin.DefaultAccessRuleEngine;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class AccessRuleEngine extends DefaultAccessRuleEngine {
	@Override
	public boolean eval(String rule) throws Exception {
		if (rule == null)
			return true;

		Expression exp = getExpression(rule);
		HttpServletRequest req = ContextUtils.getRequest();
		UserProfile user = ContextUtils.getUser(req);

		StandardEvaluationContext ctx = new StandardEvaluationContext();
		ctx.setVariable("request", req);
		ctx.setRootObject(user);
		synchronized (exp) {
			return exp.getValue(ctx, Boolean.class);
		}
	}
}
