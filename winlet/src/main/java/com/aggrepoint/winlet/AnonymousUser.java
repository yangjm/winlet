package com.aggrepoint.winlet;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class AnonymousUser implements UserProfile {
	private static final long serialVersionUID = 1L;
	static AnonymousUser instance = new AnonymousUser();

	private AnonymousUser() {
	}

	@Override
	public boolean isAnonymous() {
		return true;
	}

	@Override
	public String getLoginId() {
		return "";
	}

	@Override
	public String getName() {
		return "";
	}

	public static AnonymousUser getInstance() {
		return instance;
	}
}
