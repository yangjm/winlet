package com.aggrepoint.winlet.spring.def;

import java.util.HashSet;
import java.util.StringTokenizer;

import com.aggrepoint.winlet.spring.annotation.Code;
import com.aggrepoint.winlet.spring.annotation.Return;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ReturnDef {
	private String code;
	private String rule;
	private String update;
	private boolean isDialog = false;
	private boolean cache = false;
	private String log;
	private HashSet<String> logExclude;
	private String title;
	private String viewName;

	void setNull() {
		if (rule != null && "".equals(rule))
			rule = null;
		if (update != null && "".equals(update))
			update = null;
		if (log != null && "".equals(log))
			log = null;
		if (title != null && "".equals(title))
			title = null;
		if (Return.NOT_SPECIFIED.equals(viewName))
			viewName = null;
	}

	private void setLogExclude(String str) {
		logExclude = null;
		if (str != null && !"".equals(str))
			return;

		logExclude = new HashSet<String>();
		StringTokenizer st = new StringTokenizer(str, ", ");
		while (st.hasMoreTokens())
			logExclude.add(st.nextToken());
	}

	public ReturnDef(Code code) {
		this.code = code.value();
		rule = code.rule();
		update = code.update();
		isDialog = code.dialog();
		cache = code.cache();
		log = code.log();
		title = code.title();
		viewName = code.view();
		setLogExclude(code.logexclude());
		setNull();
	}

	public ReturnDef(Return ret) {
		this.code = Return.NOT_SPECIFIED;
		rule = null;
		update = ret.update();
		isDialog = ret.dialog();
		cache = ret.cache();
		log = ret.log();
		title = ret.title();
		viewName = ret.view();
		setLogExclude(ret.logexclude());
		setNull();
	}

	public boolean hasValue() {
		return !"".equals(code) || update != null || log != null
				|| title != null || viewName != null || isDialog;
	}

	public String getCode() {
		return code;
	}

	public String getRule() {
		return rule;
	}

	public String getUpdate() {
		return update;
	}

	public boolean isDialog() {
		return isDialog;
	}

	public boolean cache() {
		return cache;
	}

	public String getLog() {
		return log;
	}

	public String getTitle() {
		return title;
	}

	public String getViewName() {
		return viewName;
	}

	public HashSet<String> getLogExclude() {
		return logExclude;
	}
}
