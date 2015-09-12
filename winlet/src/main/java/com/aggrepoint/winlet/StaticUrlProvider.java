package com.aggrepoint.winlet;

import java.util.HashMap;

/**
 * 支持静态URL的Winlet在Window逻辑中需生成一个实现了StaticUrlProvider的对象，并把这个对象放在request
 * attribute中
 * 
 * @author jiangmingyang
 *
 */
public interface StaticUrlProvider {
	static final String REQ_ATTR_KEY = "WINLET_STATIC_URL_PROVIDER";

	public String getUrl(String param, String value);

	public HashMap<String, String> getPropertyMetas();

	public HashMap<String, String> getNameMetas();
}
