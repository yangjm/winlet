package com.aggrepoint.winlet.site.taglib;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.LogInfoImpl;
import com.aggrepoint.winlet.ReqInfo;

/**
 * 调用Handler，将Handler放在Model中的数据放入Request
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class RunHandlerTag extends TagSupport {
	static final long serialVersionUID = 0;

	String url;

	public void setUrl(String url) {
		this.url = url;
	}

	public int doStartTag() throws JspException {
		try {
			HttpServletRequest req = (HttpServletRequest) pageContext
					.getRequest();
			LogInfoImpl log = ContextUtils.getLogInfo(req);
			ReqInfo reqInfo = ContextUtils.getReqInfo();

			try {
				Map<String, Object> model = ContextUtils
						.getDispatcher(req)
						.runHandler(
								req,
								(HttpServletResponse) pageContext.getResponse(),
								reqInfo.getPageUrl(), url);

				for (String key : model.keySet())
					req.setAttribute(key, model.get(key));
			} finally {
				// 恢复当前请求的ReqInfo
				ContextUtils.setReqInfo(reqInfo);
				// 恢复当前请求的LogInfo
				ContextUtils.setLogInfo(req, log);
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();

			throw new JspException(e.getMessage());
		}
		return SKIP_BODY;
	}
}
