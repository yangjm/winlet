package com.aggrepoint.winlet.plugin;

import java.util.Hashtable;
import java.util.Map;

import com.aggrepoint.winlet.CodeMapWrapper;
import com.aggrepoint.winlet.CodeMapProvider;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DefaultCodeMapProvider implements CodeMapProvider {
	static CodeMapWrapper table = new CodeMapWrapper(
			new Hashtable<String, String>());

	@Override
	public Map<String, String> getMap(String type) {
		return table;
	}
}
