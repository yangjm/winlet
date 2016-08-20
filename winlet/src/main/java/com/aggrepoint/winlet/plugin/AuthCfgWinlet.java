package com.aggrepoint.winlet.plugin;

import java.util.List;

public class AuthCfgWinlet {
	/** winlet路径 */
	private String path;
	/** 是否不限制访问 */
	private boolean isPublic;
	/** isPublic为false时：可以访问本winlet的角色 */
	private List<String> roles;
	/** 其中的window和action的访问定义 */
	private List<AuthCfgMethod> methods;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public List<AuthCfgMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<AuthCfgMethod> methods) {
		this.methods = methods;
	}

	public void retianRoles(List<String> roles) {
		if (this.roles != null && roles != null)
			this.roles.retainAll(roles);

		if (methods != null)
			for (AuthCfgMethod method : methods)
				method.retianRoles(roles);
	}
}
