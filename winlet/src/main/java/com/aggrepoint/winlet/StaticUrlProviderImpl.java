package com.aggrepoint.winlet;

import java.util.HashMap;
import java.util.function.BiFunction;

import javax.servlet.http.HttpServletRequest;

public class StaticUrlProviderImpl implements StaticUrlProvider {
	HashMap<String, String> propertyMetas;
	HashMap<String, String> nameMetas;
	BiFunction<String, String, String> getUrl;

	public StaticUrlProviderImpl(HttpServletRequest req,
			BiFunction<String, String, String> getUrl) {
		req.setAttribute(REQ_ATTR_KEY, this);
		this.getUrl = getUrl;
	}

	public void addPropertyMeta(String property, String content) {
		if (propertyMetas == null)
			propertyMetas = new HashMap<String, String>();
		propertyMetas.put(property, content);
	}

	public void addNameMeta(String name, String content) {
		if (nameMetas == null)
			nameMetas = new HashMap<String, String>();
		nameMetas.put(name, content);
	}

	@Override
	public HashMap<String, String> getPropertyMetas() {
		return propertyMetas;
	}

	@Override
	public HashMap<String, String> getNameMetas() {
		return nameMetas;
	}

	@Override
	public String getUrl(String param, String value) {
		return getUrl.apply(param, value);
	}

}
