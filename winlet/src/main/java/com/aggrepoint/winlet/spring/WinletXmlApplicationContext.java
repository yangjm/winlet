package com.aggrepoint.winlet.spring;

import java.io.IOException;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletXmlApplicationContext extends XmlWebApplicationContext {
	public final static String SCOPE_WINLET = "winlet";

	/**
	 * 引入winlet scope
	 */
	@Override
	protected void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) {
		super.postProcessBeanFactory(beanFactory);
		beanFactory.registerScope(SCOPE_WINLET, new WinletScope());
	}

	/**
	 * 重载该方法的目的是在容器加载bean时，处理Winlet相关的注解
	 */
	@Override
	public ClassLoader getClassLoader() {
		return new WinletClassLoader(super.getClassLoader());
	}

	/**
	 * 重载该方法的目的是在容器访问bean的Scope注解之前，将@Winlet注解转换为@Scope注解
	 * 
	 * 由于Spring使用Resource和ClassReader访问bean的注解
	 * ，而不是使用ClassLoader，因此仅重载getClassLoader()不起作用。
	 */
	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		WinletClassLoader wcl = new WinletClassLoader(super.getClassLoader());

		Resource[] reses = super.getResources(locationPattern);

		for (int i = 0; i < reses.length; i++)
			reses[i] = wcl.convert(reses[i]);

		return reses;
	}
}
