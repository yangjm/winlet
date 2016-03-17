package com.aggrepoint.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

public class CORSFilter extends OncePerRequestFilter {
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		if (request.getHeader("Origin") == null) {
			filterChain.doFilter(request, response);
			return;
		}

		response.addHeader("Access-Control-Allow-Origin",
				request.getHeader("Origin"));

		if (request.getHeader("Access-Control-Request-Method") != null
				&& "OPTIONS".equals(request.getMethod())) {
			// CORS "pre-flight" request
			response.addHeader("Access-Control-Allow-Methods",
					"GET, POST, PUT, DELETE");
			// response.addHeader("Access-Control-Allow-Headers",
			// "Authorization");
			response.addHeader("Access-Control-Allow-Headers", "Content-Type");
			response.addHeader("Access-Control-Max-Age", "1");
		}

		// CORS请求不能传递自定义的HEADER，转换为<div id="winlet-header"></div>放在返回的内容中
		CORSResponseWrapper wrapper = new CORSResponseWrapper(response);
		filterChain.doFilter(request, wrapper);
		wrapper.close();
	}
}
