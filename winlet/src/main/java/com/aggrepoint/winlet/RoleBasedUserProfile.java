package com.aggrepoint.winlet;

import java.util.Collection;

public interface RoleBasedUserProfile extends UserProfile {
	/** 是否具备角色 */
	boolean hasRole(String role);

	/** 是否具备任意一个角色 */
	boolean hasRole(Collection<String> role);
}
