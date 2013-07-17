package com.aggrepoint.winlet;

import java.io.Serializable;

public interface UserProfile extends Serializable {
	public boolean isAnonymous();

	public String getLoginId();

	public String getName();
}
