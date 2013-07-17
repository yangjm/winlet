package com.aggrepoint.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.hibernate.ReplicationMode;
import org.hibernate.criterion.DetachedCriteria;
import org.springframework.dao.DataAccessException;

/**
 * @see org.springframework.orm.hibernate3.HibernateOperations
 */
public interface HibernateDao<T> {
	void clear() throws DataAccessException;

	boolean contains(T entity) throws DataAccessException;

	void delete(T entity) throws DataAccessException;

	void deleteAll(Collection<T> entities) throws DataAccessException;

	void evict(T entity) throws DataAccessException;

	List<T> find(String queryString) throws DataAccessException;

	List<T> find(String queryString, Object value) throws DataAccessException;

	List<T> find(String queryString, Object... values)
			throws DataAccessException;

	List<T> findByCriteria(DetachedCriteria criteria)
			throws DataAccessException;

	List<T> findByCriteria(DetachedCriteria criteria, int firstResult,
			int maxResults) throws DataAccessException;

	List<T> findByNamedParam(String queryString, String paramName, Object value)
			throws DataAccessException;

	List<T> findByNamedParam(String queryString, String[] paramNames,
			Object[] values) throws DataAccessException;

	List<T> findByNamedQuery(String queryName) throws DataAccessException;

	List<T> findByNamedQuery(String queryName, Object value)
			throws DataAccessException;

	List<T> findByNamedQuery(String queryName, Object... values)
			throws DataAccessException;

	List<T> findByNamedQueryAndNamedParam(String queryName, String paramName,
			Object value) throws DataAccessException;

	List<T> findByNamedQueryAndNamedParam(String queryName,
			String[] paramNames, Object[] values) throws DataAccessException;

	void flush() throws DataAccessException;

	T get(Serializable id) throws DataAccessException;

	void load(T entity, Serializable id) throws DataAccessException;

	List<T> loadAll() throws DataAccessException;

	T merge(T entity) throws DataAccessException;

	void persist(T entity) throws DataAccessException;

	void refresh(T entity) throws DataAccessException;

	void replicate(T entity, ReplicationMode replicationMode)
			throws DataAccessException;

	Serializable save(T entity) throws DataAccessException;

	void saveOrUpdate(T entity) throws DataAccessException;

	void update(T entity) throws DataAccessException;
}
