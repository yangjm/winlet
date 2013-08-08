package com.aggrepoint.winlet.site.domain;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Area extends Base {
	private boolean cascade;
	private String content;

	public boolean isCascade() {
		return cascade;
	}

	public void setCascade(boolean cascade) {
		this.cascade = cascade;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
