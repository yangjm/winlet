package com.aggrepoint.dao;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.dao.support.DaoSupport;

/**
 * 不能注入SessionFactory，不能在checkDaoConfig时获取SessionFactory，
 * 因为会造成嵌套加载其他扫描Dao对象的情况，对象数量多时会引起堆栈溢出
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class HibernateDaoFactoryBean<T, K> extends DaoSupport implements
		FactoryBean<T>, ApplicationContextAware {
	private ApplicationContext ctx;
	private Class<T> daoInterface;
	private T proxy;
	private Class<K> domainClz;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ctx = applicationContext;
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
	@SuppressWarnings("unchecked")
	public T getObject() throws Exception {
		if (proxy == null) {
			try {
				proxy = (T) Proxy.newProxyInstance(
						daoInterface.getClassLoader(),
						new Class[] { daoInterface },
						new HibernateDaoProxy<K>(ctx
								.getBean(SessionFactory.class), daoInterface,
								domainClz));
			} catch (Throwable t) {
				logger.error("Error while creating proxy for dao interface '"
						+ this.daoInterface + "'.", t);
				throw new IllegalArgumentException(t);
			}
		}

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

	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {
		notNull(daoInterface, "Property 'daoInterface' is required");
		isTrue(daoInterface.isInterface(),
				"Property 'daoInterface' must be interface");
	}
}
