package com.aggrepoint.dao;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class Func {
	static Pattern P_FUNC = Pattern.compile(":(([\\w\\d]+)\\[(.*?)\\])");

	String match;
	IFunc func;
	String[] params;

	protected Func(List<IFunc> funcs, Matcher m)
			throws FunctionNotFoundException {
		match = m.group(1);

		String funcName = m.group(2);

		for (IFunc f : funcs)
			if (f.getName().equals(funcName))
				func = f;

		if (func == null)
			throw new FunctionNotFoundException(funcName);

		Vector<String> vec = new Vector<String>();
		StringTokenizer st = new StringTokenizer(m.group(3), ",");
		while (st.hasMoreTokens())
			vec.add(st.nextToken().trim());

		params = vec.toArray(new String[vec.size()]);
	}

	public String exec(Method method, Object[] args, Annotation[][] anns) {
		return func.exec(method, params, args, anns);
	}

	public String getMatch() {
		return match;
	}

	public String getFuncName() {
		return func.getName();
	}

	public static Func[] getFunctions(List<IFunc> funcs, String hql)
			throws FunctionNotFoundException {
		Vector<Func> vec = new Vector<Func>();

		Matcher m = P_FUNC.matcher(hql);
		while (m.find()) {
			vec.add(new Func(funcs, m));
		}

		if (vec.size() == 0)
			return null;

		return vec.toArray(new Func[vec.size()]);
	}
}
