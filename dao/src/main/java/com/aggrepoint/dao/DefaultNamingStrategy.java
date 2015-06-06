package com.aggrepoint.dao;

import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.internal.util.StringHelper;

public class DefaultNamingStrategy extends ImprovedNamingStrategy {
	private static final long serialVersionUID = 1L;

	protected static String addUnderscores(String name) {
		StringBuilder buf = new StringBuilder(name.replace('.', '_'));
		for (int i = 1; i < buf.length(); i++) {
			if ((Character.isLowerCase(buf.charAt(i - 1)) || Character
					.isDigit(buf.charAt(i - 1)))
					&& Character.isUpperCase(buf.charAt(i))
					&& (i < buf.length() - 1
							&& Character.isLowerCase(buf.charAt(i + 1)) || i == buf
							.length() - 1)) {
				buf.insert(i++, '_');
			}
		}

		return buf.toString().toLowerCase();
	}

	@Override
	public String classToTableName(String className) {
		String name = addUnderscores(StringHelper.unqualify(className))
				.toUpperCase();

		if (name.endsWith("Y"))
			if (!name.endsWith("AY") && !name.endsWith("EY")
					&& !name.endsWith("OY") && !name.endsWith("IY")
					&& !name.endsWith("UY"))
				return name.substring(0, name.length() - 1) + "IES";

		if (name.endsWith("S") || name.endsWith("SH") || name.endsWith("CH")
				|| name.endsWith("X"))
			return name + "ES";

		return name + "S";
	}

	@Override
	public String tableName(String tableName) {
		return tableName;
	}

	@Override
	public String propertyToColumnName(String propertyName) {
		return addUnderscores(StringHelper.unqualify(propertyName))
				.toUpperCase();
	}
}
