package com.aggrepoint.winlet;

import java.lang.reflect.Method;

import com.aggrepoint.winlet.site.domain.Branch;
import com.aggrepoint.winlet.site.domain.Page;

/**
 * <pre>
 * 20160405
 * AuthorizationEngine替代AccessRuleEngine，作为Winlet框架的访问权限检查机制。以便实现
 * 访问规则控制权限之外更加灵活的访问权限检查机制。
 * 
 * AccessRuleEngine仍然作为框架中的核心机制存在。作为AuthorizationEngine的缺省实现，
 * AccessRuleAuthorizationEngine中使用AccessRuleEngine实现了用访问规则控制权限的机制。
 * </pre>
 */
public interface AuthorizationEngine {
	/** 检查对Branch的访问 */
	public Class<? extends Exception> check(Branch branch);

	/** 检查对Page的访问 */
	public Class<? extends Exception> check(Page page, boolean expand);

	/** 检查对控制器类的访问 */
	public Class<? extends Exception> check(Class<?> controller);

	/** 检查对控制器方法的访问。 */
	public Class<? extends Exception> check(Class<?> controller, Method method);

	/** 检查对路径对应的Winlet的action或普通RequestMapping方法的访问。path如果只有一段，则视为在当前winlet中的路径 */
	public Class<? extends Exception> check(String path);
}
