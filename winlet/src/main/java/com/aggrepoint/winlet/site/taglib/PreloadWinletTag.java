package com.aggrepoint.winlet.site.taglib;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ReqConst;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.ReqInfoImpl;
import com.aggrepoint.winlet.WinletManager;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * 关于预加载窗口
 * 
 * <pre>
 * Winlet应用的窗口状态保存在hash中。预加载窗口是在页面显示时加载，页面显示时浏览器不会将hash值
 * 发送给服务器，因此预加载的窗口无法获得hash中保存的状态。另外，窗口的状态保存在hashgroup中，而
 * 页面初次显示时并没有hashgroup的概念，因此即使浏览器会将hash发送给服务器，也无法将hash中的参数
 * 与预加载窗口关联起来。
 * 
 * 预加载分为两种情况，强制预加载和判断预加载。判断预加载的窗口与客户端状态有关，如果PreloadWinletTag
 * 可以判断出客户端当前页面的hash中有状态，判断预加载的窗口不会被预加载。如果PreloadWinletTag判断
 * 不出，判断预加载窗口会被PreloadWinletTag加载并返回。客户端winlet_local.js如果发现hash中其实
 * 有适用于已经加载好的判断预加载窗口的数据，winlet_local.js会重新请求页面，替换PreloadWinletTag
 * 预加载的内容。当用户按回退按钮改变了浏览器中的hash状态时，winlet_local.js也会重新请求判断预加载
 * 窗口的内容。强制预加载的窗口客户端状态无关，PreloadWinletTag一定会加载这种类型的窗口，客户端
 * winlet_local.js在页面初始化时，和浏览器回退导致hash值变更时，都不会再次向服务器请求这种窗口的
 * 内容。强制预加载窗口内可以进行post操作，改变窗口本身的状态。
 * 
 * 强制预加载的窗口返回的页面中可以包含<div data-winlet=''>标签，winlet_local.js会在页面初始
 * 化和hash变更时加载这些标签指定的窗口。返回这种标签的强制预加载窗口本身不能进行get或者post操作，因为
 * 操作返回的页面中如果再包含这种标签，winlet_local.js不会加以处理。判断预加载的窗口返回的页面中不
 * 能包含这种标签，因为winlet_local.js从服务器端获取了winlet的页面内容后，不会处理页面内容中存在
 * 的<div data-winlet>标签。
 * 
 * 强制预加载窗口用data-preload-forced说明，判断预加载窗口用data-preload说明。
 * 
 * PreloadWinletTag目前无法判断客户端是否有状态。准备对winlet_local.js进行改进，第一次在URL中加入
 * hash值时，同时将URL修改带上一个get参数，用于表示URL中存在hash数据。当用户刷新页面时，虽然hash数据
 * 无法被传递到服务器端，但get参数会。PreloadWinletTag可以根据这个get参数判断是否要加载判断预加载窗口。
 * 改变URL但不引发服务器端请求的方法只在IE10+支持，对于IE10以下的浏览器PreloadWinletTag无法获得
 * 客户端是否有状态的指示。
 * 
 * 对于winlet_local.js，强制预加载的窗口被包含在<div>标签中，标签没有data-winlet属性，但是有
 * data-winlet-url属性。判断预加载窗口既有data-winlet，也有data-winlet-url。普通顶级窗口
 * 只有data-winlet属性，没有data-winlet-url。服务器端include的子窗口与强制加载窗口类似，
 * 没有data-winlet属性，但是有data-winlet-url属性。按回退按钮导致hash变更时，强制预加载窗口和
 * 被include生成的子窗口都无需被加载，因此他们都没有data-winlet属性。参考Redmine中关于Winlet
 * 容器的说明。
 * 
 * 20150908
 * 
 * 当遇到Google和Facebook等爬虫通过_escaped_fragment_参数请求页面时，PreloadWinletTag会预加载
 * 所有窗口，不管窗口是否定义为预加载。
 * 
 * </pre>
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PreloadWinletTag extends BodyTagSupport {
	static final long serialVersionUID = 0;

	// 在内容中寻找要预加载的winlet
	static Pattern WINLET_PRELOAD = Pattern
			.compile("<div\\s+data-winlet\\s*=\\s*\"\\s*(/?\\w+(/[^\"]+/\\w+))(\\?([^\\s\"]+))?(\\s+([^\"]*?))?\"\\s+(data-preload(-forced)?)\\s*>\\s*</div>");
	static Pattern WINLET_NO_PRELOAD = Pattern
			.compile("<div\\s+data-winlet\\s*=\\s*\"([^\">]+)\"\\s+data-preload\\s*>(\\s*)</div>");
	static Pattern WINLET_ALL = Pattern
			.compile("<div\\s+data-winlet\\s*=\\s*\"\\s*(/?\\w+(/[^\"]+/\\w+))(\\?([^\\s\"]+))?(\\s+([^\"]*?))?\"[^>]*>\\s*</div>");

	static Pattern META_BY_NAME = Pattern
			.compile("<meta\\s+name\\s*=\\s*\"([^\"]+)\"\\s+content\\s*=\\s*\"([^\"]+)\"[^>]*>");
	static Pattern META_BY_PROPERTY = Pattern
			.compile("<meta\\s+property\\s*=\\s*\"([^\"]+)\"\\s+content\\s*=\\s*\"([^\"]+)\"[^>]*>");

	static Pattern HASH_GROUP_URL = Pattern.compile("^/?\\w+/\\w+/");
	static Pattern BODY = Pattern.compile("(<body[^>]*>).*</body>",
			Pattern.DOTALL);

	public static String preloadWinlet(ReqInfo reqInfo, String content,
			boolean wholeBody) throws Exception {
		ReqInfoImpl ri = (ReqInfoImpl) reqInfo;

		Pattern pattern = null;

		if (ri.isHashEscaped()) { // 预加载所有窗口
			pattern = WINLET_ALL;
		} else {
			pattern = WINLET_PRELOAD;

			if (ri.noPreload())
				// 不加载“判断预加载”窗口
				content = WINLET_NO_PRELOAD.matcher(content).replaceAll(
						"<div data-winlet=\"$1\">$2</div>");
		}

		HashMap<String, String> metaByName = new HashMap<String, String>();
		HashMap<String, String> metaByProperty = new HashMap<String, String>();

		if (wholeBody && ri.isHashEscaped() && ri.getTopPageUrl() != null) {
			// 只加载top page
			// 页面中必须有<body>，top page内容才会执行和插入到返回的页面中
			Matcher m = BODY.matcher(content);
			if (m.find()) {
				HashMap<String, String> reqParams = new HashMap<String, String>();
				reqParams.put(ReqConst.PARAM_PAGE_PATH, ri.getPageId());
				reqParams.put(ReqConst.PARAM_PAGE_URL, ri.getPageUrl());
				if (ri.getTopPageParams() != null)
					reqParams.putAll(ri.getTopPageParams());
				String str = ri.getWindowContent(WinletManager.getSeqId(),
						ri.getTopPageUrl(), reqParams, null);

				// { 提取meta
				while (true) {
					Matcher mm = META_BY_NAME.matcher(str);
					if (!mm.find())
						break;

					metaByName.put(mm.group(1).trim(), mm.group(2));
					str = mm.replaceFirst("");
				}

				while (true) {
					Matcher mm = META_BY_PROPERTY.matcher(str);
					if (!mm.find())
						break;

					metaByProperty.put(mm.group(1).trim(), mm.group(2));
					str = mm.replaceFirst("");
				}
				// }

				str = m.group(1) + str + "</body>";
				content = m.replaceFirst(Matcher.quoteReplacement(str));
			}
		} else {
			HashMap<String, String> hashGroups = new HashMap<String, String>();
			Function<String, String> getHashGroupKey = p -> {
				Matcher m = HASH_GROUP_URL.matcher(p);
				if (m.find())
					p = m.group();
				return p;
			};
			Function<String, String> getHashGroup = p -> {
				p = getHashGroupKey.apply(p);
				if (!hashGroups.containsKey(p))
					hashGroups.put(p, Integer.toString(hashGroups.size() + 1));
				return hashGroups.get(p);
			};

			while (true) {
				Matcher m = pattern.matcher(content);
				if (!m.find())
					break;

				boolean forced = false;

				if (ri.isHashEscaped())
					forced = true;
				else
					forced = m.group(7).equals("data-preload-forced");

				String params = null;

				HashMap<String, String> reqParams = new HashMap<String, String>();
				if (m.group(4) != null) {
					StringTokenizer st = new StringTokenizer(m.group(4), "&");
					while (st.hasMoreElements()) {
						String s = st.nextToken();
						int idx = s.indexOf("=");
						if (idx >= 0)
							reqParams.put(s.substring(0, idx),
									s.substring(idx + 1));
					}
				}
				if (reqParams.size() > 0)
					params = new ObjectMapper().writeValueAsString(reqParams);

				reqParams.put(ReqConst.PARAM_PAGE_PATH, ri.getPageId());
				reqParams.put(ReqConst.PARAM_PAGE_URL, ri.getPageUrl());

				long wid = WinletManager.getSeqId();

				String settings = m.group(5);
				if (settings == null)
					settings = "";

				if (ri.isHashEscaped()) { // 把hash中的参数带上
					if (settings.indexOf("root:yes") > 0
							&& !hashGroups.containsKey("root")) {
						hashGroups
								.put(getHashGroupKey.apply(ri.getRequest()
										.getContextPath() + m.group(2)), "root");
					}

					HashMap<String, String> hashParams = ri
							.getHashParams(getHashGroup.apply(ri.getRequest()
									.getContextPath() + m.group(2)));
					if (hashParams != null)
						reqParams.putAll(hashParams);
				}

				String str = ri.getWindowContent(wid, m.group(2), reqParams,
						null);

				// { 提取meta
				while (true) {
					Matcher mm = META_BY_NAME.matcher(str);
					if (!mm.find())
						break;

					if (!metaByName.containsKey(mm.group(1).trim()))
						metaByName.put(mm.group(1).trim(), mm.group(2));
					str = mm.replaceFirst("");
				}

				while (true) {
					Matcher mm = META_BY_PROPERTY.matcher(str);
					if (!mm.find())
						break;

					if (!metaByProperty.containsKey(mm.group(1).trim()))
						metaByProperty.put(mm.group(1).trim(), mm.group(2));
					str = mm.replaceFirst("");
				}
				// }

				StringBuffer sb = new StringBuffer();

				sb.append("<div data-winlet-id=\"").append(wid).append("\"");

				if (!forced)
					sb.append(" data-winlet=\"")
							.append(ri.getRequest().getContextPath())
							.append(m.group(2)).append(settings).append("\"");

				sb.append(" data-winlet-url=\"")
						.append(ri.getRequest().getContextPath())
						.append(m.group(2)).append("\"");

				if (params != null)
					sb.append(" data-winlet-params=\"")
							.append(params.replaceAll("\"", "&quot;"))
							.append("\"");

				// TODO: settings处理

				sb.append(">").append(str).append("</div>");
				content = m
						.replaceFirst(Matcher.quoteReplacement(sb.toString()));
			}
		}

		if (wholeBody) {
			// 页面中必须有<title>...</title>标签，设置的title才会生效
			if (metaByName.get("title") != null) {
				content = content.replaceAll("<title>[^<]*</title>", "<title>"
						+ metaByName.get("title") + "</title>");
				metaByName.remove("title");
			}

			// 页面中必须存在</head>标签，设置的meta data才会被处理
			if ((metaByName.size() > 0 || metaByProperty.size() > 0)
					&& content.indexOf("</head>") > 0) {
				StringBuffer sb = new StringBuffer();
				for (String key : metaByName.keySet()) {
					sb.append("<meta name=\"").append(key)
							.append("\" content=\"")
							.append(metaByName.get(key)).append("\"/>");
					String replace = "<meta\\s+name\\s*=\\s*\"\\s*"
							+ Matcher.quoteReplacement(key)
							+ "\\s*\"\\s+content\\s*=\\s*\"[^\"]*\"[^>]*>";
					System.out.println(replace);
					content = content.replaceAll(replace, "");
				}
				for (String key : metaByProperty.keySet()) {
					sb.append("<meta property=\"").append(key)
							.append("\" content=\"")
							.append(metaByProperty.get(key)).append("\"/>");
					content = content
							.replaceAll(
									"<meta\\s+property\\s*=\\s*\"\\s*"
											+ Matcher.quoteReplacement(key)
											+ "\\s*\"\\s+content\\s*=\\s*\"[^\"]*\"[^>]*>",
									"");
				}

				int idx = content.indexOf("</head>");
				content = content.substring(0, idx) + sb.toString()
						+ content.substring(idx);
			}
		}

		return content;
	}

	public int doStartTag() throws JspException {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() throws JspTagException {
		BodyContent body = getBodyContent();
		try {
			getPreviousOut().write(
					preloadWinlet(ContextUtils.getReqInfo(), body.getString(),
							true));
		} catch (Exception e) {
			throw new JspTagException(e.getMessage());
		}

		body.clearBody();

		return SKIP_BODY;
	}
}
