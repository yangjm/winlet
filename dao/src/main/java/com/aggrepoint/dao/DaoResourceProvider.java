package com.aggrepoint.dao;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Metamodel;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.util.StringUtils;

/**
 * 为DaoAnnotationMethod和DaoBaseMethod延迟提供Session和CacheManager
 * 
 * 之前是在DaoFactoryBean中获取资源，会遇到DaoFactoryBean运行时Session尚未创建的情况
 * 
 * @author jimyang
 */
public class DaoResourceProvider {
	private ApplicationContext ctx;
	private String entityManagerFactoryName;
	private String sessionFactoryName;
	private Class<?> domainClz;

	private EntityManagerFactory entityManagerFactory;
	private SessionFactory sessionFactory;

	private CacheManager cacheManager;

	public DaoResourceProvider(ApplicationContext ctx, String entityManagerFactoryName, String sessionFactoryName,
			Class<?> domainClz) {
		this.ctx = ctx;
		this.entityManagerFactoryName = entityManagerFactoryName;
		this.sessionFactoryName = sessionFactoryName;
		this.domainClz = domainClz;
	}

	private EntityManagerFactory getEntityManagerFactory() {
		if (entityManagerFactory == null)
			try {
				if (StringUtils.isEmpty(entityManagerFactoryName)) {
					for (EntityManagerFactory f : ctx.getBeansOfType(EntityManagerFactory.class).values()) {
						final Metamodel mm = f.getMetamodel();

						for (final ManagedType<?> managedType : mm.getManagedTypes()) {
							if (managedType.getJavaType().equals(domainClz)) {
								entityManagerFactory = f;
								break;
							}
						}
					}
				} else
					entityManagerFactory = ctx.getBean(entityManagerFactoryName, EntityManagerFactory.class);
			} catch (NoSuchBeanDefinitionException e) {
			}

		return entityManagerFactory;
	}

	private SessionFactory getSessionFactory() {
		if (sessionFactory == null)
			try {
				if (StringUtils.isEmpty(sessionFactoryName)) {
					Map<String, SessionFactory> sfs = ctx.getBeansOfType(SessionFactory.class);
					if (sfs != null && sfs.size() > 0)
						sessionFactory = sfs.values().iterator().next();
				} else
					sessionFactory = ctx.getBean(sessionFactoryName, SessionFactory.class);
			} catch (NoSuchBeanDefinitionException e) {
			}
		return sessionFactory;
	}

	public Session getSession() {
		if (!StringUtils.isEmpty(sessionFactoryName) && StringUtils.isEmpty(entityManagerFactoryName)) {
			SessionFactory sf = getSessionFactory();
			if (sf != null)
				return sf.getCurrentSession();

			EntityManagerFactory em = getEntityManagerFactory();
			if (em != null)
				return EntityManagerFactoryUtils.getTransactionalEntityManager(em).unwrap(Session.class);
		} else {
			EntityManagerFactory em = getEntityManagerFactory();
			if (em != null)
				return EntityManagerFactoryUtils.getTransactionalEntityManager(em).unwrap(Session.class);

			SessionFactory sf = getSessionFactory();
			if (sf != null)
				return sf.getCurrentSession();
		}

		return null;
	}

	public CacheManager getCacheManager() {
		if (cacheManager != null)
			return cacheManager;

		try {
			cacheManager = ctx.getBean("daoCache", CacheManager.class);
		} catch (Exception e) {
		}
		if (cacheManager == null) {
			try {
				cacheManager = ctx.getBean(CacheManager.class);
			} catch (Exception e) {
			}
		}
		return cacheManager;
	}
}
