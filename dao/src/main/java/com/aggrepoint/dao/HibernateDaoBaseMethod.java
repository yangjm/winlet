package com.aggrepoint.dao;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataAccessException;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 *
 */
public class HibernateDaoBaseMethod<T> implements HibernateDaoMethod {
	Class<T> clz;

	static Object[][] methods = {
			{ 1, "clear", new Class[] {} },
			{ 2, "contains", new Class[] { Object.class } },
			{ 3, "delete", new Class[] { Object.class } },
			{ 4, "deleteAll", new Class[] { Collection.class } },
			{ 5, "evict", new Class[] { Object.class } },
			{ 6, "find", new Class[] { String.class } },
			{ 7, "find", new Class[] { String.class, Object.class } },
			{ 8, "find", new Class[] { String.class, Object[].class } },
			{ 9, "findByCriteria", new Class[] { DetachedCriteria.class } },
			{
					10,
					"findByCriteria",
					new Class[] { DetachedCriteria.class, int.class, int.class } },
			{ 11, "findByNamedParam",
					new Class[] { String.class, String.class, Object.class } },
			{
					12,
					"findByNamedParam",
					new Class[] { String.class, String[].class, Object[].class } },
			{ 13, "findByNamedQuery", new Class[] { String.class } },
			{ 14, "findByNamedQuery",
					new Class[] { String.class, Object.class } },
			{ 15, "findByNamedQuery",
					new Class[] { String.class, Object[].class } },
			{ 16, "findByNamedQueryAndNamedParam",
					new Class[] { String.class, String.class, Object.class } },
			{
					17,
					"findByNamedQueryAndNamedParam",
					new Class[] { String.class, String[].class, Object[].class } },
			{ 18, "flush", new Class[] {} },
			{ 19, "get", new Class[] { Serializable.class } },
			{ 20, "load", new Class[] { Object.class, Serializable.class } },
			{ 21, "loadAll", new Class[] {} },
			{ 22, "merge", new Class[] { Object.class } },
			{ 23, "persist", new Class[] { Object.class } },
			{ 24, "refresh", new Class[] { Object.class } },
			{ 25, "replicate",
					new Class[] { Object.class, ReplicationMode.class } },
			{ 26, "save", new Class[] { Object.class } },
			{ 27, "saveOrUpdate", new Class[] { Object.class } },
			{ 28, "update", new Class[] { Object.class } } };

	SessionFactory factory;
	int methodId;

	public HibernateDaoBaseMethod(Class<T> clz, Method method,
			SessionFactory factory) {
		this.clz = clz;
		this.factory = factory;

		for (Object[] m : methods) {
			if (!method.getName().equals(m[1]))
				continue;

			Class<?>[] ptypes = method.getParameterTypes();
			if (!(ptypes.length == ((Class[]) m[2]).length))
				continue;

			boolean notMatch = false;
			for (int i = 0; i < ptypes.length; i++)
				if (!ptypes[i].equals(((Class[]) m[2])[i])) {
					notMatch = true;
					break;
				}

			if (!notMatch) {
				methodId = (Integer) m[0];
				return;
			}
		}

		throw new IllegalArgumentException(
				"Unable to match HibernateDao method '" + method
						+ "' defined in interface '" + method.getDeclaringClass().getName() + "'.");
	}

	@SuppressWarnings("unchecked")
	public List<T> find(final String queryString, final Object... values)
			throws DataAccessException {
		Query queryObject = factory.getCurrentSession()
				.createQuery(queryString);
		// SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(final DetachedCriteria criteria,
			final int firstResult, final int maxResults)
			throws DataAccessException {
		Criteria executableCriteria = criteria.getExecutableCriteria(factory
				.getCurrentSession());
		//SessionFactoryUtils.applyTransactionTimeout(executableCriteria, factory);
		if (firstResult >= 0) {
			executableCriteria.setFirstResult(firstResult);
		}
		if (maxResults > 0) {
			executableCriteria.setMaxResults(maxResults);
		}
		return executableCriteria.list();
	}

	static void applyNamedParameterToQuery(Query queryObject,
			String paramName, Object value) throws HibernateException {
		if (value instanceof Collection) {
			queryObject.setParameterList(paramName, (Collection<?>) value);
		} else if (value instanceof Object[]) {
			queryObject.setParameterList(paramName, (Object[]) value);
		} else {
			queryObject.setParameter(paramName, value);
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> findByNamedParam(final String queryString,
			final String[] paramNames, final Object[] values)
			throws DataAccessException {
		Query queryObject = factory.getCurrentSession()
				.createQuery(queryString);
		//SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
		if (values != null) {
			for (int i = 0; i < values.length; i++)
				applyNamedParameterToQuery(queryObject, paramNames[i],
						values[i]);
		}
		return queryObject.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByNamedQuery(final String queryName,
			final Object... values) throws DataAccessException {
		Query queryObject = factory.getCurrentSession()
				.getNamedQuery(queryName);
		//SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByNamedQueryAndNamedParam(final String queryName,
			final String[] paramNames, final Object[] values)
			throws DataAccessException {
		Query queryObject = factory.getCurrentSession()
				.getNamedQuery(queryName);
		//SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				applyNamedParameterToQuery(queryObject, paramNames[i],
						values[i]);
			}
		}
		return queryObject.list();
	}

	@SuppressWarnings("unchecked")
	public Object invoke(Object[] args) {
		switch (methodId) {
		case 1:
			factory.getCurrentSession().clear();
			return null;
		case 2:
			return factory.getCurrentSession().contains(args[0]);
		case 3:
			factory.getCurrentSession().delete(args[0]);
			return null;
		case 4:
			Session session = factory.getCurrentSession();
			for (T t : (Collection<T>) args[0])
				session.delete(t);
			return null;
		case 5:
			factory.getCurrentSession().evict(args[0]);
			return null;
		case 6:
			return find((String) args[0], (Object[]) null);
		case 7:
			return find((String) args[0], new Object[] { args[1] });
		case 8:
			return find((String) args[0], (Object[]) args[1]);
		case 9:
			return findByCriteria((DetachedCriteria) args[0], -1, -1);
		case 10:
			return findByCriteria((DetachedCriteria) args[0], (Integer) args[1], (Integer) args[2]);
		case 11:
			return findByNamedParam((String) args[0],
					new String[] { (String) args[1] }, new Object[] { args[2] });
		case 12:
			return findByNamedParam((String) args[0], (String[]) args[1],
					(Object[]) args[2]);
		case 13:
			return findByNamedQuery((String) args[0], (Object[]) null);
		case 14:
			return findByNamedQuery((String) args[0], new Object[] { args[1] });
		case 15:
			return findByNamedQuery((String) args[0], (Object[]) args[1]);
		case 16:
			return findByNamedQueryAndNamedParam((String) args[0],
					new String[] { (String) args[1] }, new Object[] { args[2] });
		case 17:
			return findByNamedQueryAndNamedParam((String) args[0],
					(String[]) args[1], (Object[]) args[2]);
		case 18:
			factory.getCurrentSession().flush();
			return null;
		case 19:
			return factory.getCurrentSession().get(clz, (Serializable) args[0]);
		case 20:
			factory.getCurrentSession().load(args[0], (Serializable) args[1]);
			return null;
		case 21:
			Criteria criteria = factory.getCurrentSession().createCriteria(clz);
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			//SessionFactoryUtils.applyTransactionTimeout(criteria, factory);
			return criteria.list();
		case 22:
			return factory.getCurrentSession().merge(args[0]);
		case 23:
			factory.getCurrentSession().persist(args[0]);
			return null;
		case 24:
			factory.getCurrentSession().refresh(args[0]);
			return null;
		case 25:
			factory.getCurrentSession().replicate(args[0],
					(ReplicationMode) args[1]);
			return null;
		case 26:
			return factory.getCurrentSession().save(args[0]);
		case 27:
			factory.getCurrentSession().saveOrUpdate(args[0]);
			return null;
		case 28:
			factory.getCurrentSession().update(args[0]);
			return null;
		}

		return null;
	}
}
