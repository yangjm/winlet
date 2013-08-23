package com.aggrepoint.dao;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import com.aggrepoint.jpa.CreatedBy;
import com.aggrepoint.jpa.CreatedDate;
import com.aggrepoint.jpa.UpdatedBy;
import com.aggrepoint.jpa.UpdatedDate;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 *
 */
public class AuditTrailInterceptor extends EmptyInterceptor {
	private static final long serialVersionUID = 1L;

	static class FieldDef {
		String fieldName;
		Class<?> annotationType;
		Class<?> valueType;

		public FieldDef(String name, Class<?> ann, Class<?> type) {
			fieldName = name;
			annotationType = ann;
			valueType = type;
		}
	}

	static Map<Class<?>, HashMap<String, FieldDef>> AUDIT_FIELDS = new HashMap<Class<?>, HashMap<String, FieldDef>>();

	static HashMap<String, FieldDef> getAuditFields(Object entity,
			String[] propertyNames) {
		if (AUDIT_FIELDS.containsKey(entity.getClass()))
			return AUDIT_FIELDS.get(entity.getClass());

		HashMap<String, FieldDef> auditFields = new HashMap<String, FieldDef>();

		// 时间关系，只支持在get方法上加入的注解
		for (Method m : entity.getClass().getMethods()) {
			String propName = null;

			for (String prop : propertyNames)
				if (("get" + prop).equalsIgnoreCase(m.getName())) {
					propName = prop;
					break;
				}

			if (propName == null)
				continue;

			for (Annotation ann : m.getAnnotations())
				if (ann instanceof CreatedBy) {
					auditFields.put(propName, new FieldDef(propName,
							CreatedBy.class, m.getReturnType()));
					break;
				} else if (ann instanceof UpdatedBy) {
					auditFields.put(propName, new FieldDef(propName,
							UpdatedBy.class, m.getReturnType()));
					break;
				} else if (ann instanceof CreatedDate) {
					auditFields.put(propName, new FieldDef(propName,
							CreatedDate.class, m.getReturnType()));
					break;
				} else if (ann instanceof UpdatedDate) {
					auditFields.put(propName, new FieldDef(propName,
							UpdatedDate.class, m.getReturnType()));
					break;
				}
		}

		AUDIT_FIELDS.put(entity.getClass(), auditFields);

		return auditFields;
	}

	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		HashMap<String, FieldDef> auditFields = getAuditFields(entity,
				propertyNames);

		boolean modified = false;
		for (String prop : auditFields.keySet()) {
			FieldDef def = auditFields.get(prop);

			if (def.annotationType == UpdatedBy.class) {
				setUser(currentState, propertyNames, prop);
				modified = true;
			} else if (def.annotationType == UpdatedDate.class) {
				setDate(currentState, propertyNames, prop, def.valueType);
				modified = true;
			}
		}

		return modified;
	}

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		HashMap<String, FieldDef> auditFields = getAuditFields(entity,
				propertyNames);

		boolean modified = false;
		for (String prop : auditFields.keySet()) {
			FieldDef def = auditFields.get(prop);

			if (def.annotationType == CreatedBy.class) {
				setUser(state, propertyNames, prop);
				modified = true;
			} else if (def.annotationType == UpdatedBy.class) {
				setUser(state, propertyNames, prop);
				modified = true;
			} else if (def.annotationType == CreatedDate.class) {
				setDate(state, propertyNames, prop, def.valueType);
				modified = true;
			} else if (def.annotationType == UpdatedDate.class) {
				setDate(state, propertyNames, prop, def.valueType);
				modified = true;
			}
		}

		return modified;
	}

	private void setUser(Object[] currentState, String[] propertyNames,
			String propertyToSet) {
		for (int i = 0; i < propertyNames.length; i++)
			if (propertyNames[i].equals(propertyToSet)) {
				currentState[i] = UserContext.getUser();
				break;
			}
	}

	private void setDate(Object[] currentState, String[] propertyNames,
			String propertyToSet, Class<?> type) {
		for (int i = 0; i < propertyNames.length; i++)
			if (propertyNames[i].equals(propertyToSet)) {
				if (java.sql.Timestamp.class.isAssignableFrom(type)) {
					currentState[i] = new java.sql.Timestamp(
							System.currentTimeMillis());
					break;
				} else if (java.sql.Date.class.isAssignableFrom(type)) {
					currentState[i] = new java.sql.Date(
							System.currentTimeMillis());
					break;
				} else if (java.util.Date.class.isAssignableFrom(type)) {
					currentState[i] = new java.util.Date();
					break;
				}
			}
	}
}
