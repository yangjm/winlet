package com.aggrepoint.winlet.spring;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.springframework.core.annotation.AnnotationUtils;

import com.aggrepoint.winlet.spring.annotation.Action;
import com.aggrepoint.winlet.spring.annotation.Window;
import com.aggrepoint.winlet.spring.annotation.Winlet;

/**
 * 修改Winlet类定义：修改类注解。
 * 
 * @see WinletClassVisitor
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletClassLoader extends ClassLoader {
	private ClassLoader innerLoader;

	Map<Class<?>, Class<?>> classMap = Collections.synchronizedMap(new HashMap<Class<?>, Class<?>>());

	static Map<String, Class<?>> pathToClassMap = new HashMap<String, Class<?>>();

	public WinletClassLoader(ClassLoader inner) {
		super(inner);
		innerLoader = inner;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> clz = innerLoader.loadClass(name);
		if (name.startsWith("org.springframework")) // 避免AnnotationUtils.findAnnotation打印异常
			return clz;

		if (classMap.containsKey(clz))
			return classMap.get(clz);

		Class<?> ret = clz;

		Winlet winlet = AnnotationUtils.findAnnotation(clz, Winlet.class);
		boolean process = winlet != null;
		if (!process) {
			try {
				for (Method m : clz.getMethods()) {
					process = AnnotationUtils.findAnnotation(m, Action.class) != null
							|| AnnotationUtils.findAnnotation(m, Window.class) != null;
					if (process)
						break;
				}
			} catch (NoClassDefFoundError e) {
			}
		}

		if (process) {
			try {
				ClassReader cr = new ClassReader(clz.getResource("/" + name.replace('.', '/') + ".class").openStream());

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				cr.accept(new WinletClassVisitor(cw), 0);
				byte[] b = cw.toByteArray();

				ret = defineClass(name, b, 0, b.length);

				if (winlet != null)
					pathToClassMap.put(winlet.value().startsWith("/") ? winlet.value() : "/" + winlet.value(), ret);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		classMap.put(clz, ret);
		return ret;
	}

	public static Class<?> getWinletClassByPath(String path) {
		if (path == null)
			return null;
		if (!path.startsWith("/"))
			path = "/" + path;
		return pathToClassMap.get(path);
	}

	public static Collection<Class<?>> getClasses() {
		return pathToClassMap.values();
	}
}
