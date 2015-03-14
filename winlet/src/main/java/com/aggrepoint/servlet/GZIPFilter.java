package com.aggrepoint.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.utils.ThreadContext;

public class GZIPFilter implements Filter {
	/**
	 * 用于在请求处理过程中指示GZIPFilter禁用压缩。 <br>
	 * 用法：在Servlet中调用<br>
	 * ThreadContext.setAttribute(GZIPFilter.THREAD_ATTR_DO_NOT_FILTER, "yes");
	 */
	public static final String THREAD_ATTR_DO_NOT_FILTER = "do not filter";

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		if (req instanceof HttpServletRequest) {
			// 清除线程处理上次请求时可能遗留的禁用压缩标记
			ThreadContext.removeAttribute(THREAD_ATTR_DO_NOT_FILTER);

			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			String ae = request.getHeader("accept-encoding");
			if (ae != null && ae.indexOf("gzip") != -1) {
				GZIPResponseWrapper wrappedResponse = new GZIPResponseWrapper(
						response);
				chain.doFilter(req, wrappedResponse);
				wrappedResponse.finishResponse();
				return;
			}
			chain.doFilter(req, res);
		}
	}

	public void init(FilterConfig filterConfig) {
	}

	public void destroy() {
	}
}
