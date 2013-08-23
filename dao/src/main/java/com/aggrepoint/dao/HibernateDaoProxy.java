package com.aggrepoint.dao;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Hashtable;

import org.hibernate.SessionFactory;

import com.aggrepoint.dao.annotation.Delete;
import com.aggrepoint.dao.annotation.Find;
import com.aggrepoint.dao.annotation.Update;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 *
 */
public class HibernateDaoProxy<T> implements InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;

	SessionFactory factory;
	Class<T> clz;
	Hashtable<Method, HibernateDaoMethod> htDaoMethods = new Hashtable<Method, HibernateDaoMethod>();

	public HibernateDaoProxy(SessionFactory factory, Class<?> daoInterface,
			Class<T> clz) {
		this.factory = factory;
		this.clz = clz;

		for (Method method : daoInterface.getMethods()) {
			boolean found = false;

			for (Method m : HibernateDao.class.getMethods()) {
				if (method.equals(m)) {
					htDaoMethods.put(method, new HibernateDaoBaseMethod<T>(clz,
							method, factory));
					found = true;
					break;
				}
			}

			if (!found)
				for (Annotation ann : method.getDeclaredAnnotations()) {
					Class<?> t = ann.annotationType();
					if (t == Find.class || t == Update.class
							|| t == Delete.class) {
						htDaoMethods.put(method,
								new HibernateDaoAnnotationMethod<T>(method,
										ann, factory));
						found = true;
						break;
					}
				}

			if (!found)
				throw new IllegalArgumentException("Unsupported method "
						+ daoInterface.getName() + "." + method.getName() + ".");
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {
		HibernateDaoMethod hdm = htDaoMethods.get(method);

		if (hdm != null)
			return hdm.invoke(args);

		return null;
	}
}
