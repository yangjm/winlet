package com.aggrepoint.winlet.plugin;

import com.aggrepoint.winlet.ConfigProvider;

/**
 * 缺省的配置提供
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class DefaultConfigProvider implements ConfigProvider {
	String booleanTrue;

	public DefaultConfigProvider() {
		booleanTrue = "true";
	}

	public DefaultConfigProvider(String booleanTrue) {
		this.booleanTrue = booleanTrue;
	}

	@Override
	public String getStr(Object context, String name, String def) {
		return def;
	}

	@Override
	public String getStr(String name) {
		return getStr(null, name, null);
	}

	@Override
	public String getStr(String name, String def) {
		String str = getStr(name);
		return str == null ? def : str;
	}

	@Override
	public int getInt(String name) {
		try {
			return Integer.parseInt(getStr(name));
		} catch (Exception e) {
		}

		return 0;
	}

	@Override
	public int getInt(String name, int def) {
		try {
			return Integer.parseInt(getStr(name));
		} catch (Exception e) {
		}

		return def;
	}

	@Override
	public int getInt(Object context, String name, int def) {
		try {
			return Integer
					.parseInt(getStr(context, name, Integer.toString(def)));
		} catch (Exception e) {
		}

		return def;
	}

	@Override
	public long getLong(String name) {
		try {
			return Long.parseLong(getStr(name));
		} catch (Exception e) {
		}

		return 0;
	}

	@Override
	public long getLong(String name, long def) {
		try {
			return Long.parseLong(getStr(name));
		} catch (Exception e) {
		}

		return def;
	}

	@Override
	public long getLong(Object context, String name, long def) {
		try {
			return Long.parseLong(getStr(context, name, Long.toString(def)));
		} catch (Exception e) {
		}

		return def;
	}

	@Override
	public float getFloat(String name) {
		try {
			return Float.parseFloat(getStr(name));
		} catch (Exception e) {
		}

		return 0;
	}

	@Override
	public float getFloat(String name, float def) {
		try {
			return Float.parseFloat(getStr(name));
		} catch (Exception e) {
		}

		return def;
	}

	@Override
	public float getFloat(Object context, String name, float def) {
		try {
			return Float.parseFloat(getStr(context, name, Float.toString(def)));
		} catch (Exception e) {
		}

		return def;
	}

	@Override
	public double getDouble(String name) {
		try {
			return Double.parseDouble(getStr(name));
		} catch (Exception e) {
		}

		return 0;
	}

	@Override
	public double getDouble(String name, double def) {
		try {
			return Double.parseDouble(getStr(name));
		} catch (Exception e) {
		}

		return def;
	}

	@Override
	public double getDouble(Object context, String name, double def) {
		try {
			return Double.parseDouble(getStr(context, name,
					Double.toString(def)));
		} catch (Exception e) {
		}

		return def;
	}

	@Override
	public boolean getBoolean(String name) {
		String str = getStr(name);
		return str == null ? false : booleanTrue.equalsIgnoreCase(str);
	}

	@Override
	public boolean getBoolean(String name, boolean def) {
		String str = getStr(name);
		return str == null ? def : booleanTrue.equalsIgnoreCase(str);
	}

	@Override
	public boolean getBoolean(Object context, String name, boolean def) {
		return booleanTrue.equalsIgnoreCase(getStr(context, name,
				def ? booleanTrue : ""));
	}

	@Override
	public boolean checkStr(String name, String value) {
		String v = getStr(name);
		if (v == null && value == null)
			return true;
		if (v == null || value == null)
			return false;
		return v.equals(value);
	}
}
