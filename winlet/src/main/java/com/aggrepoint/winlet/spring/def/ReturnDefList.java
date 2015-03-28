package com.aggrepoint.winlet.spring.def;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import com.aggrepoint.winlet.spring.annotation.Return;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ReturnDefList {
	private Hashtable<String, ArrayList<ReturnDef>> htRetDefs = new Hashtable<String, ArrayList<ReturnDef>>();

	private ArrayList<ReturnDef> getRetDefList(String key) {
		synchronized (htRetDefs) {
			ArrayList<ReturnDef> list = htRetDefs.get(key);
			if (list == null) {
				list = new ArrayList<ReturnDef>();
				htRetDefs.put(key, list);
			}

			return list;
		}
	}

	/**
	 * 如果方法被重载，提取父类被重载方法上声明的Return注解，这样重载方法无需重新声明在被重载方法上已经声明过的@Return
	 * 
	 * @param all
	 * @param method
	 * @param clz
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	private void getRets(HashMap<String, Return> all, Method method,
			Class<?> clz) throws NoSuchMethodException, SecurityException {
		if (!clz.getSuperclass().equals(Object.class))
			getRets(all, method, clz.getSuperclass());

		Method m = clz.getDeclaredMethod(method.getName(),
				method.getParameterTypes());
		if (m != null) {
			Return[] rets = m.getAnnotationsByType(Return.class);
			if (rets != null)
				for (Return ret : rets)
					all.put(ret.value(), ret);
		}
	}

	public ReturnDefList(Method method) {
		Return[] rets = null;

		if (method.getDeclaringClass().getSuperclass().equals(Object.class)) { // 没有父类
			rets = method.getAnnotationsByType(Return.class);
		} else { // 有父类，需要检查合并父类上声明的Return
			HashMap<String, Return> allRets = new HashMap<String, Return>();

			try {
				getRets(allRets, method, method.getDeclaringClass());
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (allRets.size() > 0)
				rets = allRets.values().toArray(new Return[allRets.size()]);
		}

		if (rets != null)
			for (Return ret : rets) {
				ReturnDef rd = new ReturnDef(ret);
				if (rd.hasValue())
					getRetDefList(rd.getValue()).add(rd);
			}
	}

	public ArrayList<ReturnDef> getReturnDef(String code) {
		ArrayList<ReturnDef> list = htRetDefs.get(code);
		if (list == null || list.size() == 0)
			return htRetDefs.get(Return.NOT_SPECIFIED);

		return list;
	}
}
