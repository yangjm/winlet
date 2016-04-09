package com.aggrepoint.winlet;

/**
 * 提供配置
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface ConfigProvider {
	/** 获取配置参数值 */
	String getStr(String name);

	String getStr(String name, String def);

	String getStr(Object context, String name, String def);

	int getInt(String name);

	int getInt(String name, int def);

	int getInt(Object context, String name, int def);

	long getLong(String name);

	long getLong(String name, long def);

	long getLong(Object context, String name, long def);

	float getFloat(String name);

	float getFloat(String name, float def);

	float getFloat(Object context, String name, float def);

	double getDouble(String name);

	double getDouble(String name, double def);

	double getDouble(Object context, String name, double def);

	boolean getBoolean(String name);

	boolean getBoolean(String name, boolean def);

	boolean getBoolean(Object context, String name, boolean def);

	boolean checkStr(String name, String value);
}
