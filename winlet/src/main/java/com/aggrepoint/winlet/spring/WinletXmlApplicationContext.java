package com.aggrepoint.winlet.spring;

import java.io.IOException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletXmlApplicationContext extends XmlWebApplicationContext {
	public final static String SCOPE_WINLET = "winlet";

	private WinletClassLoader classLoader;

	/**
	 * 引入winlet scope
	 */
	@Override
	protected void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) {
		super.postProcessBeanFactory(beanFactory);
		beanFactory.registerScope(SCOPE_WINLET, new WinletScope());

		// replace RequestMappingHandlerAdapter with
		// WinletRequestMappingHandlerAdapter
		for (String name : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition bd = beanFactory.getBeanDefinition(name);
			if (bd.getBeanClassName().equals(
					RequestMappingHandlerAdapter.class.getName()))
				bd.setBeanClassName(WinletRequestMappingHandlerAdapter.class
						.getName());
		}
	}

	/**
	 * 重载该方法的目的是在容器加载bean时，处理Winlet相关的注解
	 */
	@Override
	public ClassLoader getClassLoader() {
		if (classLoader == null) {
			if (!(super.getClassLoader() instanceof WinletClassLoader))
				classLoader = new WinletClassLoader(getParent()
						.getClassLoader());
		}

		return classLoader;
	}

	/**
	 * 重载该方法的目的是在容器访问bean的Scope注解之前，将@Winlet注解转换为@Scope注解
	 * 
	 * 由于Spring使用Resource和ClassReader访问bean的注解
	 * ，而不是使用ClassLoader，因此仅重载getClassLoader()不起作用。
	 */
	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		Resource[] reses = super.getResources(locationPattern);

		for (int i = 0; i < reses.length; i++)
			reses[i] = ((WinletClassLoader) getClassLoader()).convert(reses[i]);

		return reses;
	}
}
