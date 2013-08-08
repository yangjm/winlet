package com.aggrepoint.winlet;

import java.util.Hashtable;

/**
 * 个性化规则引擎，负责执行个性化规则表达式
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface PsnRuleEngine {
	boolean eval(String rule) throws Exception;

	boolean eval(String rule, Hashtable<String, Object> variables)
			throws Exception;
}
