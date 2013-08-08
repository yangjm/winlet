package com.aggrepoint.winlet.spring;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ExceptionMapping {
	private Class<? extends Exception> clz;
	private String rule;
	private String view;
	private boolean redirect;

	@SuppressWarnings("unchecked")
	public void setException(String str) throws ClassNotFoundException {
		clz = (Class<? extends Exception>) Class.forName(str);
	}
	
	public String getException() {
		return clz.getName();
	}

	public void setRule(String str) {
		if (str != null && !"".equals(str))
			rule = str;
	}

	public void setView(String str) {
		view = str;
		if (view.startsWith(Const.REDIRECT)) {
			redirect = true;
			view = str.substring(9);
		}
	}

	public boolean isRedirect() {
		return redirect;
	}

	public String getRule() {
		return rule;
	}

	public String getView() {
		return view;
	}

	public Class<? extends Exception> getClz() {
		return clz;
	}
}
