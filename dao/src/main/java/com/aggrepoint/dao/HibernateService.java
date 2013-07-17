package com.aggrepoint.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;

public interface HibernateService<T> {
	Serializable create(T entity) throws DataAccessException;

	void createOrUpdate(T entity) throws DataAccessException;

	T find(Serializable id) throws DataAccessException;

	List<T> find() throws DataAccessException;

	void update(T entity) throws DataAccessException;

	void delete(T entity) throws DataAccessException;

	void delete(Collection<T> entities) throws DataAccessException;
}
