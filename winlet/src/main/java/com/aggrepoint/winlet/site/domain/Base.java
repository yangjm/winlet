package com.aggrepoint.winlet.site.domain;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Base implements Comparable<Base> {
	static final Log logger = LogFactory.getLog(Page.class);

	protected String dir;
	protected int order;
	protected String name;
	/** 名字的配置名称。优先级高于name */
	protected String nameCfg;
	protected String rule;

	public int compareTo(Base o) {
		return order - o.order;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNameCfg() {
		return nameCfg;
	}

	public void setNameCfg(String nameCfg) {
		this.nameCfg = nameCfg;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
}
