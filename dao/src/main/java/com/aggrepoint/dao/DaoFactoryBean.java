package com.aggrepoint.dao;

import static org.springframework.util.Assert.isTrue;
import static org.springframework.util.Assert.notNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ConversionServiceFactoryBean;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.support.DaoSupport;

/**
 * 不能注入SessionFactory，不能在checkDaoConfig时获取SessionFactory，
 * 因为会造成嵌套加载其他扫描Dao对象的情况，对象数量多时会引起堆栈溢出
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DaoFactoryBean<T, K> extends DaoSupport implements FactoryBean<T>,
		ApplicationContextAware {
	private static final Log logger = LogFactory.getLog(DaoFactoryBean.class);

	private static ConversionService conversionService;
	private ApplicationContext ctx;
	private Class<T> daoInterface;
	private T proxy;
	private Class<K> domainClz;
	private List<IFunc> funcs;

	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		ctx = applicationContext;
	}

	@SuppressWarnings("unchecked")
	Class<K> findDomainClass(Class<?> intf) {
		for (Type t : intf.getGenericInterfaces()) {
			if (t instanceof ParameterizedType) {
				ParameterizedType pt = (ParameterizedType) t;
				if (pt.getRawType().equals(DaoService.class)) {
					return (Class<K>) pt.getActualTypeArguments()[0];
				}
			}
		}

		for (Class<?> t : intf.getInterfaces()) {
			Class<K> k = findDomainClass(t);
			if (k != null)
				return k;
		}

		return null;
	}

	public void setDaoInterface(Class<T> daoInterface) {
		this.daoInterface = daoInterface;
		domainClz = findDomainClass(daoInterface);

		if (domainClz == null) {
			// { exception caught by Spring framework, so have to print it out
			// here
			String err = "Unable to match extract domain class from dao interface '"
					+ daoInterface + "'.";
			logger.error(err);
			// }

			throw new IllegalArgumentException(err);
		}
	}

	public void setFuncs(List<IFunc> funcs) {
		this.funcs = funcs;
	}

	/**
	 * get ConversionService. If a service is defined in context with name
	 * daoConversionService then use it, otherwise create a default one
	 * 
	 * @return
	 */
	private ConversionService getConversionService() {
		if (conversionService != null)
			return conversionService;

		try {
			conversionService = (ConversionService) ctx
					.getBean("daoConversionService");
			if (conversionService != null)
				return conversionService;
		} catch (Exception e) {
		}

		ConversionServiceFactoryBean factory = new ConversionServiceFactoryBean();
		factory.afterPropertiesSet();
		conversionService = factory.getObject();
		return conversionService;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	public T getObject() throws Exception {
		if (proxy == null) {
			try {
				CacheManager cm = null;
				try {
					cm = ctx.getBean("daoCache", CacheManager.class);
				} catch (Exception e) {
				}
				if (cm == null) {
					try {
						cm = ctx.getBean(CacheManager.class);
					} catch (Exception e) {
					}
				}

				proxy = (T) Proxy.newProxyInstance(
						daoInterface.getClassLoader(),
						new Class[] { daoInterface },
						new DaoInvocationHandler<K>(ctx
								.getBean(SessionFactory.class), cm,
								getConversionService(), daoInterface,
								domainClz, funcs));
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
