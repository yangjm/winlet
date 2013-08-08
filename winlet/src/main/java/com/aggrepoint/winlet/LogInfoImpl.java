package com.aggrepoint.winlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.aggrepoint.winlet.spring.def.ReturnDef;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class LogInfoImpl implements LogInfo {
	private static String REQUEST_ID_KEY = LogInfoImpl.class.getName()
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

	protected LogInfoImpl(HttpServletRequest request, HttpServletResponse response) {
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

	public static LogInfoImpl getLogInfo(HttpServletRequest request,
			HttpServletResponse response) {
		LogInfoImpl li = ContextUtils.getLogInfo(request);
		if (li == null)
			li = new LogInfoImpl(request, response);
		if (response != null && li.response == null)
			li.response = response;
		return li;
	}

	public LogInfo setReqInfo(ReqInfo ri) {
		reqInfo = ri;
		return this;
	}

	public LogInfoImpl setHandler(Object handler) {
		this.handler = handler;
		return this;
	}

	public LogInfoImpl setModelAndView(ModelAndView mv) {
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

	public LogInfoImpl setException(Exception ex) {
		this.ex = ex;
		return this;
	}

	public LogInfo complete() {
		end = System.currentTimeMillis();
		return this;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getReqInfo()
	 */
	@Override
	public ReqInfo getReqInfo() {
		return reqInfo;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getRequestId()
	 */
	@Override
	public long getRequestId() {
		return requestId;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getStart()
	 */
	@Override
	public long getStart() {
		return start;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getEnd()
	 */
	@Override
	public long getEnd() {
		return end;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getRequest()
	 */
	@Override
	public HttpServletRequest getRequest() {
		return request;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getResponse()
	 */
	@Override
	public HttpServletResponse getResponse() {
		return response;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getHandler()
	 */
	@Override
	public Object getHandler() {
		return handler;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getModelAndView()
	 */
	@Override
	public ModelAndView getModelAndView() {
		return modelAndView;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getReturnDef()
	 */
	@Override
	public ReturnDef getReturnDef() {
		return returnDef;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getView()
	 */
	@Override
	public View getView() {
		return view;
	}

	/* (non-Javadoc)
	 * @see com.aggrepoint.winlet.LogInfo#getException()
	 */
	@Override
	public Exception getException() {
		return ex;
	}
}
