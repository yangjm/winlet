package com.aggrepoint.winlet;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.owasp.esapi.errors.ValidationException;

import com.aggrepoint.winlet.form.Form;
import com.aggrepoint.winlet.spring.def.ReturnDef;
import com.aggrepoint.winlet.spring.def.WinletDef;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface ReqInfo {
	public abstract String getParameter(String name, String def);

	public abstract int getParameter(String name, int def);

	public abstract long getParameter(String name, long def);

	public abstract HttpServletRequest getRequest();

	public abstract String getRequestPath() throws ValidationException;

	public abstract HttpSession getSession();

	public abstract HttpSession getSession(boolean create);

	public abstract UserProfile getUser();

	public abstract String getPath();

	public abstract long getRequestId();

	public abstract String getRootWindowId();

	public abstract String getWindowId();

	public abstract String getPageId();

	public abstract String getPageUrl();

	public abstract String getActionId();

	public abstract Form getForm();

	public abstract boolean isValidateField();

	public abstract String getValidateFieldName();

	public abstract String getValidateFieldValue();

	public abstract String getValidateFieldId();

	public abstract boolean isPageRefresh();

	public abstract WindowInstance getWindowInstance();

	public abstract PageStorage getPageStorage();

	public abstract SharedPageStorage getSharedPageStorage();

	public abstract ReturnDef getReturnDef();

	public abstract IncludeResult include(WinletDef winlet, String window,
			Hashtable<String, String> params, String uniqueId) throws Exception;

	String getTranslateUpdate();
}