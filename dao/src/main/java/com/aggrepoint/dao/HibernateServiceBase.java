package com.aggrepoint.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public abstract class HibernateServiceBase<T> implements HibernateService<T> {
	public abstract HibernateDao<T> getDao();

	public Serializable create(T entity) throws DataAccessException {
		return getDao().save(entity);
	}

	public void createOrUpdate(T entity) throws DataAccessException {
		getDao().saveOrUpdate(entity);
	}

	public T find(Serializable id) throws DataAccessException {
		return getDao().get(id);
	}

	public List<T> find() throws DataAccessException {
		return getDao().loadAll();
	}

	public void update(T entity) throws DataAccessException {
		getDao().update(entity);
	}

	public void delete(T entity) throws DataAccessException {
		getDao().delete(entity);
	}

	public void delete(Collection<T> entities) throws DataAccessException {
		getDao().deleteAll(entities);
	}
}
