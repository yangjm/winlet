package com.aggrepoint.winlet;

import java.util.Map;
import java.util.function.Consumer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.aggrepoint.winlet.form.Form;
import com.aggrepoint.winlet.spring.def.ReturnDef;
import com.aggrepoint.winlet.spring.def.WinletDef;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface ReqInfo {
	String getParameter(String name, String def);

	int getParameter(String name, int def);

	long getParameter(String name, long def);

	HttpServletRequest getRequest();

	HttpSession getSession();

	HttpSession getSession(boolean create);

	UserProfile getUser();

	String getPath();

	long getRequestId();

	String getPageId();

	String getPageUrl();

	String getActionId();

	Form getForm();

	boolean isValidateField();

	String getValidateFieldName();

	String getValidateFieldValue();

	String getValidateFieldId();

	boolean isPageRefresh();

	PageStorage getPageStorage();

	SharedPageStorage getSharedPageStorage();

	ReturnDef getReturnDef();

	void setWinlet(WinletDef def, Object winlet);

	Object getWinlet();

	WinletDef getWinletDef();

	String getWindowContent(Long wid, String windowUrl,
			Map<String, String> params, Map<String, Object> attributes,
			Consumer<StaticUrlProvider> returnProvider) throws Exception;

	String getWindowUrl(WinletDef winletDef, String window);

	boolean noPreload();

	boolean isFromContainer();

	boolean isFirstInclude();

	String getRemoteDomain();

	boolean isCrossDomain();

	public String getContextPath();
}