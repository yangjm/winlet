package com.aggrepoint.winlet.plugin;

import java.util.Enumeration;
import java.util.HashSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.view.JstlView;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.LogInfo;
import com.aggrepoint.winlet.ReqConst;
import com.aggrepoint.winlet.ReqInfo;
import com.aggrepoint.winlet.RequestLogger;
import com.aggrepoint.winlet.UserProfile;
import com.aggrepoint.winlet.spring.def.ReturnDef;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DefaultRequestLogger implements RequestLogger {
	static final HashSet<String> SYSTEM_PARAMS;
	static {
		SYSTEM_PARAMS = new HashSet<String>();
		for (String str : new String[] { ReqConst.PARAM_PAGE_PATH,
				ReqConst.PARAM_PAGE_URL, ReqConst.PARAM_WIN_ACTION,
				ReqConst.PARAM_WIN_ID, ReqConst.PARAM_WIN_PARAM,
				ReqConst.PARAM_WIN_RES, ReqConst.PARAM_WIN_VALIDATE_FIELD,
				ReqConst.PARAM_WIN_VALIDATE_FIELD_VALUE,
				ReqConst.PARAM_WIN_VIEW })
			SYSTEM_PARAMS.add(str);
	}

	@Override
	public void log(LogInfo log) {
		if (log.getHandler() instanceof HandlerMethod) {
			HandlerMethod hm = (HandlerMethod) log.getHandler();
			Logger logger = LoggerFactory.getLogger(hm.getBeanType());

			StringBuffer sb = new StringBuffer();

			// request id
			sb.append("| ");
			sb.append(log.getRequestId());

			// processed time
			sb.append(" | ");
			sb.append(log.getEnd() - log.getStart());

			// request ip
			sb.append(" | ");
			sb.append(log.getRequest().getRemoteAddr());

			// request url
			sb.append(" | ");
			sb.append(log.getRequest().getRequestURL());

			// reqinfo
			ReqInfo ri = log.getReqInfo();
			addUser(sb, ContextUtils.getUser(ri.getRequest()));

			if (ri != null) {
				sb.append(" | ");
				sb.append(ri.getPageId());
				sb.append(" | ");
				sb.append(ri.getViewId());
			}

			// handler object & method
			sb.append(" | ");
			sb.append(hm.getBean());
			sb.append(".");
			sb.append(hm.getMethod().getName());
			sb.append("()");

			// return view
			sb.append(" | ");
			if (log.getModelAndView() != null)
				sb.append(log.getModelAndView().getViewName());

			sb.append(" | ");
			org.springframework.web.servlet.View view = log.getView();
			if (view != null && view instanceof JstlView)
				sb.append(((JstlView) view).getUrl());

			// log message
			sb.append(" | ");
			if (log.getReturnDef() != null)
				sb.append(log.getReturnDef().getLog());

			addParams(sb, log.getRequest(), log.getReturnDef());

			addCookies(sb, log.getRequest());

			if (log.getException() != null)
				logger.error(sb.toString(), log.getException());
			else
				logger.info(sb.toString(), log.getException());
		}
	}

	protected void addUser(StringBuffer sb, UserProfile user) {
		sb.append(" | ");
		if (user.isAnonymous())
			sb.append("[ANONYMOUS]");
		else
			sb.append(user.getLoginId());
	}

	protected void addCookies(StringBuffer sb, HttpServletRequest req) {
		sb.append(" | ");
		Cookie[] ck = req.getCookies();
		if (ck != null)
			for (int i = 0; i < ck.length; i++) {
				sb.append(ck[i].getName());
				sb.append("=");
				sb.append(ck[i].getValue());
				sb.append(";");
			}
		else
			sb.append(" ");

	}

	protected void addParams(StringBuffer sb, HttpServletRequest req,
			ReturnDef def) {
		sb.append(" | ");

		HashSet<String> logExclude = null;
		if (def != null)
			logExclude = def.getLogExclude();

		boolean bFirst = true;
		for (@SuppressWarnings("unchecked")
		Enumeration<String> e = req.getParameterNames(); e.hasMoreElements();) {
			String name = e.nextElement();
			if (SYSTEM_PARAMS.contains(name))
				continue;
			if (logExclude != null && logExclude.contains(name))
				continue;

			if (bFirst)
				bFirst = false;
			else
				sb.append(", ");
			sb.append(name).append("=").append(req.getParameter(name));
		}
	}
}
