package com.aggrepoint.hibernate;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentityGenerator;
import org.hibernate.type.Type;

/**
 * 若Object的主键值为空或为0，则使用IdentityGenerator生成主键值
 * 
 * @author Jim
 */
public class ZeroIdGenerator extends IdentityGenerator implements Configurable {
	private String entityName;

	@Override
	public Serializable generate(SessionImplementor session, Object obj)
			throws HibernateException {
		if (obj == null)
			throw new HibernateException(new NullPointerException());

		final Serializable id = session.getEntityPersister(entityName, obj)
				.getIdentifier(obj, session);

		if (id == null || id.equals(0))
			return super.generate(session, obj);

		return id;
	}

	@Override
	public void configure(Type type, Properties params, Dialect d)
			throws MappingException {
		entityName = params.getProperty(ENTITY_NAME);
		if (entityName == null) {
			throw new MappingException("no entity name");
		}
	}
}
