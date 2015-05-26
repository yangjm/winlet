package com.aggrepoint.winlet.spring;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * <pre>
 * 主要功能：
 * 在请求中添加额外的请求头和参数
 * 防止request attribute被设置到被包含的request中，但是可以访问到被包含的request中的attribute
 * </pre>
 * 
 * @author Jim
 *
 */
public class WinletRequestWrapper extends HttpServletRequestWrapper {
	private Map<String, String> headers;
	private Map<String, String> params;
	private HashMap<String, Object> attributes = new HashMap<String, Object>();
	private String servletPath;

	public WinletRequestWrapper(HttpServletRequest request,
			Map<String, String> headers, Map<String, String> params,
			Map<String, Object> attributes) {
		super(request);
		this.headers = headers;
		this.params = params;
		if (attributes != null)
			this.attributes.putAll(attributes);
	}

	void setParams(Hashtable<String, String> params) {
		this.params = params;
	}

	void setHeaders(Hashtable<String, String> headers) {
		this.headers = headers;
	}

	public Map<String, Object> getSetAttributes() {
		return attributes;
	}

	public void setServletPath(String path) {
		if (path == null)
			return;

		servletPath = path.startsWith("/") ? path : "/" + path;
	}

	@Override
	public String getRequestURI() {
		if (servletPath == null)
			return super.getRequestURI();

		return super.getContextPath() + servletPath;
	}

	@Override
	public String getServletPath() {
		return servletPath == null ? super.getServletPath() : servletPath;
	}

	@Override
	public String getHeader(String name) {
		if (headers != null && headers.containsKey(name))
			return headers.get(name);

		return super.getHeader(name);
	}

	@Override
	public String getParameter(String name) {
		if (params != null && params.containsKey(name))
			return params.get(name);

		return super.getParameter(name);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		if (params == null || params.size() == 0)
			return super.getParameterNames();

		Vector<String> set = new Vector<String>();

		set.addAll(params.keySet());
		for (Enumeration<String> e = super.getParameterNames(); e
				.hasMoreElements();)
			set.add((String) e.nextElement());

		return set.elements();
	}

	@Override
	public String[] getParameterValues(String name) {
		if (params != null && params.containsKey(name))
			return new String[] { params.get(name) };

		return super.getParameterValues(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		if (params == null)
			return super.getParameterMap();

		HashMap<String, String[]> map = new HashMap<String, String[]>();
		map.putAll(super.getParameterMap());

		for (String key : params.keySet())
			map.put(key, new String[] { params.get(key) });
		return map;
	}

	@Override
	public void setAttribute(String name, Object o) {
		attributes.put(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		attributes.remove(name);
	}

	@Override
	public Object getAttribute(String name) {
		Object attr = attributes.get(name);
		return attr == null ? super.getAttribute(name) : attr;
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		HashSet<String> names = new HashSet<String>();

		for (Enumeration<String> e = super.getAttributeNames(); e
				.hasMoreElements();)
			names.add(e.nextElement());

		names.addAll(attributes.keySet());

		final Iterator<String> it = names.iterator();
		return new Enumeration<String>() {
			@Override
			public boolean hasMoreElements() {
				return it.hasNext();
			}

			@Override
			public String nextElement() {
				return it.next();
			}
		};
	}

}
