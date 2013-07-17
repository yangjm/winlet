package com.aggrepoint.dao;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.dao.support.DaoSupport;

public class HibernateDaoFactoryBean<T, K> extends DaoSupport implements
		FactoryBean<T> {
	private SessionFactory factory;
	private Class<T> daoInterface;
	private T proxy;
	private Class<K> domainClz;

	public SessionFactory getSessionFactory() {
		return factory;
	}

	public void setSessionFactory(SessionFactory factory) {
		this.factory = factory;
	}

	@SuppressWarnings("unchecked")
	public void setDaoInterface(Class<T> daoInterface) {
		this.daoInterface = daoInterface;

		for (Type t : daoInterface.getGenericInterfaces()) {
			if (t instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) t;
				if (pt.getRawType().equals(HibernateDao.class)) {
					domainClz = (Class<K>) pt.getActualTypeArguments()[0];
					break;
				}
			}
		}

		if (domainClz == null)
			throw new IllegalArgumentException(
					"Unable to match extract domain class from dao interface '"
							+ daoInterface + "'.");
	}

	/**
	 * {@inheritDoc}
	 */
	public T getObject() throws Exception {
		return proxy;
	}

	/**
	 * {@inheritDoc}
	 */
	public Class<T> getObjectType() {
		return this.daoInterface;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSingleton() {
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		notNull(daoInterface, "Property 'daoInterface' is required");
		isTrue(daoInterface.isInterface(),
				"Property 'daoInterface' must be interface");

		try {
			proxy = (T) Proxy.newProxyInstance(daoInterface.getClassLoader(),
					new Class[] { daoInterface }, new HibernateDaoProxy<K>(
							factory, daoInterface, domainClz));
		} catch (Throwable t) {
			logger.error("Error while creating proxy for dao interface '"
					+ this.daoInterface + "'.", t);
			throw new IllegalArgumentException(t);
		}
	}
}
