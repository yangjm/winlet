package com.aggrepoint.winlet.plugin;

import java.util.List;

public class AuthCfgMethod {
	/** Method的路径 */
	private String path;
	/** 是否沿用Winlet的访问规则 */
	private boolean isInherit;
	/** isInherit为false时：是否不限制访问 */
	private boolean isPublic;
	/** isInherit为false并且isPublic为false时：可以访问本method的角色 */
	private List<String> roles;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isInherit() {
		return isInherit;
	}

	public void setInherit(boolean isInherit) {
		this.isInherit = isInherit;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
