package com.aggrepoint.winlet;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.aggrepoint.winlet.form.Form;
import com.aggrepoint.winlet.form.FormImpl;
import com.aggrepoint.winlet.spring.WinletRequestWrapper;
import com.aggrepoint.winlet.spring.def.ReturnDef;
import com.aggrepoint.winlet.spring.def.WinletDef;
import com.aggrepoint.winlet.utils.BufferedResponse;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ReqInfoImpl implements ReqConst, ReqInfo {
	private static long REQUEST_ID = 0;

	private long requestId;

	private HttpServletRequest request;
	// 遇到当执行了一定逻辑后，request.getSession(true)会不定期返回null
	private HttpSession session;
	private String requestPath;
	private String path;
	private String pageId;
	private String pageUrl;
	private String actionId;
	private String validateFieldName;
	private String validateFieldValue;
	private String validateFieldId;
	private boolean pageRefresh;
	private Form form;
	private PageStorage ps;
	private SharedPageStorage sps;
	private ReturnDef rd;
	private WinletDef winletDef;
	private Object winlet;
	private boolean noPreload;
	private boolean isFromContainer;

	// 待移植
	public boolean m_bUseAjax = true;

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

		noPreload = getParameter(PARAM_NO_PRELOAD, null) != null;
		isFromContainer = "y".equalsIgnoreCase(getParameter(
				PARAM_REQ_FROM_CONTAINER, ""));

		pageId = getParameter(PARAM_PAGE_PATH, null);
		if (pageId == null)
			pageId = request.getRequestURI();

		pageUrl = getParameter(PARAM_PAGE_URL, null);
		if (pageUrl == null)
			pageUrl = request.getRequestURL().toString();

		actionId = getParameter(PARAM_WIN_ACTION, null);
		if (actionId == null) {
			pageRefresh = "yes".equalsIgnoreCase(getParameter(
					PARAM_PAGE_REFRESH, ""));
		}

		validateFieldName = getParameter(PARAM_WIN_VALIDATE_FIELD, null);
		if (validateFieldName != null) {
			validateFieldValue = getParameter(PARAM_WIN_VALIDATE_FIELD_VALUE,
					"");
			validateFieldId = getParameter(PARAM_WIN_VALIDATE_FIELD_ID, "");
		}

		form = new FormImpl(this);

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

	public void setReturnDef(ReturnDef rd) {
		this.rd = rd;
	}

	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	@Override
	public HttpSession getSession() {
		HttpSession s = request.getSession();
		if (s != null)
			session = s;
		return session;
	}

	@Override
	public HttpSession getSession(boolean create) {
		HttpSession s = request.getSession(create);
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
	public String getPageId() {
		return pageId;
	}

	@Override
	public String getPageUrl() {
		return pageUrl;
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
	public String getWindowUrl(WinletDef winletDef, String window) {
		String requestPath = null;
		if (window == null) { // 执行完action后获取window的内容时不指定window参数
			requestPath = this.requestPath;
		} else {
			if (winletDef == null) // 执行当前winlet的其他window方法时不用指定winletDef参数
				requestPath = "/" + this.winletDef.getName();
			else
				requestPath = "/" + winletDef.getName();
			requestPath = requestPath + "/" + window;
		}
		return requestPath;
	}

	public static String updateScriptWinletReference(Long wid, String str) {
		if (wid != null) {
			str = str.replaceAll("win\\$\\.post\\s*\\(", "win\\$._post(" + wid
					+ ", null, ");
			str = str.replaceAll("win\\$\\.embed\\s*\\(", "win\\$._post(" + wid
					+ ", ");
			str = str.replaceAll("win\\$\\.winlet\\s*\\(", "win\\$._winlet("
					+ wid);
			str = str.replaceAll("win\\$\\.ajax\\s*\\(", "win\\$._ajax(" + wid
					+ ", ");
			str = str.replaceAll("win\\$\\.get\\s*\\(", "win\\$._get(" + wid
					+ ", ");
			str = str.replaceAll("win\\$\\.toggle\\s*\\(", "win\\$._toggle("
					+ wid + ", ");
			str = str.replaceAll("win\\$\\.url\\s*\\(", "win\\$._url(" + wid
					+ ", ");
			str = str.replaceAll("win\\$\\.submit\\s*\\(", "win\\$._submit("
					+ wid + ", ");
			str = str.replaceAll("win\\$\\.find\\s*\\(", "win\\$._find("
					+ wid + ", ");
			str = str.replaceAll("win\\$\\.wait\\s*\\(", "win\\$._wait("
					+ wid + ", ");
			str = str.replaceAll("win\\$\\.aftersubmit\\s*\\(",
					"win\\$._aftersubmit(" + wid + ", ");
		}

		return str;
	}

	@Override
	public String getWindowContent(Long wid, String windowUrl,
			Map<String, String> params, Map<String, Object> attributes)
			throws Exception {
		LogInfoImpl log = ContextUtils.getLogInfo(request);

		HashMap<String, String> reqParams = new HashMap<String, String>();
		if (params != null) // 执行完action后获取window的内容时不指定params参数
			reqParams.putAll(params);
		reqParams.put(PARAM_WIN_ACTION, null);

		if (windowUrl == null)
			windowUrl = this.requestPath;

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
					.getRequestDispatcher(windowUrl)
					.forward(
							new WinletRequestWrapper(request, null, reqParams,
									attributes), response);
		} finally {
			// 恢复当前请求的ReqInfo
			ContextUtils.setReqInfo(this);
			// 恢复当前请求的LogInfo
			ContextUtils.setLogInfo(request, log);
		}
		byte[] bytes = response.getBuffered();

		String str = bytes == null ? "" : new String(bytes, "UTF-8");
		return updateScriptWinletReference(wid, str);
	}

	@Override
	public Object getWinlet() {
		return winlet;
	}

	@Override
	public void setWinlet(WinletDef def, Object winlet) {
		this.winletDef = def;
		this.winlet = winlet;
	}

	@Override
	public WinletDef getWinletDef() {
		return winletDef;
	}

	@Override
	public boolean noPreload() {
		return noPreload;
	}

	@Override
	public boolean isFromContainer() {
		return isFromContainer;
	}
}
