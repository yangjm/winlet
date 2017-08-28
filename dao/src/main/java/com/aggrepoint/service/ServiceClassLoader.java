package com.aggrepoint.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aggrepoint.dao.DaoService;
import com.aggrepoint.dao.annotation.DefaultDao;

/**
 * Add implementation for methods declared in DaoService interface but not in
 * service implementation class<br>
 * <br>
 * This class loader modifies class that implements DaoService method and has
 * one field with @DefaultDao annotation
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ServiceClassLoader extends ClassLoader {
	private static final Log logger = LogFactory
			.getLog(ServiceClassLoader.class);

	private ClassLoader innerLoader;
	private HashMap<String, String> tmMap = new HashMap<String, String>();

	Map<Class<?>, Class<?>> classMap = Collections
			.synchronizedMap(new HashMap<Class<?>, Class<?>>());
	/** Dao fields of service class */
	static HashMap<Class<?>, Field> daoFields = new HashMap<Class<?>, Field>();

	public ServiceClassLoader(ClassLoader inner) {
		super(inner);
		innerLoader = inner;
	}

	public ServiceClassLoader(ClassLoader inner,
			TransactionManagerConfig... tmcfgs) {
		super(inner);
		innerLoader = inner;

		if (tmcfgs != null && tmcfgs.length > 0) {
			for (TransactionManagerConfig tmc : tmcfgs) {
				if (tmc != null && tmc.getName() != null
						&& tmc.getClasses() != null
						&& tmc.getClasses().length > 0) {
					for (Class<?> c : tmc.getClasses()) {
						if (c != null)
							tmMap.put(c.getName(), tmc.getName());
					}
				}
			}
		}
	}

	public static Field findDaoField(Class<?> clz) {
		if (!daoFields.containsKey(clz)) {
			daoFields.put(clz, null);

			Field daoField = null;
			for (Field field : clz.getDeclaredFields()) {
				DefaultDao[] anns = field
						.getAnnotationsByType(DefaultDao.class);
				if (anns != null && anns.length > 0) {
					if (daoField != null)
						throw new IllegalArgumentException(
								"Mutiple @DefaultDao fields found in service class: "
										+ clz.getName());

					daoField = field;
				}
			}

			if (daoField != null)
				daoField.setAccessible(true);
			daoFields.put(clz, daoField);
		}

		return daoFields.get(clz);
	}

	/**
	 * Check whether c implements interface i
	 * 
	 * @param c
	 * @param i
	 * @return
	 */
	public static boolean implementsInterface(Class<?> c, Class<?> i) {
		if (c == i)
			return true;

		for (Class<?> x : c.getInterfaces())
			if (implementsInterface(x, i))
				return true;

		return false;
	}

	public static HashSet<Method> findMethods(Class<?> c, Class<?> i) {
		HashSet<Method> methods = new HashSet<Method>();

		if (c.isInterface()) {
			if (!implementsInterface(c, i))
				return methods;

			for (Method m : c.getDeclaredMethods())
				methods.add(m);
		}

		for (Class<?> x : c.getInterfaces())
			methods.addAll(findMethods(x, i));

		return methods;
	}

	public static boolean sameSignature(Method a, Method b) {
		if (!a.getName().equals(b.getName()))
			return false;

		if (a.getParameterCount() != b.getParameterCount())
			return false;

		for (int i = a.getParameterCount() - 1; i >= 0; i--)
			if (!a.getParameters()[i].getType().equals(
					b.getParameters()[i].getType()))
				return false;

		return true;
	}

	static boolean same(CtClass a, Class<?> b) throws NotFoundException {
		if (a.isArray()) {
			if (!b.isArray())
				return false;
			return same(a.getComponentType(), b.getComponentType());
		}

		return a.getName().equals(b.getName());
	}

	public static boolean sameSignature(CtMethod a, Method b)
			throws NotFoundException {
		if (!a.getName().equals(b.getName()))
			return false;

		if (a.getParameterTypes().length != b.getParameterCount())
			return false;

		for (int i = a.getParameterTypes().length - 1; i >= 0; i--) {
			CtClass typea = a.getParameterTypes()[i];
			Class<?> typeb = b.getParameters()[i].getType();

			if (!same(typea, typeb))
				return false;
		}

		return true;
	}

	public static boolean declares(Class<?> clz, Method method) {
		for (Method m : clz.getDeclaredMethods())
			if (sameSignature(m, method))
				return true;
		return false;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> clz = innerLoader.loadClass(name);

		if (classMap.containsKey(clz))
			return classMap.get(clz);

		Class<?> ret = clz;

		if (implementsInterface(clz, DaoService.class)) {
			Field daoField = findDaoField(clz);
			if (daoField != null) {
				// Modify DaoService implementation
				HashSet<Method> methods = findMethods(clz, DaoService.class);
				HashSet<Method> find = findMethods(daoField.getType(),
						DaoService.class);
				methods.retainAll(find);
				Method[] toadd = (Method[]) methods.stream()
						.filter(p -> !declares(clz, p))
						.filter(p -> !Modifier.isStatic(p.getModifiers()))
						.toArray(size -> new Method[size]);

				if (toadd != null && toadd.length > 0) {
					try {
						final ClassLoader classLoader = this;
						ClassPool classPool = new ClassPool() {
							// use ServiceClassLoader as class loader in order
							// to use existing class name for modified class
							public ClassLoader getClassLoader() {
								return classLoader;
							}
						};
						classPool.appendClassPath(new LoaderClassPath(this));

						CtClass ctclass = classPool.get(clz.getName());

						String tm = tmMap.get(daoField.getType().getName());
						if (tm != null) {
							// Change @Transactional annotation to use specified
							// transaction manager
							ConstPool cp = ctclass.getClassFile()
									.getConstPool();
							Iterator<?> it = ctclass.getClassFile()
									.getAttributes().iterator();
							while (it.hasNext()) {
								AttributeInfo ai = (AttributeInfo) it.next();
								if (ai instanceof AnnotationsAttribute) {
									AnnotationsAttribute aa = (AnnotationsAttribute) ai;
									if (aa.removeAnnotation("org.springframework.transaction.annotation.Transactional")) {
										Annotation anno = new Annotation(
												"org.springframework.transaction.annotation.Transactional",
												cp);
										anno.addMemberValue("value",
												new StringMemberValue(tm, cp));
										aa.addAnnotation(anno);
									}
								}
							}
						}

						// add interface method implementation
						for (Method method : toadd) {
							CtClass ds = classPool.get(method
									.getDeclaringClass().getName());

							boolean found = false;
							for (CtMethod m : ds.getDeclaredMethods())
								if (sameSignature(m, method)) {
									CtMethod newmethod = null;
									try {
										newmethod = CtNewMethod.copy(m,
												ctclass, null);
									} catch (Exception e) {
										e.printStackTrace();
									}

									// { Change method implementation to call
									// dao method
									StringBuffer code = new StringBuffer();
									if (!newmethod.getReturnType().getName()
											.equals("void"))
										code.append("return ");
									code.append(daoField.getName() + "."
											+ method.getName() + "($$);");

									newmethod.setBody(code.toString());
									// }

									ctclass.addMethod(newmethod);
									found = true;
									break;
								}

							if (!found) {
								logger.error("Unable to find CtMethod for method "
										+ method
										+ ", this DaoService can't be implemented automatically on class "
										+ clz.getName());
							}
						}

						ret = ctclass.toClass();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		classMap.put(clz, ret);
		return ret;
	}
}
