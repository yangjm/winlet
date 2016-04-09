package com.aggrepoint.winlet.plugin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class AuthConfig {
	/** 系统中定义的角色 */
	private List<String> roles;
	/** Winlet访问规则定义 */
	private List<AuthCfgWinlet> winlets;
	/** 配置的时间戳。用于实现在系统运行过程中动态修改配置。时间戳变更表示配置变更 */
	@JsonIgnore
	private long timestamp;

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<AuthCfgWinlet> getWinlets() {
		return winlets;
	}

	public void setWinlets(List<AuthCfgWinlet> winlets) {
		this.winlets = winlets;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
