package com.aggrepoint.dao;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public interface DaoService<T> {
	default Serializable create(T entity) throws DataAccessException {
		return null;
	}

	default void createOrUpdate(T entity) throws DataAccessException {
	}

	default T find(Serializable id) throws DataAccessException {
		return null;
	}

	default List<T> find() throws DataAccessException {
		return null;
	}

	default void update(T entity) throws DataAccessException {
	}

	default void delete(T entity) throws DataAccessException {
	}

	default void delete(Collection<T> entities) throws DataAccessException {
	}
}
