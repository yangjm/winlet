package com.aggrepoint.winlet;

/**
 * 访问控制规则引擎，负责执行访问规则表达式
 * 
 * @author Jim
 */
public interface AccessRuleEngine {
	boolean eval(String rule) throws Exception;
}
