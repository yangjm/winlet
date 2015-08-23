package com.aggrepoint.dao;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;

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
public class DaoBaseMethod<T> implements DaoMethod<T> {
	Class<T> clz;

	static Object[][] methods = {
			{ 101, "create", new Class[] { null }, DaoService.class },
			{ 102, "createOrUpdate", new Class[] { null }, DaoService.class },
			{ 103, "find", new Class[] { Serializable.class }, DaoService.class },
			{ 104, "find", new Class[] {}, DaoService.class },
			{ 105, "update", new Class[] { null }, DaoService.class },
			{ 106, "delete", new Class[] { null }, DaoService.class },
			{ 107, "delete", new Class[] { Collection.class }, DaoService.class },

			{ 1, "clear", new Class[] {}, HibernateDao.class },
			{ 2, "contains", new Class[] { Object.class }, HibernateDao.class },
			{ 3, "evict", new Class[] { Object.class }, HibernateDao.class },
			{ 4, "find", new Class[] { String.class }, HibernateDao.class },
			{ 5, "find", new Class[] { String.class, Object.class },
					HibernateDao.class },
			{ 6, "find", new Class[] { String.class, Object[].class },
					HibernateDao.class },
			{ 7, "findByCriteria", new Class[] { DetachedCriteria.class },
					HibernateDao.class },
			{
					8,
					"findByCriteria",
					new Class[] { DetachedCriteria.class, int.class, int.class },
					HibernateDao.class },
			{ 9, "findByNamedParam",
					new Class[] { String.class, String.class, Object.class },
					HibernateDao.class },
			{
					10,
					"findByNamedParam",
					new Class[] { String.class, String[].class, Object[].class },
					HibernateDao.class },
			{ 11, "findByNamedQuery", new Class[] { String.class },
					HibernateDao.class },
			{ 12, "findByNamedQuery",
					new Class[] { String.class, Object.class },
					HibernateDao.class },
			{ 13, "findByNamedQuery",
					new Class[] { String.class, Object[].class },
					HibernateDao.class },
			{ 14, "findByNamedQueryAndNamedParam",
					new Class[] { String.class, String.class, Object.class },
					HibernateDao.class },
			{
					15,
					"findByNamedQueryAndNamedParam",
					new Class[] { String.class, String[].class, Object[].class },
					HibernateDao.class },
			{ 16, "flush", new Class[] {}, HibernateDao.class },
			{ 17, "load", new Class[] { Object.class, Serializable.class },
					HibernateDao.class },
			{ 18, "merge", new Class[] { Object.class }, HibernateDao.class },
			{ 19, "persist", new Class[] { Object.class }, HibernateDao.class },
			{ 20, "refresh", new Class[] { Object.class }, HibernateDao.class },
			{ 21, "replicate",
					new Class[] { Object.class, ReplicationMode.class },
					HibernateDao.class } };

	EntityManager entityManager;
	SessionFactory sessionFactory;
	int methodId;

	private Session getSession() {
		if (entityManager != null)
			return entityManager.unwrap(Session.class);
		if (sessionFactory != null)
			return sessionFactory.getCurrentSession();
		return null;
	}

	public DaoBaseMethod(Class<T> clz, Method method, EntityManager manager,
			SessionFactory factory) {
		this.clz = clz;
		this.entityManager = manager;
		this.sessionFactory = factory;

		for (Object[] m : methods) {
			if (!method.getName().equals(m[1]))
				continue;

			if (!method.getDeclaringClass().equals(m[3]))
				continue;

			Class<?>[] ptypes = method.getParameterTypes();
			if (!(ptypes.length == ((Class[]) m[2]).length))
				continue;

			boolean notMatch = false;
			for (int i = 0; i < ptypes.length; i++)
				if (!(((Class[]) m[2])[i] == null || ptypes[i]
						.equals(((Class[]) m[2])[i]))) {
					notMatch = true;
					break;
				}

			if (!notMatch) {
				methodId = (Integer) m[0];
				return;
			}
		}

		throw new IllegalArgumentException("Unable to match method '" + method
				+ "' defined in interface '"
				+ method.getDeclaringClass().getName() + "'.");
	}

	@SuppressWarnings("unchecked")
	public List<T> find(final String queryString, final Object... values)
			throws DataAccessException {
		Query queryObject = getSession().createQuery(queryString);
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
		Criteria executableCriteria = criteria
				.getExecutableCriteria(getSession());
		// SessionFactoryUtils.applyTransactionTimeout(executableCriteria,
		// factory);
		if (firstResult >= 0) {
			executableCriteria.setFirstResult(firstResult);
		}
		if (maxResults > 0) {
			executableCriteria.setMaxResults(maxResults);
		}
		return executableCriteria.list();
	}

	static void applyNamedParameterToQuery(Query queryObject, String paramName,
			Object value) throws HibernateException {
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
		Query queryObject = getSession().createQuery(queryString);
		// SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
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
		Query queryObject = getSession().getNamedQuery(queryName);
		// SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
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
		Query queryObject = getSession().getNamedQuery(queryName);
		// SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				applyNamedParameterToQuery(queryObject, paramNames[i],
						values[i]);
			}
		}
		return queryObject.list();
	}

	@SuppressWarnings("unchecked")
	public Object invoke(Object proxy, Method method, Object[] args) {
		switch (methodId) {
		case 101:
			return getSession().save(args[0]);
		case 102:
			getSession().saveOrUpdate(args[0]);
			return null;
		case 103:
			return getSession().get(clz, (Serializable) args[0]);
		case 104:
			Criteria criteria = getSession().createCriteria(clz);
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			// SessionFactoryUtils.applyTransactionTimeout(criteria, factory);
			return criteria.list();
		case 105:
			getSession().update(args[0]);
			return null;
		case 106:
			getSession().delete(args[0]);
			return null;
		case 107:
			Session session = getSession();
			for (T t : (Collection<T>) args[0])
				session.delete(t);
			return null;

		case 1:
			getSession().clear();
			return null;
		case 2:
			return getSession().contains(args[0]);
		case 3:
			getSession().evict(args[0]);
			return null;
		case 4:
			return find((String) args[0], (Object[]) null);
		case 5:
			return find((String) args[0], new Object[] { args[1] });
		case 6:
			return find((String) args[0], (Object[]) args[1]);
		case 7:
			return findByCriteria((DetachedCriteria) args[0], -1, -1);
		case 8:
			return findByCriteria((DetachedCriteria) args[0],
					(Integer) args[1], (Integer) args[2]);
		case 9:
			return findByNamedParam((String) args[0],
					new String[] { (String) args[1] }, new Object[] { args[2] });
		case 10:
			return findByNamedParam((String) args[0], (String[]) args[1],
					(Object[]) args[2]);
		case 11:
			return findByNamedQuery((String) args[0], (Object[]) null);
		case 12:
			return findByNamedQuery((String) args[0], new Object[] { args[1] });
		case 13:
			return findByNamedQuery((String) args[0], (Object[]) args[1]);
		case 14:
			return findByNamedQueryAndNamedParam((String) args[0],
					new String[] { (String) args[1] }, new Object[] { args[2] });
		case 15:
			return findByNamedQueryAndNamedParam((String) args[0],
					(String[]) args[1], (Object[]) args[2]);
		case 16:
			getSession().flush();
			return null;
		case 17:
			getSession().load(args[0], (Serializable) args[1]);
			return null;
		case 18:
			return getSession().merge(args[0]);
		case 19:
			getSession().persist(args[0]);
			return null;
		case 20:
			getSession().refresh(args[0]);
			return null;
		case 21:
			getSession().replicate(args[0], (ReplicationMode) args[1]);
			return null;
		}

		return null;
	}

	@Override
	public boolean match(Object[] args) throws NoSuchMethodException,
			InvocationTargetException, IllegalAccessException {
		return true;
	}
}
