package com.aggrepoint.dao;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aggrepoint.dao.annotation.Like;
import com.aggrepoint.dao.annotation.Param;
import com.aggrepoint.dao.annotation.Replace;

public class QueryPart {
	static Pattern P_PARAM = Pattern.compile(":(\\w+)");
	static Pattern P_REPLACE = Pattern.compile("^[\\w+\\s+\\.,]*$");

	boolean optional;
	String part;
	String[] paramDepends;
	Hashtable<String, Param> params;
	Hashtable<String, Like> likes;
	Hashtable<String, String> replace = new Hashtable<String, String>();

	public QueryPart(Method method, boolean optional, String part,
			Hashtable<String, Param> params, Hashtable<String, Like> likes,
			Hashtable<String, Replace> replaces) {
		this.optional = optional;
		this.part = part;
		this.params = params;
		this.likes = likes;

		HashSet<String> vecParam = new HashSet<String>();

		Matcher m = P_PARAM.matcher(part);
		while (m.find()) {
			String p = m.group(1);
			if (params.containsKey(p) || likes.containsKey(p)) {
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

	public String get(HashMap<String, Object> values) {
		if (optional) {
			for (String param : paramDepends) {
				Object v = values.get(param);
				Param p = params.get(param);
				if (p != null) {
					if (v == null)
						return null;
				} else if (v == null) // like - can't be null
					return null;
			}

			// replace - can't be null
			for (String param : replace.values())
				if (values.get(param) == null)
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

		return part;
	}
}
