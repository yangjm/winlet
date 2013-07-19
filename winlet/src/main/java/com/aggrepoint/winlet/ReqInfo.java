package com.aggrepoint.winlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.aggrepoint.winlet.form.FormImpl;
import com.aggrepoint.winlet.form.InputImpl;
import com.aggrepoint.winlet.spring.def.ReturnDef;

public interface ReqInfo {

	public abstract String getParameter(HttpServletRequest request,
			String name, String def);

	public abstract HttpServletRequest getRequest();

	public abstract HttpSession getSession();

	public abstract UserProfile getUser();

	public abstract String getPath();

	public abstract long getRequestId();

	public abstract String getWinId();

	public abstract String getPageId();

	public abstract String getViewId();

	public abstract String getActionId();

	public abstract String getFormId();

	public abstract boolean isValidateField();

	public abstract String getValidateFieldName();

	public abstract String getValidateFieldValue();

	public abstract ViewInstance getViewInstance();

	public abstract FormImpl getForm();

	public abstract InputImpl getValidateField();

	public abstract PageStorage getPageStorage();

	public abstract ReturnDef getReturnDef();

}