package com.aggrepoint.dao;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.query.Query;
import org.springframework.dao.DataAccessException;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public class DaoBaseMethod<T> implements DaoMethod<T> {
	Class<T> clz;

	static Object[][] methods = { { 101, "create", new Class[] { null }, DaoService.class },
			{ 102, "createOrUpdate", new Class[] { null }, DaoService.class },
			{ 103, "find", new Class[] { Serializable.class }, DaoService.class },
			{ 104, "find", new Class[] {}, DaoService.class },
			{ 105, "update", new Class[] { null }, DaoService.class },
			{ 106, "delete", new Class[] { null }, DaoService.class },
			{ 107, "delete", new Class[] { Collection.class }, DaoService.class },
			{ 108, "evict", new Class[] { null }, DaoService.class },

			{ 1, "clear", new Class[] {}, HibernateDao.class },
			{ 2, "contains", new Class[] { Object.class }, HibernateDao.class },
			{ 3, "evict", new Class[] { Object.class }, HibernateDao.class },
			{ 4, "find", new Class[] { String.class }, HibernateDao.class },
			{ 5, "find", new Class[] { String.class, Object.class }, HibernateDao.class },
			{ 6, "find", new Class[] { String.class, Object[].class }, HibernateDao.class },
			{ 7, "findByCriteria", new Class[] { DetachedCriteria.class }, HibernateDao.class },
			{ 8, "findByCriteria", new Class[] { DetachedCriteria.class, int.class, int.class }, HibernateDao.class },
			{ 9, "findByNamedParam", new Class[] { String.class, String.class, Object.class }, HibernateDao.class },
			{ 10, "findByNamedParam", new Class[] { String.class, String[].class, Object[].class },
					HibernateDao.class },
			{ 11, "findByNamedQuery", new Class[] { String.class }, HibernateDao.class },
			{ 12, "findByNamedQuery", new Class[] { String.class, Object.class }, HibernateDao.class },
			{ 13, "findByNamedQuery", new Class[] { String.class, Object[].class }, HibernateDao.class },
			{ 14, "findByNamedQueryAndNamedParam", new Class[] { String.class, String.class, Object.class },
					HibernateDao.class },
			{ 15, "findByNamedQueryAndNamedParam", new Class[] { String.class, String[].class, Object[].class },
					HibernateDao.class },
			{ 16, "flush", new Class[] {}, HibernateDao.class },
			{ 17, "load", new Class[] { Object.class, Serializable.class }, HibernateDao.class },
			{ 18, "merge", new Class[] { Object.class }, HibernateDao.class },
			{ 19, "persist", new Class[] { Object.class }, HibernateDao.class },
			{ 20, "refresh", new Class[] { Object.class }, HibernateDao.class },
			{ 21, "replicate", new Class[] { Object.class, ReplicationMode.class }, HibernateDao.class } };

	EntityManagerFactory entityManagerFactory;
	SessionFactory sessionFactory;
	int methodId;

	public DaoBaseMethod(Class<T> clz, Method method, EntityManagerFactory managerFactory, SessionFactory factory) {
		this.clz = clz;
		this.entityManagerFactory = managerFactory;
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
				if (!(((Class[]) m[2])[i] == null || ptypes[i].equals(((Class[]) m[2])[i]))) {
					notMatch = true;
					break;
				}

			if (!notMatch) {
				methodId = (Integer) m[0];
				return;
			}
		}

		throw new IllegalArgumentException("Unable to match method '" + method + "' defined in interface '"
				+ method.getDeclaringClass().getName() + "'.");
	}

	@SuppressWarnings("unchecked")
	public List<T> find(Session session, final String queryString, final Object... values) throws DataAccessException {
		Query<T> queryObject = session.createQuery(queryString);
		// SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByCriteria(Session session, final DetachedCriteria criteria, final int firstResult,
			final int maxResults) throws DataAccessException {
		Criteria executableCriteria = criteria.getExecutableCriteria(session);
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

	static void applyNamedParameterToQuery(Query<?> queryObject, String paramName, Object value)
			throws HibernateException {
		if (value instanceof Collection) {
			queryObject.setParameterList(paramName, (Collection<?>) value);
		} else if (value instanceof Object[]) {
			queryObject.setParameterList(paramName, (Object[]) value);
		} else {
			queryObject.setParameter(paramName, value);
		}
	}

	@SuppressWarnings("unchecked")
	public List<T> findByNamedParam(Session session, final String queryString, final String[] paramNames,
			final Object[] values) throws DataAccessException {
		Query<T> queryObject = session.createQuery(queryString);
		// SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
		if (values != null) {
			for (int i = 0; i < values.length; i++)
				applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
		}
		return queryObject.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByNamedQuery(Session session, final String queryName, final Object... values)
			throws DataAccessException {
		Query<T> queryObject = session.getNamedQuery(queryName);
		// SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				queryObject.setParameter(i, values[i]);
			}
		}
		return queryObject.list();
	}

	@SuppressWarnings("unchecked")
	public List<T> findByNamedQueryAndNamedParam(Session session, final String queryName, final String[] paramNames,
			final Object[] values) throws DataAccessException {
		Query<T> queryObject = session.getNamedQuery(queryName);
		// SessionFactoryUtils.applyTransactionTimeout(queryObject, factory);
		if (values != null) {
			for (int i = 0; i < values.length; i++) {
				applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
			}
		}
		return queryObject.list();
	}

	@SuppressWarnings("unchecked")
	public Object invoke(Object proxy, Method method, Object[] args) {
		Session session = null;
		EntityManager em = null;

		// entityManagerFactory to be supported
		// if (entityManagerFactory != null) {
		// em = entityManagerFactory.createEntityManager();
		// session = em.unwrap(Session.class);
		// } else

		if (sessionFactory != null)
			session = sessionFactory.getCurrentSession();

		try {
			switch (methodId) {
			case 101:
				return session.save(args[0]);
			case 102:
				session.saveOrUpdate(args[0]);
				return null;
			case 103:
				return session.get(clz, (Serializable) args[0]);
			case 104:
				CriteriaQuery<?> criteriaQuery = session.getCriteriaBuilder().createQuery(clz);
				criteriaQuery.from(clz);

				return session.createQuery(criteriaQuery).getResultList();
			case 105:
				session.update(args[0]);
				return null;
			case 106:
				session.delete(args[0]);
				return null;
			case 107:
				for (T t : (Collection<T>) args[0])
					session.delete(t);
				return null;
			case 108:
				session.evict(args[0]);
				return null;

			case 1:
				session.clear();
				return null;
			case 2:
				return session.contains(args[0]);
			case 3:
				session.evict(args[0]);
				return null;
			case 4:
				return find(session, (String) args[0], (Object[]) null);
			case 5:
				return find(session, (String) args[0], new Object[] { args[1] });
			case 6:
				return find(session, (String) args[0], (Object[]) args[1]);
			case 7:
				return findByCriteria(session, (DetachedCriteria) args[0], -1, -1);
			case 8:
				return findByCriteria(session, (DetachedCriteria) args[0], (Integer) args[1], (Integer) args[2]);
			case 9:
				return findByNamedParam(session, (String) args[0], new String[] { (String) args[1] },
						new Object[] { args[2] });
			case 10:
				return findByNamedParam(session, (String) args[0], (String[]) args[1], (Object[]) args[2]);
			case 11:
				return findByNamedQuery(session, (String) args[0], (Object[]) null);
			case 12:
				return findByNamedQuery(session, (String) args[0], new Object[] { args[1] });
			case 13:
				return findByNamedQuery(session, (String) args[0], (Object[]) args[1]);
			case 14:
				return findByNamedQueryAndNamedParam(session, (String) args[0], new String[] { (String) args[1] },
						new Object[] { args[2] });
			case 15:
				return findByNamedQueryAndNamedParam(session, (String) args[0], (String[]) args[1], (Object[]) args[2]);
			case 16:
				session.flush();
				return null;
			case 17:
				session.load(args[0], (Serializable) args[1]);
				return null;
			case 18:
				return session.merge(args[0]);
			case 19:
				session.persist(args[0]);
				return null;
			case 20:
				session.refresh(args[0]);
				return null;
			case 21:
				session.replicate(args[0], (ReplicationMode) args[1]);
				return null;
			}

			return null;
		} finally {
			if (em != null)
				em.close();
		}
	}

	@Override
	public boolean match(Object[] args)
			throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		return true;
	}
}
