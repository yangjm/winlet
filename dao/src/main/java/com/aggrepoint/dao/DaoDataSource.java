package com.aggrepoint.dao;

public class DaoDataSource {
	private String classNames;
	private String entityManagerName;
	private String sessionFactoryName;

	public String getClassNames() {
		return classNames;
	}

	public void setClassNames(String classNames) {
		this.classNames = classNames;
	}

	public String getEntityManagerName() {
		return entityManagerName;
	}

	public void setEntityManagerName(String entityManagerName) {
		this.entityManagerName = entityManagerName;
	}

	public String getSessionFactoryName() {
		return sessionFactoryName;
	}

	public void setSessionFactoryName(String sessionFactoryName) {
		this.sessionFactoryName = sessionFactoryName;
	}
}
