package com.aggrepoint.winlet.spring;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.web.context.request.AbstractRequestAttributesScope;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletScope extends AbstractRequestAttributesScope {
	@Override
	public Object get(String name,
			@SuppressWarnings("rawtypes") ObjectFactory objectFactory) {
		// 控制逻辑放在WinletManager。只有当WinletManager需要获得新的实例时，才会调用到这个方法
		return objectFactory.getObject();
	}

	@Override
	public String getConversationId() {
		return null;
	}

	@Override
	protected int getScope() {
		return 10;
	}
}
