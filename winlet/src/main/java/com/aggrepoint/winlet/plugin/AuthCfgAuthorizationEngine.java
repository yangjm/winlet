package com.aggrepoint.winlet.plugin;

import java.lang.reflect.Method;
import java.util.HashMap;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.RoleBasedUserProfile;
import com.aggrepoint.winlet.UserProfile;
import com.aggrepoint.winlet.spring.annotation.AccessRule;
import com.aggrepoint.winlet.spring.annotation.Unspecified;
import com.aggrepoint.winlet.spring.def.BaseDef;
import com.aggrepoint.winlet.spring.def.WinletDef;

/**
 * <pre>
 * 支持AccessRule和基于AuthConfig在代码之外（不是通过AccessRule注解）定义的访问限制配置
 * 只有双重配置通过才允许访问
 * AuthConfig对于非Winlet或非Action或非Window不起作用（直接检查通过）
 * </pre>
 * 
 * @author jiangmingyang
 */
public abstract class AuthCfgAuthorizationEngine extends
		AccessRuleAuthorizationEngine {
	private long cfgTimestamp;
	private HashMap<String, AuthCfgWinlet> winletMap;
	private HashMap<String, AuthCfgMethod> methodMap;

	abstract protected AuthConfig getConfig();

	private void updateCfg() {
		AuthConfig cfg = getConfig();

		if (winletMap == null || cfg.getTimestamp() != cfgTimestamp) {
			winletMap = new HashMap<String, AuthCfgWinlet>();
			methodMap = new HashMap<String, AuthCfgMethod>();

			if (cfg.getWinlets() != null)
				for (AuthCfgWinlet winlet : cfg.getWinlets()) {
					winletMap.put(winlet.getPath(), winlet);

					for (AuthCfgMethod method : winlet.getMethods()) {
						String path = winlet.getPath() + "/" + method.getPath();
						winletMap.put(path, winlet);
						methodMap.put(path, method);
					}
				}
		}
	}

	@Override
	public Class<? extends Exception> check(Class<?> controller) {
		Class<? extends Exception> ret = super.check(controller);
		if (ret != null)
			return ret;

		updateCfg();
		WinletDef def = WinletDef.getDef(controller);
		if (def == null) // 不是winlet，不在控制范围内，允许访问
			return null;

		AuthCfgWinlet cfg = winletMap.get(def.getName());
		if (cfg == null) // 没有配置，可以访问
			return null;

		if (cfg.isPublic())
			return null;

		UserProfile up = ContextUtils.getUser(ContextUtils.getRequest());
		if ((cfg.getRoles() == null || cfg.getRoles().size() == 0)
				&& !up.isAnonymous()) // 非匿名用户可以访问
			return null;

		if (!(up instanceof RoleBasedUserProfile)) // 配置制定了可以访问的角色，但当前用户没有实现RoleBasedUserProfile接口，不可以访问
			return Unspecified.class;

		if (((RoleBasedUserProfile) up).hasRole(cfg.getRoles()))
			return null;

		return Unspecified.class;
	}

	@Override
	public Class<? extends Exception> check(Class<?> controller, Method method) {
		AccessRule rule = getRule(method);
		if (rule != null) {
			try {
				if (!getRuleEngine().eval(rule.value()))
					return rule.exception();
			} catch (Exception e) {
				logger.error("Error evaluating access rule \"" + rule.value()
						+ "\" defined on method \"" + method.getName()
						+ "\" of class \""
						+ method.getDeclaringClass().getName() + "\"", e);
				return rule.exception();
			}
		}

		updateCfg();
		WinletDef win = WinletDef.getDef(controller);

		if (win != null) { // 是winlet，在控制范围内
			BaseDef def = win.getDef(method);
			if (def != null) { // 是action或window，在控制范围内
				AuthCfgMethod cfg = methodMap.get(def.getWinletDef().getName()
						+ "/" + def.getName());
				if (cfg != null && !cfg.isInherit()) {
					if (cfg.isPublic())
						return null;

					UserProfile up = ContextUtils.getUser(ContextUtils
							.getRequest());
					if ((cfg.getRoles() == null || cfg.getRoles().size() == 0)
							&& !up.isAnonymous()) // 非匿名用户可以访问
						return null;

					if (!(up instanceof RoleBasedUserProfile)) // 配置制定了可以访问的角色，但当前用户没有实现RoleBasedUserProfile接口，不可以访问
						return Unspecified.class;

					if (((RoleBasedUserProfile) up).hasRole(cfg.getRoles()))
						return null;

					return Unspecified.class;
				}
			}
		}

		return check(controller);
	}
}
