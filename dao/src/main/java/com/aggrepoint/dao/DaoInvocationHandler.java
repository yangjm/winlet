package com.aggrepoint.dao;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.springframework.core.convert.ConversionService;

import com.aggrepoint.dao.annotation.Cache;
import com.aggrepoint.dao.annotation.Delete;
import com.aggrepoint.dao.annotation.Deletes;
import com.aggrepoint.dao.annotation.Find;
import com.aggrepoint.dao.annotation.Finds;
import com.aggrepoint.dao.annotation.Load;
import com.aggrepoint.dao.annotation.Update;
import com.aggrepoint.dao.annotation.Updates;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public class DaoInvocationHandler<T> implements InvocationHandler, Serializable {
	private static final long serialVersionUID = 1L;

	DaoResourceProvider resProvider;
	Class<T> clz;
	Hashtable<Method, ArrayList<DaoMethod<T>>> htDaoMethods = new Hashtable<Method, ArrayList<DaoMethod<T>>>();

	void addDaoMethod(Method method, DaoMethod<T> daoMethod) {
		ArrayList<DaoMethod<T>> arr = htDaoMethods.get(method);
		if (arr == null) {
			arr = new ArrayList<DaoMethod<T>>();
			htDaoMethods.put(method, arr);
		}

		arr.add(daoMethod);
	}

	public DaoInvocationHandler(DaoResourceProvider resProvider, ConversionService cs, Class<?> daoInterface,
			Class<T> clz, List<IFunc> funcs) {
		this.resProvider = resProvider;
		this.clz = clz;

		for (Method method : daoInterface.getMethods()) {
			boolean found = false;

			for (Method m : DaoService.class.getMethods()) {
				if (method.equals(m)) {
					addDaoMethod(method, new DaoBaseMethod<T>(clz, method, resProvider));
					found = true;
					break;
				}
			}

			if (!found)
				for (Method m : HibernateDao.class.getMethods()) {
					if (method.equals(m)) {
						addDaoMethod(method, new DaoBaseMethod<T>(clz, method, resProvider));
						found = true;
						break;
					}
				}

			if (!found)
				for (Annotation ann : method.getDeclaredAnnotations()) {
					Class<?> t = ann.annotationType();
					if (t == Find.class || t == Cache.class || t == Update.class || t == Delete.class
							|| t == Load.class) {
						addDaoMethod(method, new DaoAnnotationMethod<T>(clz, method, ann, funcs, resProvider, cs));
						found = true;
					} else if (t == Finds.class) {
						for (Annotation a : ((Finds) ann).value())
							addDaoMethod(method, new DaoAnnotationMethod<T>(clz, method, a, funcs, resProvider, cs));
						found = true;
					} else if (t == Updates.class) {
						for (Annotation a : ((Updates) ann).value())
							addDaoMethod(method, new DaoAnnotationMethod<T>(clz, method, a, funcs, resProvider, cs));
						found = true;
					} else if (t == Deletes.class) {
						for (Annotation a : ((Deletes) ann).value())
							addDaoMethod(method, new DaoAnnotationMethod<T>(clz, method, a, funcs, resProvider, cs));
						found = true;
					}
				}

			if (!found && !method.isDefault())
				throw new IllegalArgumentException(
						"Unsupported method " + daoInterface.getName() + "." + method.getName() + ".");
		}
	}

	static Method equals;
	static Method hashCode;
	static Method toString;

	static {
		try {
			equals = Object.class.getMethod("equals", Object.class);
			hashCode = Object.class.getMethod("hashCode");
			toString = Object.class.getMethod("toString");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		try {
			ArrayList<DaoMethod<T>> arr = htDaoMethods.get(method);
			if (arr != null) {
				Object ret = null;
				for (DaoMethod<T> hdm : arr) {
					if (hdm.match(args))
						ret = hdm.invoke(proxy, method, args);
				}
				return ret;
			}

			if (equals.equals(method)) {
				return proxy == args[0];
			} else if (hashCode.equals(method)) {
				return proxy.getClass().hashCode();
			} else if (toString.equals(method)) {
				return proxy.getClass().toString();
			} else if (method.isDefault()) {
				Class<?> declaringClass = method.getDeclaringClass();
				return DaoAnnotationMethod.CONSTRUCTOR.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
						.unreflectSpecial(method, declaringClass).bindTo(proxy).invokeWithArguments(args);
			}

			return null;
		} catch (Throwable t) {
			throw t;
		}
	}
}
