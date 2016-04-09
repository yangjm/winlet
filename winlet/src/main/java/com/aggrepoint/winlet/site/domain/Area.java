package com.aggrepoint.winlet.site.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Area extends Base {
	private boolean cascade;
	private String content;
	/** 页面里引用的Winlet的URL */
	private List<String> winlets = new ArrayList<String>();

	public boolean isCascade() {
		return cascade;
	}

	public void setCascade(boolean cascade) {
		this.cascade = cascade;
	}

	public String getContent() {
		return content;
	}

	Pattern P_WINLET = Pattern
			.compile("<div\\s+data-winlet\\s*=\\s*\"([^\\?\"\\s]+)([^\"]*)\"");

	public void setContent(String content, String contextRoot) {
		this.content = content;

		Matcher m = P_WINLET.matcher(content);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			String url = m.group(1);

			if (!url.startsWith("/"))
				url = "/" + url;

			if (!url.startsWith(contextRoot)) // winlet路径不是以context
												// root开始，加上context root
				url = contextRoot + url;

			winlets.add(url.substring(contextRoot.length() + 1));
			m.appendReplacement(sb,
					("<div data-winlet=\"" + url + m.group(2) + "\"")
							.replaceAll("\\$", "\\\\\\$"));
		}
		m.appendTail(sb);
		this.content = sb.toString();
	}

	public List<String> getWinletUrls() {
		return winlets;
	}
}
