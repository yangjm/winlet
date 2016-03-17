package com.aggrepoint.winlet.jsp.site.taglib;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.site.SiteContext;
import com.aggrepoint.winlet.site.domain.Area;

import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * 栏位
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class AreaContentTag extends TagSupport {
	static final long serialVersionUID = 0;

	String m_strName;

	String[] attrs;

	public AreaContentTag() {
	}

	public void setName(String name) {
		m_strName = name;
	}

	/**
	 * @param attrs
	 *            如果不为空，则表示content的内容是Freemarker Template，attrs中是以空格分隔的request
	 *            attribute名称，这些attribute会被从request中取出作为Freemarker
	 *            Template的运行环境参数执行Freemarker Template
	 */
	public void setAttrs(String attrs) {
		if (attrs != null)
			this.attrs = attrs.split(", ");
	}

	public int doStartTag() throws JspException {
		try {
			JspWriter out = pageContext.getOut();

			HttpServletRequest request = (HttpServletRequest) pageContext
					.getRequest();
			SiteContext sc = (SiteContext) request
					.getAttribute(SiteContext.SITE_CONTEXT_KEY);
			ReqInfo ri = ContextUtils.getReqInfo();

			StringBuffer sbContent = new StringBuffer();

			boolean inPreloadWinletTag = TagSupport.findAncestorWithClass(this,
					PreloadWinletTag.class) != null;

			List<Area> areas = sc.getPage().getAreas(m_strName);
			if (areas != null)
				for (Area area : areas) {
					String content = area.getContent();

					if (attrs != null && attrs.length > 0) {
						// 作为Freemarker Template执行
						Template tmpl = new Template("", new StringReader(
								content), new Configuration());
						Map<String, Object> root = new HashMap<String, Object>();
						for (String attr : attrs)
							root.put(attr, request.getAttribute(attr));
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						tmpl.process(root, new OutputStreamWriter(baos));
						content = baos.toString();
					}

					try {
						if (inPreloadWinletTag) // 包含在PreloadWinletTag中，由PreloadWinletTag来处理预加载的情况
							sbContent.append(content);
						else
							sbContent.append(PreloadWinletTag.preloadWinlet(ri,
									content, false));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			out.print(sbContent.toString());
		} catch (Exception e) {
			e.printStackTrace();
			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
