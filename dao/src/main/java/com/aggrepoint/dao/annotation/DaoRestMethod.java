package com.aggrepoint.dao.annotation;

public enum DaoRestMethod {
	create, createOrUpdate, delete, find, findAll, update;

	public static DaoRestMethod fromClassName(String name) {
		for (DaoRestMethod m : values()) {
			if ((m.getClass().getName() + "." + m.name()).equals(name))
				return m;
		}
		return null;
	}
}
