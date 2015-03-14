package com.aggrepoint.winlet;

import java.util.List;
import java.util.Map;

/**
 * 提供应用共用的对象列表和映射
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface ListProvider {
	Map<String, ?> getMap(String type);

	Map<String, String> getCodeValueMap(String type);

	List<?> getList(String type);

	List<CodeValue> getCodeValueList(String type);
}
