package com.aggrepoint.dao;

import static org.springframework.util.Assert.notNull;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.StringUtils;

/**
 * @see org.mybatis.spring.mapper.MapperScannerConfigurer
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DaoScannerConfigurer implements
		BeanDefinitionRegistryPostProcessor, InitializingBean,
		ApplicationContextAware, BeanNameAware {
	private String basePackage;
	private ApplicationContext applicationContext;
	private EntityManager entityManager;
	private SessionFactory sessionFactory;

	private String beanName;

	private boolean processPropertyPlaceHolders;

	private BeanNameGenerator nameGenerator;

	private List<IFunc> functions;

	public String getBasePackage() {
		return basePackage;
	}

	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	private String updatePropertyValue(String propertyName,
			PropertyValues values) {
		PropertyValue property = values.getPropertyValue(propertyName);

		if (property == null) {
			return null;
		}

		Object value = property.getValue();

		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return value.toString();
		} else if (value instanceof TypedStringValue) {
			return ((TypedStringValue) value).getValue();
		} else {
			return null;
		}
	}

	private void processPropertyPlaceHolders() {
		Map<String, PropertyResourceConfigurer> prcs = applicationContext
				.getBeansOfType(PropertyResourceConfigurer.class);

		if (!prcs.isEmpty()
				&& applicationContext instanceof GenericApplicationContext) {
			BeanDefinition mapperScannerBean = ((GenericApplicationContext) applicationContext)
					.getBeanFactory().getBeanDefinition(beanName);

			DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
			factory.registerBeanDefinition(beanName, mapperScannerBean);

			for (PropertyResourceConfigurer prc : prcs.values()) {
				prc.postProcessBeanFactory(factory);
			}

			PropertyValues values = mapperScannerBean.getPropertyValues();
			this.basePackage = updatePropertyValue("basePackage", values);
		}
	}

	@Override
	public void postProcessBeanDefinitionRegistry(
			BeanDefinitionRegistry registry) throws BeansException {
		if (this.processPropertyPlaceHolders)
			processPropertyPlaceHolders();

		DaoScanner scanner = new DaoScanner(registry, applicationContext,
				nameGenerator, functions, entityManager, sessionFactory);
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage,
				ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}

	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(this.basePackage, "Property 'basePackage' is required");
	}

	public void setProcessPropertyPlaceHolders(
			boolean processPropertyPlaceHolders) {
		this.processPropertyPlaceHolders = processPropertyPlaceHolders;
	}

	public BeanNameGenerator getNameGenerator() {
		return nameGenerator;
	}

	public void setNameGenerator(BeanNameGenerator nameGenerator) {
		this.nameGenerator = nameGenerator;
	}

	public void setFunctions(List<IFunc> funcs) {
		this.functions = funcs;
	}
}
