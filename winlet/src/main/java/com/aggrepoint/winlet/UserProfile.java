package com.aggrepoint.winlet;

import java.io.Serializable;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface UserProfile extends Serializable {
	public boolean isAnonymous();

	public String getLoginId();

	public String getName();
}
