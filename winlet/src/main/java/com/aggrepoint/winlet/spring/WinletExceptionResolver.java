package com.aggrepoint.winlet.spring;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.aggrepoint.winlet.ContextUtils;
import com.aggrepoint.winlet.PsnRuleEngine;
import com.aggrepoint.winlet.RespHeaderConst;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletExceptionResolver implements HandlerExceptionResolver {
	static final Log logger = LogFactory.getLog(WinletExceptionResolver.class);

	List<ExceptionMapping> exceptionMap;

	@Override
	public ModelAndView resolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex) {
		PsnRuleEngine psnEngine = ContextUtils.getPsnRuleEngine(request);

		for (ExceptionMapping map : exceptionMap)
			if (map.getClz().isAssignableFrom(ex.getClass())) {
				if (map.getRule() != null) {
					try {
						if (!psnEngine.eval(map.getRule()))
							continue;
					} catch (Exception e) {
						logger.error(
								"Error eveluating psn rule \"" + map.getRule()
										+ "\" defined in ExceptionMapping", e);
						continue;
					}
				}

				if (map.isRedirect()) {
					response.setHeader(RespHeaderConst.HEADER_REDIRECT, map.getView());
					ModelAndView mv = new ModelAndView();
					mv.clear();
					return mv;
				}

				return new ModelAndView(map.getView());
			}

		return null;
	}

	public void setExceptionMappings(List<ExceptionMapping> map) {
		this.exceptionMap = map;
	}
}
