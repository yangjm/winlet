package com.aggrepoint.dao;

import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;

public class DefaultNamingStrategy5 implements PhysicalNamingStrategy {
	protected static String addUnderscores(String name) {
		StringBuilder buf = new StringBuilder(name.replace('.', '_'));
		for (int i = 1; i < buf.length(); i++) {
			if ((Character.isLowerCase(buf.charAt(i - 1)) || Character.isDigit(buf.charAt(i - 1)))
					&& Character.isUpperCase(buf.charAt(i))
					&& (i < buf.length() - 1 && Character.isLowerCase(buf.charAt(i + 1)) || i == buf.length() - 1)) {
				buf.insert(i++, '_');
			}
		}

		return buf.toString().toLowerCase();
	}

	@Override
	public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalTableName(Identifier n, JdbcEnvironment jdbcEnvironment) {
		if (n.getText().toUpperCase().equals(n.getText())) // Assume @Table() use all upper case table name
			return n;

		String name = addUnderscores(n.getText()).toUpperCase();

		if (name.equals("NEWS") || name.endsWith("_NEWS"))
			return Identifier.toIdentifier(name);

		if (name.endsWith("Y"))
			if (!name.endsWith("AY") && !name.endsWith("EY") && !name.endsWith("OY") && !name.endsWith("IY")
					&& !name.endsWith("UY"))
				return Identifier.toIdentifier(name.substring(0, name.length() - 1) + "IES");

		if (name.endsWith("S") || name.endsWith("SH") || name.endsWith("CH") || name.endsWith("X"))
			return Identifier.toIdentifier(name + "ES");

		return Identifier.toIdentifier(name + "S");
	}

	@Override
	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return name;
	}

	@Override
	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
		return Identifier.toIdentifier(addUnderscores(name.getText()).toUpperCase());
	}
}
