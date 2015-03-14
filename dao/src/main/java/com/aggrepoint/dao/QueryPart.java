package com.aggrepoint.dao;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aggrepoint.dao.annotation.Like;
import com.aggrepoint.dao.annotation.Replace;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public class QueryPart {
	static Pattern P_PARAM = Pattern
			.compile(":([\\w\\d]+(\\___[\\w\\d\\_]+)?(\\[.*\\])?)");
	static Pattern P_REPLACE = Pattern.compile("^[\\w\\d\\s\\.,]*$");

	boolean optional;
	String part;
	String[] paramDepends;
	Hashtable<String, String> replace = new Hashtable<String, String>();
	Hashtable<String, String> depends = new Hashtable<String, String>();

	public QueryPart(Method method, boolean optional, String part,
			HashSet<String> params, Hashtable<String, Like> likes,
			Hashtable<String, Replace> replaces,
			Hashtable<String, Function> funcs) {
		this.optional = optional;
		this.part = part;

		HashSet<String> vecParam = new HashSet<String>();

		Matcher m = P_PARAM.matcher(part);
		while (m.find()) {
			String p = m.group(1);
			if (funcs.containsKey(p)) {
				depends.put(m.group(0), p);
				continue;
			} else if (params.contains(p) || likes.containsKey(p)) {
				vecParam.add(p);
				continue;
			} else if (replaces.containsKey(p)) {
				replace.put(m.group(0), p);
				continue;
			}

			throw new IllegalArgumentException(
					"Hql reference an undefined parameter '" + p + "' on "
							+ method.getDeclaringClass().getName() + "."
							+ method.getName());
		}

		paramDepends = vecParam.toArray(new String[vecParam.size()]);
	}

	public String[] getParamDepends() {
		return paramDepends;
	}

	static boolean nullOrEmptyArray(Object o) {
		if (o == null)
			return true;
		if (o.getClass().isArray()) {
			return (((Object[]) o).length == 0);
		}

		return false;
	}

	public String get(HashMap<String, Object> values) {
		if (optional) {
			for (String param : paramDepends) {
				Object v = values.get(param);
				if (nullOrEmptyArray(v))
					return null;
			}

			// replace - can't be null
			for (String param : replace.values())
				if (nullOrEmptyArray(values.get(param)))
					return null;

			// function - can't be null
			for (String func : depends.values())
				if (nullOrEmptyArray(values.get(func)))
					return null;
		}

		String part = this.part;
		for (String r : replace.keySet()) {
			// SQL Injection handling
			Object val = values.get(replace.get(r));
			if (val == null)
				val = "";
			String str = val.toString();
			if (!P_REPLACE.matcher(str).find())
				throw new IllegalArgumentException(
						"Invalid characters are used in replace string: '"
								+ str + "'.");

			part = part.replaceAll(r, str);
		}

		for (String func : depends.keySet()) {
			Object val = values.get(depends.get(func));
			if (val == null)
				val = "";
			String str = val.toString();
			part = part.replaceAll(Pattern.quote(func), str);
		}

		return part;
	}
}
