package com.aggrepoint.service;

public class TransactionManagerConfig {
	private String name;
	private Class<?>[] clzs;

	public TransactionManagerConfig(String name, Class<?>... clzs) {
		this.name = name;
		this.clzs = clzs;
	}

	public String getName() {
		return name;
	}

	public Class<?>[] getClasses() {
		return clzs;
	}
}
