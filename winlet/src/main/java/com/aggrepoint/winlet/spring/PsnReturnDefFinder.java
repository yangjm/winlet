package com.aggrepoint.winlet.spring;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.PsnRuleEngine;
import com.aggrepoint.winlet.spring.def.ReturnDef;

/**
 * 负责找到个性化规则
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PsnReturnDefFinder {
	static final Log logger = LogFactory.getLog(PsnReturnDefFinder.class);

	public static ReturnDef getReturnDef(ArrayList<ReturnDef> defs) {
		if (defs == null || defs.size() == 0)
			return null;

		PsnRuleEngine rule = ContextUtils.getPsnRuleEngine(ContextUtils
				.getRequest());

		for (ReturnDef def : defs) {
			try {
				if (def.getRule() == null)
					return def;
				if (rule.eval(def.getRule()))
					return def;
			} catch (Exception e) {
				logger.error("Error evaluating psn rule \"" + def.getRule()
						+ "\"", e);
				return null;
			}
		}

		return null;
	}
}
