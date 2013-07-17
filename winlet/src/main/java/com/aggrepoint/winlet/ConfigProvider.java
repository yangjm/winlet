package com.aggrepoint.winlet;

/**
 * 提供配置
 * 
 * @author Jim
 */
public interface ConfigProvider {
	/** 获取配置参数值 */
	String getConfig(String name);
}
