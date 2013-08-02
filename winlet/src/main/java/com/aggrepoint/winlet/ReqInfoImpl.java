package com.aggrepoint.winlet;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import com.aggrepoint.winlet.form.FormImpl;
import com.aggrepoint.winlet.form.InputImpl;
import com.aggrepoint.winlet.spring.def.ReturnDef;

public class ReqInfoImpl implements ReqConst, ReqInfo {
	private static long REQUEST_ID = 0;

	private long requestId;

	private HttpServletRequest request;
	// 遇到当执行了一定逻辑后，request.getSession(true)会不定期返回null
	private HttpSession session;
	private String path;
	private String winId;
	private String pageId;
	private String pageUrl;
	private String viewId;
	private String actionId;
	private String formId;
	private String winRes;
	private String validateFieldName;
	private String validateFieldValue;
	private boolean pageRefresh;
	private ViewInstance vi;
	private FormImpl form;
	private PageStorage ws;
	private ReturnDef rd;

	// 待移植
	public boolean m_bUseAjax = true;

	/**
	 * 用于分解Action或Resource
	 */
	Pattern P_DECODE = Pattern.compile("([^!]*)!([^!]*)!([^!]+)(?:!(.*))?");

	public ReqInfoImpl(HttpServletRequest request, String path) {
		this.request = request;
		this.session = request.getSession(true);
		this.path = request.getContextPath() + path;

		requestId = REQUEST_ID++;

		pageId = getParameter(request, PARAM_PAGE_PATH, null);
		if (pageId == null)
			pageId = request.getRequestURI();

		pageUrl = getParameter(request, PARAM_PAGE_URL, null);
		if (pageUrl == null)
			pageUrl = request.getRequestURL().toString();

		winId = getParameter(request, PARAM_WIN_ID, "");
		viewId = getParameter(request, PARAM_WIN_VIEW, "");
		if ("".equals(viewId))
			viewId = winId;

		actionId = getParameter(request, PARAM_WIN_ACTION, null);
		if (actionId != null) {
			Matcher m;
			synchronized (P_DECODE) {
				m = P_DECODE.matcher(actionId);
			}

			if (m.find()) {
				try {
					winId = m.group(1);
					viewId = m.group(2);
				} catch (Exception e) {
				}
				actionId = m.group(3);

				if (m.groupCount() > 3)
					formId = m.group(4);
			}
		} else {
			pageRefresh = "yes".equalsIgnoreCase(getParameter(request,
					PARAM_PAGE_REFRESH, ""));
		}

		if (formId != null && !formId.trim().equals("")) {
			validateFieldName = getParameter(request, PARAM_WIN_VALIDATE_FIELD,
					null);
			if (validateFieldName != null)
				validateFieldValue = getParameter(request,
						PARAM_WIN_VALIDATE_FIELD_VALUE, "");
		}

		winRes = getParameter(request, PARAM_WIN_RES, null);
		if (winRes != null) {
			Matcher m;
			synchronized (P_DECODE) {
				m = P_DECODE.matcher(winRes);
			}
			if (m.find()) {
				try {
					winId = m.group(1);
					viewId = m.group(2);
				} catch (Exception e) {
				}
				winRes = m.group(3);
			}
		}

		RequestContextHolder.currentRequestAttributes().setAttribute(
				WinletConst.REQUEST_ATTR_REQUEST, this,
				RequestAttributes.SCOPE_REQUEST);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getParameter(javax.servlet.http.
	 * HttpServletRequest, java.lang.String, java.lang.String)
	 */
	@Override
	public String getParameter(HttpServletRequest request, String name,
			String def) {
		String str;

		str = request.getParameter(name);
		if (str == null)
			return def;
		return str.trim();
	}

	public void setViewInstance(ViewInstance vi) {
		this.vi = vi;
	}

	public void setForm(FormImpl form) {
		this.form = form;
	}

	public void setReturnDef(ReturnDef rd) {
		this.rd = rd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getRequest()
	 */
	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getSession()
	 */
	@Override
	public HttpSession getSession() {
		return session;
	}

	@Override
	public UserProfile getUser() {
		return ContextUtils.getUser(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getPath()
	 */
	@Override
	public String getPath() {
		return path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getRequestId()
	 */
	@Override
	public long getRequestId() {
		return requestId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getWinId()
	 */
	@Override
	public String getWinId() {
		return winId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getPageId()
	 */
	@Override
	public String getPageId() {
		return pageId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getViewId()
	 */
	@Override
	public String getViewId() {
		return viewId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getActionId()
	 */
	@Override
	public String getActionId() {
		return actionId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getFormId()
	 */
	@Override
	public String getFormId() {
		return formId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#isValidateField()
	 */
	@Override
	public boolean isValidateField() {
		return validateFieldName != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getValidateFieldName()
	 */
	@Override
	public String getValidateFieldName() {
		return validateFieldName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getValidateFieldValue()
	 */
	@Override
	public String getValidateFieldValue() {
		return validateFieldValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#isPageRefresh()
	 */
	@Override
	public boolean isPageRefresh() {
		return pageRefresh;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getViewInstance()
	 */
	@Override
	public ViewInstance getViewInstance() {
		return vi;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getForm()
	 */
	@Override
	public FormImpl getForm() {
		return form;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getValidateField()
	 */
	@Override
	public InputImpl getValidateField() {
		String name = getValidateFieldName();
		if (name == null)
			return null;

		return form.getInputByName(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getWinletStorage()
	 */
	@Override
	public PageStorage getPageStorage() {
		if (ws == null)
			ws = new PageStorageImpl(this);
		return ws;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.aggrepoint.winlet.ReqInfo#getReturnDef()
	 */
	@Override
	public ReturnDef getReturnDef() {
		return rd;
	}
}
