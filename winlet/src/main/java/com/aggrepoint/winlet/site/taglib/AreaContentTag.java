package com.aggrepoint.winlet.site.taglib;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.LogInfoImpl;
import com.aggrepoint.winlet.ReqConst;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.site.SiteContext;
import com.aggrepoint.winlet.site.domain.Area;
import com.aggrepoint.winlet.spring.WinletRequestWrapper;
import com.aggrepoint.winlet.utils.BufferedResponse;

/**
 * 栏位
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class AreaContentTag extends TagSupport {
	static final long serialVersionUID = 0;

	String m_strName;

	public AreaContentTag() {
	}

	public void setName(String name) {
		m_strName = name;
	}

	// 在内容中寻找要预加载的winlet
	static Pattern PRELOAD_WINLET = Pattern
			.compile("<div\\s+data-winlet\\s*=\\s*\"(\\w+(/.+/\\w+)(\\?([^\\s]+))?(\\s+(.*?))?)\"\\s+data-preload\\s*>\\s*</div>");
	static final String PRELOAD_WINLET_ID_KEY = "PRELOAD_WINLET_ID";

	private String getPreloadWinletId(ServletRequest req) {
		Integer idx = (Integer) req.getAttribute(PRELOAD_WINLET_ID_KEY);
		if (idx == null) {
			idx = 0;
			req.setAttribute(PRELOAD_WINLET_ID_KEY, 1);
		} else
			req.setAttribute(PRELOAD_WINLET_ID_KEY, idx + 1);

		String wid = "P" + idx;
		return wid.length() + wid;
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			SiteContext sc = (SiteContext) request
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);
			ReqInfo ri = ContextUtils.getReqInfo();
			LogInfoImpl log = ContextUtils.getLogInfo(request);

			StringBuffer sb = new StringBuffer();

			List<Area> areas = sc.getPage().getAreas(m_strName);
			if (areas != null)
				for (Area area : areas) {
					String content = area.getContent();

					while (true) {
						Matcher m = PRELOAD_WINLET.matcher(content);
						if (!m.find())
							break;

						String wid = getPreloadWinletId(pageContext
								.getRequest());
						HashMap<String, String> reqParams = new HashMap<String, String>();
						reqParams.put(ReqConst.PARAM_WIN_ID, wid);
						reqParams.put(ReqConst.PARAM_PAGE_PATH, ri.getPageId());
						reqParams.put(ReqConst.PARAM_PAGE_URL, ri.getPageUrl());

						BufferedResponse response = new BufferedResponse();

						// 参考ReqInfoImpl.include()方法
						try {
							// 避免被包含的功能改变当前LogInfo
							ContextUtils.setLogInfo(request, null);
							request.getServletContext()
									.getRequestDispatcher("/" + m.group(2))
									.forward(
											new WinletRequestWrapper(request,
													null, reqParams), response);
						} finally {
							// 恢复当前请求的ReqInfo
							ContextUtils.setReqInfo(ri);
							// 恢复当前请求的LogInfo
							ContextUtils.setLogInfo(request, log);
						}
						byte[] bytes = response.getBuffered();

						String str = bytes == null ? "" : new String(bytes,
								"UTF-8");

						str = "<div data-winlet=\"" + m.group(1)
								+ "\" data-wid=\"" + wid
								+ "\"><div id=\"ap_win_" + wid + "\">" + str
								+ "</div></div>";
						content = m.replaceFirst(Matcher.quoteReplacement(str));
					}
					sb.append(content);
				}

			out.print(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
