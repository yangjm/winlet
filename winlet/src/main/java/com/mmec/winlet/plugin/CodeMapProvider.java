package com.mmec.winlet.plugin;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import com.aggrepoint.winlet.CodeMapWrapper;
import com.aggrepoint.winlet.plugin.DefaultCodeMapProvider;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class CodeMapProvider extends DefaultCodeMapProvider {
	static Hashtable<String, CodeMapWrapper> tables = new Hashtable<String, CodeMapWrapper>();
	static {
		HashMap<String, String> test = new HashMap<String, String>();
		test.put("0", "男");
		test.put("1", "女");
		tables.put("gender", new CodeMapWrapper(test));
	}

	@Override
	public Map<String, String> getMap(String type) {
		CodeMapWrapper table = tables.get(type);
		return table == null ? super.getMap(type) : table;
	}
}
