package com.aggrepoint.winlet.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.Resource;

import com.aggrepoint.winlet.spring.annotation.Winlet;

/**
 * 修改Winlet类定义：修改类注解。
 * 
 * @see WinletClassVisitor
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletClassLoader extends ClassLoader {
	private ClassLoader innerLoader;

	Map<Class<?>, Class<?>> classMap = Collections
			.synchronizedMap(new HashMap<Class<?>, Class<?>>());

	public WinletClassLoader(ClassLoader inner) {
		super(inner);
		innerLoader = inner;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		Class<?> clz = innerLoader.loadClass(name);

		if (classMap.containsKey(clz))
			return classMap.get(clz);

		Class<?> ret = clz;

		Winlet winlet = AnnotationUtils.findAnnotation(clz, Winlet.class);
		if (winlet != null) {
			try {
				ClassReader cr = new ClassReader(clz.getResource(
						"/" + name.replace('.', '/') + ".class").openStream());

				ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
				cr.accept(new WinletClassVisitor(cw), 0);
				byte[] b = cw.toByteArray();

				ret = defineClass(name, b, 0, b.length);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		classMap.put(clz, ret);
		return ret;
	}

	public Resource convert(Resource res) {
		if (!res.getFilename().endsWith(".class"))
			return res;

		try {
			ClassReader cr = new ClassReader(res.getInputStream());

			// cr.accept(new TraceClassVisitor(null, new ASMifier(),
			// new PrintWriter(System.out)), ClassReader.SKIP_DEBUG);

			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			WinletClassVisitor wcv = new WinletClassVisitor(cw);
			cr.accept(wcv, 0);

			if (!wcv.isWinlet())
				return res;

			byte[] b = cw.toByteArray();

			// cr = new ClassReader(new ByteArrayInputStream(b));
			// cr.accept(new TraceClassVisitor(null, new ASMifier(),
			// new PrintWriter(System.out)), ClassReader.SKIP_DEBUG);

			return new WinletResource(res, b);
		} catch (Exception e) {
		}

		return res;
	}
}
