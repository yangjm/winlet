package com.aggrepoint.winlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.aggrepoint.winlet.spring.def.ReturnDef;

public class LogInfo {
	private static String REQUEST_ID_KEY = LogInfo.class.getName()
			+ ".REQUEST_ID_KEY";
	private static long REQUEST_ID = 0l;

	private long requestId;
	private long start;
	private long end;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private ReqInfo reqInfo;
	private Object handler;
	private ModelAndView modelAndView;
	private ReturnDef returnDef;
	private View view;
	private Exception ex;

	protected LogInfo(HttpServletRequest request, HttpServletResponse response) {
		start = System.currentTimeMillis();

		this.request = request;
		this.response = response;

		Long rid = (Long) request.getAttribute(REQUEST_ID_KEY);
		if (rid == null) {
			rid = REQUEST_ID++;
			request.setAttribute(REQUEST_ID_KEY, rid);
		}
		requestId = rid;

		ContextUtils.setLogInfo(request, this);
	}

	public static LogInfo getLogInfo(HttpServletRequest request,
			HttpServletResponse response) {
		LogInfo li = ContextUtils.getLogInfo(request);
		if (li == null)
			li = new LogInfo(request, response);
		if (response != null && li.response == null)
			li.response = response;
		return li;
	}

	public LogInfo setReqInfo(ReqInfo ri) {
		reqInfo = ri;
		return this;
	}

	public LogInfo setHandler(Object handler) {
		this.handler = handler;
		return this;
	}

	public LogInfo setModelAndView(ModelAndView mv) {
		modelAndView = mv;
		return this;
	}

	public LogInfo setReturnDef(ReturnDef rd) {
		returnDef = rd;
		return this;
	}

	public LogInfo setView(View view) {
		this.view = view;
		return this;
	}

	public LogInfo setException(Exception ex) {
		this.ex = ex;
		return this;
	}

	public LogInfo complete() {
		end = System.currentTimeMillis();
		return this;
	}

	public ReqInfo getReqInfo() {
		return reqInfo;
	}

	public long getRequestId() {
		return requestId;
	}

	public long getStart() {
		return start;
	}

	public long getEnd() {
		return end;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Object getHandler() {
		return handler;
	}

	public ModelAndView getModelAndView() {
		return modelAndView;
	}

	public ReturnDef getReturnDef() {
		return returnDef;
	}

	public View getView() {
		return view;
	}

	public Exception getException() {
		return ex;
	}
}
