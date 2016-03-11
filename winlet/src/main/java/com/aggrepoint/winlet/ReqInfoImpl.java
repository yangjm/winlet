package com.aggrepoint.winlet;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.aggrepoint.utils.StringUtils;
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
	private String remoteDomain;
	private String actionId;
	private String validateFieldName;
	private String validateFieldValue;
	private String validateFieldId;
	private boolean pageRefresh;
	private boolean firstInclude;
	private Form form;
	private PageStorage ps;
	private SharedPageStorage sps;
	private ReturnDef rd;
	private WinletDef winletDef;
	private Object winlet;
	private boolean noPreload;
	private boolean isFromContainer;
	/** 搜索引擎用_escaped_fargment_参数请求时为true */
	private boolean hashEscaped;
	/** 当hashEscaped = true时：hash中的参数 */
	private HashMap<String, HashMap<String, String>> hashParams;
	/** 当hashEscaped = true时：hash中顶层页面的URL */
	private String topPageUrl;
	/** 当hashEscaped = true时：hash中顶层页面的参数 */
	private HashMap<String, String> topPageParams;

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
		if (idx > 0)
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

		remoteDomain = getParameter(PARAM_WINLET_DOMAIN_NAME, null);

		firstInclude = "yes".equalsIgnoreCase(getParameter(PARAM_FIRST_INCLUDE,
				""));

		validateFieldName = getParameter(PARAM_WIN_VALIDATE_FIELD, null);
		if (validateFieldName != null) {
			validateFieldValue = getParameter(PARAM_WIN_VALIDATE_FIELD_VALUE,
					"");
			validateFieldId = getParameter(PARAM_WIN_VALIDATE_FIELD_ID, "");
		}

		form = new FormImpl(this);

		parseEscapedHash(getParameter(PARAM_ESCAPED_FRAGMENT, null));

		ContextUtils.setReqInfo(this);
	}

	private HashMap<String, String> getHashParams(String group,
			boolean createIfNotExist) {
		if (hashParams == null) {
			if (!createIfNotExist)
				return null;

			hashParams = new HashMap<String, HashMap<String, String>>();
		}

		if (!hashParams.containsKey(group)) {
			if (!createIfNotExist)
				return null;

			hashParams.put(group, new HashMap<String, String>());
		}

		return hashParams.get(group);
	}

	// 普通winlet参数的名称，例如：3[page]
	static final Pattern P_GROUP_PARAM = Pattern
			.compile("^(\\w)\\[([^\\]]+)\\]$");
	// 页面url，例如_p[1][u]
	static final Pattern P_PAGE_URL = Pattern
			.compile("^_p\\[(\\d+)\\]\\[u\\]$");
	// 页面参数，例如_p[1][p][programId]
	static final Pattern P_PAGE_PARAMETER = Pattern
			.compile("^_p\\[(\\d+)\\]\\[p\\]\\[([^\\]]+)\\]$");

	/**
	 * Example of hash value:
	 * 
	 * <pre>
	 * type=10&_p%5B0%5D%5Bu%5D=11&_p%5B0%5D%5Bp%5D%5BprogramId%5D=14402&addr=Toronto
	 * 3%5Bpage%5D=3&type=10&addr=L4C+9H5
	 * </pre>
	 * 
	 * @param hash
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private boolean parseEscapedHash(String hash) {
		if (StringUtils.isEmpty(hash))
			return false;

		hashEscaped = true;
		int maxPage = -1;

		try {
			for (String str : hash.split("&")) {
				int idx = str.indexOf("=");
				if (idx <= 0)
					continue;

				String name = URLDecoder.decode(str.substring(0, idx), "UTF-8");
				String value = URLDecoder.decode(str.substring(idx + 1),
						"UTF-8");

				if (name.indexOf("[") == -1) {
					getHashParams("root", true).put(name, value);
					continue;
				}

				Matcher m = P_GROUP_PARAM.matcher(name);
				if (m.find()) {
					getHashParams(m.group(1), true).put(m.group(2), value);
					continue;
				}

				m = P_PAGE_URL.matcher(name);
				if (m.find()) {
					int page = Integer.parseInt(m.group(1));

					if (page > maxPage)
						topPageParams = null;

					if (page >= maxPage) {
						maxPage = page;
						topPageUrl = value;
						continue;
					}
				}

				m = P_PAGE_PARAMETER.matcher(name);
				if (m.find()) {
					int page = Integer.parseInt(m.group(1));

					if (page > maxPage) {
						topPageUrl = null;
						topPageParams = null;
					}

					if (page >= maxPage) {
						if (topPageParams == null)
							topPageParams = new HashMap<String, String>();
						topPageParams.put(m.group(2), value);
						continue;
					}
				}

				// 不支持的参数，忽略不处理
				System.err.println("Unsupported escaped hash parameter name: "
						+ name + ", value: " + value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return true;
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
	public String getRemoteDomain() {
		return remoteDomain;
	}

	@Override
	public boolean isCrossDomain() {
		return StringUtils.notEmpty(remoteDomain);
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
	public boolean isFirstInclude() {
		return firstInclude;
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
			str = str.replaceAll("win\\$\\.include\\s*\\(", "win\\$._include("
					+ wid + ", ");
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
			str = str.replaceAll("win\\$\\.find\\s*\\(", "win\\$._find(" + wid
					+ ", ");
			str = str.replaceAll("win\\$\\.wait\\s*\\(", "win\\$._wait(" + wid
					+ ", ");
			str = str.replaceAll("win\\$\\.aftersubmit\\s*\\(",
					"win\\$._aftersubmit(" + wid + ", ");
		}

		return str;
	}

	@Override
	public String getWindowContent(Long wid, String windowUrl,
			Map<String, String> params, Map<String, Object> attributes,
			Consumer<StaticUrlProvider> returnProvider) throws Exception {
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
			WinletRequestWrapper wrapper = new WinletRequestWrapper(request,
					null, reqParams, attributes);
			// 避免被包含的功能改变当前LogInfo
			ContextUtils.setLogInfo(request, null);
			request.getServletContext().getRequestDispatcher(windowUrl)
					.forward(wrapper, response);

			if (returnProvider != null)
				returnProvider.accept((StaticUrlProvider) wrapper
						.getAttribute(StaticUrlProvider.REQ_ATTR_KEY));
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

	public boolean isHashEscaped() {
		return hashEscaped;
	}

	public HashMap<String, String> getHashParams(String group) {
		if (group == null || hashParams == null
				|| !hashParams.containsKey(group))
			return null;
		return hashParams.get(group);
	}

	public String getTopPageUrl() {
		return topPageUrl;
	}

	public HashMap<String, String> getTopPageParams() {
		return topPageParams;
	}

	@Override
	public String getContextPath() {
		return request.getContextPath();
	}

	@Override
	public boolean isWinInclude() {
		return request instanceof WinletRequestWrapper;
	}
}
