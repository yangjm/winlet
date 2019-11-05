package com.aggrepoint.utils;

public class FourValues<U, V, K, M> extends ThreeValues<U, V, K> {
	private static final long serialVersionUID = 1L;

	private M four;

	public FourValues(U u, V v, K k, M m) {
		super(u, v, k);
		four = m;
	}

	public M getFour() {
		return four;
	}

	public void setFour(M four) {
		this.four = four;
	}
}
