package com.aggrepoint.utils;

public class ThreeValues<U, V, K> extends TwoValues<U, V> {
	private static final long serialVersionUID = 1L;

	private K three;

	public ThreeValues(U u, V v, K k) {
		super(u, v);
		three = k;
	}

	public K getThree() {
		return three;
	}

	public void setThree(K three) {
		this.three = three;
	}
}
