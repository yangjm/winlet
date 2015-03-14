package com.aggrepoint.winlet;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.owasp.esapi.errors.ValidationException;

import com.aggrepoint.winlet.form.Form;
import com.aggrepoint.winlet.form.FormImpl;
import com.aggrepoint.winlet.spring.RequestAttributeRecorder;
import com.aggrepoint.winlet.spring.WinletRequestWrapper;
import com.aggrepoint.winlet.spring.def.ReturnDef;
import com.aggrepoint.winlet.spring.def.WinletDef;
import com.aggrepoint.winlet.utils.BufferedResponse;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ReqInfoImpl implements ReqConst, ReqInfo {
	private static final String REQ_PARAMETERS_FROM_ACTION_KEY = ".req.param.from.action";

	private static long REQUEST_ID = 0;

	private long requestId;

	private HttpServletRequest request;
	// 遇到当执行了一定逻辑后，request.getSession(true)会不定期返回null
	private HttpSession session;
	String requestPath;
	private String path;
	private String rootWindowId;
	private String windowId;
	private String pageId;
	private String pageUrl;
	private String actionId;
	private String validateFieldName;
	private String validateFieldValue;
	private String validateFieldId;
	private boolean pageRefresh;
	private String translateUpdate;
	private WindowInstance wi;
	private Form form;
	private PageStorage ps;
	private SharedPageStorage sps;
	private ReturnDef rd;

	// 待移植
	public boolean m_bUseAjax = true;

	/**
	 * 用于分解Action或Resource
	 */
	Pattern P_DECODE = Pattern.compile("([^!]*)!([^!]+)");

	public ReqInfoImpl(HttpServletRequest request, String path) {
		this.request = request;
		this.session = request.getSession(true);
		this.path = request.getContextPath() + path;

		// 将IncludeTag需要获得的当前Winlet的RequestPath取出保存
		// 发现如果在WinletDispatcherServlet中用HttpServletRequestWrapper的派生类将request封装，请求forward到JSP页面后
		// ，用ReqInfoImpl中保存的request对象的getRequestURI()方法只能获得当前JSP的URI而不是期望的Winlet
		// URI。如果不封装，或者封装类不从HttpServletRequestWrapper派生，则ReqInfoImpl中request.getRequestURI()可以正常工作。为了避免这个问题，这里直接将requestURI取出保存。
		requestPath = request.getRequestURI();
		int idx = requestPath.indexOf("/", 1);
		requestPath = requestPath.substring(idx);

		requestId = REQUEST_ID++;

		pageId = getParameter(PARAM_PAGE_PATH, null);
		if (pageId == null)
			pageId = request.getRequestURI();

		pageUrl = getParameter(PARAM_PAGE_URL, null);
		if (pageUrl == null)
			pageUrl = request.getRequestURL().toString();

		windowId = request.getHeader(WinletConst.REQUEST_HEADER_WINDOW_ID);
		if (windowId == null || windowId.equals(""))
			windowId = getParameter(PARAM_WIN_ID, null);

		actionId = getParameter(PARAM_WIN_ACTION, null);
		if (actionId != null) {
			Matcher m;
			synchronized (P_DECODE) {
				m = P_DECODE.matcher(actionId);
			}

			if (m.find()) {
				try {
					windowId = m.group(1);
				} catch (Exception e) {
				}
				actionId = m.group(2);
			}
		} else {
			pageRefresh = "yes".equalsIgnoreCase(getParameter(
					PARAM_PAGE_REFRESH, ""));
		}

		validateFieldName = getParameter(PARAM_WIN_VALIDATE_FIELD, null);
		if (validateFieldName != null) {
			validateFieldValue = getParameter(PARAM_WIN_VALIDATE_FIELD_VALUE,
					"");
			validateFieldId = getParameter(PARAM_WIN_VALIDATE_FIELD_ID, "");
		}

		translateUpdate = getParameter(PARAM_TRANSLATE_UPDATE, null);

		form = new FormImpl(this);

		rootWindowId = WindowInstance.getRootWindowId(windowId);

		ContextUtils.setReqInfo(this);
	}

	@Override
	public String getParameter(String name, String def) {
		String str;

		str = request.getParameter(name);
		if (str == null)
			return def;
		return str.trim();
	}

	@Override
	public int getParameter(String name, int def) {
		return Integer.parseInt(getParameter(name, Integer.toString(def)));
	}

	@Override
	public long getParameter(String name, long def) {
		return Long.parseLong(getParameter(name, Long.toString(def)));
	}

	/**
	 * Save the request attributes set by action to session in order to pass
	 * them to window request
	 */
	public void saveActionRequestParameters() {
		HttpSession ses = getSession();

		if (actionId == null || wi == null || request == null || ses == null
				|| !(request instanceof RequestAttributeRecorder))
			return;

		ses.setAttribute(wi.getWinlet().toString()
				+ REQ_PARAMETERS_FROM_ACTION_KEY,
				((RequestAttributeRecorder) request).getRecorded());
	}

	public void setWindowIntance(WindowInstance wi) {
		HttpSession ses = getSession();

		this.wi = wi;

		// { retrieve attributes passed to window by action
		if (actionId == null && request != null && ses != null) {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> attrs = (HashMap<String, Object>) ses
					.getAttribute(wi.getWinlet().toString()
							+ REQ_PARAMETERS_FROM_ACTION_KEY);
			if (attrs != null) {
				for (String key : attrs.keySet())
					request.setAttribute(key, attrs.get(key));
				ses.removeAttribute(wi.getWinlet().toString()
						+ REQ_PARAMETERS_FROM_ACTION_KEY);
			}
		}
		// }
	}

	public void setReturnDef(ReturnDef rd) {
		this.rd = rd;
	}

	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	public String getRequestPath() throws ValidationException {
		String str = request.getHeader(WinletConst.REQUEST_HEADER_REQ_PATH);
		if (str == null || str.equals(""))
			str = requestPath;
		return str;
	}

	@Override
	public HttpSession getSession() {
		HttpSession s = request.getSession();
		if (s != null)
			session = s;
		return session;
	}

	@Override
	public UserProfile getUser() {
		return ContextUtils.getUser(request);
	}

	@Override
	public String getPath() {
		return path;
	}

	@Override
	public long getRequestId() {
		return requestId;
	}

	@Override
	public String getRootWindowId() {
		return rootWindowId;
	}

	@Override
	public String getPageId() {
		return pageId;
	}

	@Override
	public String getPageUrl() {
		return pageUrl;
	}

	@Override
	public String getWindowId() {
		return windowId;
	}

	@Override
	public String getActionId() {
		return actionId;
	}

	@Override
	public Form getForm() {
		return form;
	}

	@Override
	public boolean isValidateField() {
		return validateFieldName != null;
	}

	@Override
	public String getValidateFieldName() {
		return validateFieldName;
	}

	@Override
	public String getValidateFieldValue() {
		return validateFieldValue;
	}

	@Override
	public String getValidateFieldId() {
		return validateFieldId;
	}

	@Override
	public boolean isPageRefresh() {
		return pageRefresh;
	}

	@Override
	public String getTranslateUpdate() {
		return translateUpdate;
	}

	@Override
	public WindowInstance getWindowInstance() {
		return wi;
	}

	@Override
	public PageStorage getPageStorage() {
		if (ps == null)
			ps = new PageStorageImpl(this);
		return ps;
	}

	@Override
	public SharedPageStorage getSharedPageStorage() {
		if (sps == null)
			sps = new SharedPageStorageImpl(this);
		return sps;
	}

	@Override
	public ReturnDef getReturnDef() {
		return rd;
	}

	@Override
	public IncludeResult include(WinletDef winletDef, String window,
			Hashtable<String, String> params, String uniqueId) throws Exception {
		LogInfoImpl log = ContextUtils.getLogInfo(request);

		WindowInstance wi = getWindowInstance();

		WindowInstance childWindow = wi.addSub(this, winletDef == null ? null
				: WinletManager.getWinlet(Context.get(), this, winletDef),
				window, null, uniqueId);
		childWindow.setParams(params);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(WinletConst.REQUEST_HEADER_WINDOW_ID, childWindow.getId());
		headers.put(WinletConst.REQUEST_HEADER_REQ_PATH, getRequestPath());

		HashMap<String, String> reqParams = new HashMap<String, String>();
		reqParams.putAll(params);
		reqParams.put(PARAM_WIN_ACTION, null);

		BufferedResponse response = new BufferedResponse();

		// 注：
		// 这里使用forward而不是include，因为使用include的情况下在被include对象
		// 中使用getRequestURI()等方法获得的是当前的URI而不是被include对象的URI，
		// 因此ADK无法正确判断被include的资源。使用forward则不存在这个问题。因为已经
		// 使用了responseWrapper，因此用forward也是可行的。
		try {
			// 避免被包含的功能改变当前LogInfo
			ContextUtils.setLogInfo(request, null);
			request.getServletContext()
					.getRequestDispatcher(getRequestPath())
					.forward(
							new WinletRequestWrapper(request, headers,
									reqParams), response);
		} finally {
			// 恢复当前请求的ReqInfo
			ContextUtils.setReqInfo(this);
			// 恢复当前请求的LogInfo
			ContextUtils.setLogInfo(request, log);
		}
		byte[] bytes = response.getBuffered();

		String str = bytes == null ? "" : new String(bytes, "UTF-8");

		// { 必须在这里替换好win$.的调用，因为客户端js不会正确使用子窗口的wid
		str = str.replaceAll("win\\$\\.wid\\s*\\(.*\\)", "win\\$._wid("
				+ childWindow.getId() + ")");
		str = str.replaceAll("win\\$\\.post\\s*\\(", "win\\$._post("
				+ childWindow.getId() + ", ");
		str = str.replaceAll("win\\$\\.ajax\\s*\\(", "win\\$._ajax("
				+ childWindow.getId() + ", ");
		str = str.replaceAll("win\\$\\.get\\s*\\(", "win\\$._get("
				+ childWindow.getId() + ", ");
		str = str.replaceAll("win\\$\\.toggle\\s*\\(", "win\\$._toggle("
				+ childWindow.getId() + ", ");
		str = str.replaceAll("win\\$\\.url\\s*\\(", "win\\$._url("
				+ childWindow.getId() + ", ");
		str = str.replaceAll("win\\$\\.submit\\s*\\(", "win\\$._submit("
				+ childWindow.getId() + ", ");
		str = str.replaceAll("win\\$\\.aftersubmit\\s*\\(",
				"win\\$._aftersubmit(" + childWindow.getId() + ", ");
		// }

		return new IncludeResult(childWindow, str);
	}
}
