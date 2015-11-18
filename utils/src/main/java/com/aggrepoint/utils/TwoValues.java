package com.aggrepoint.utils;

public class TwoValues<U, V> {
	private U one;
	private V two;

	public TwoValues(U u, V v) {
		one = u;
		two = v;
	}

	public U getOne() {
		return one;
	}

	public void setOne(U one) {
		this.one = one;
	}

	public V getTwo() {
		return two;
	}

	public void setTwo(V two) {
		this.two = two;
	}
}
