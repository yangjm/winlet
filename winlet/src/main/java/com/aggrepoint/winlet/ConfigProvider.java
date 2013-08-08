package com.aggrepoint.winlet;

/**
 * 提供配置
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface ConfigProvider {
	/** 获取配置参数值 */
	String getConfig(String name);
}
