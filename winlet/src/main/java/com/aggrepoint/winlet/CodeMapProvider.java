package com.aggrepoint.winlet;

import java.util.Map;

/**
 * 提供基础数据代码键值和代码说明的映射表
 * 
 * @author Jim
 */
public interface CodeMapProvider {
	Map<String, String> getMap(String type);
}
