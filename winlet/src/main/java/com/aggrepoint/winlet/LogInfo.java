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
public interface LogInfo {

	public abstract ReqInfo getReqInfo();

	public abstract long getRequestId();

	public abstract long getStart();

	public abstract long getEnd();

	public abstract HttpServletRequest getRequest();

	public abstract HttpServletResponse getResponse();

	public abstract Object getHandler();

	public abstract ModelAndView getModelAndView();

	public abstract ReturnDef getReturnDef();

	public abstract View getView();

	public abstract Exception getException();

}