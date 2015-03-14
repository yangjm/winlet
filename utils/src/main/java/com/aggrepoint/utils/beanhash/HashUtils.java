package com.aggrepoint.utils.beanhash;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;

import org.springframework.beans.BeanWrapperImpl;

public class HashUtils {
	static class HashProperty {
		String name;
		String[] fromProps;

		public HashProperty(String name, String value) {
			this.name = name;
			fromProps = value.split(", ");
		}
	}

	static MessageDigest md;
	static {
		try {
			md = MessageDigest.getInstance("SHA-256");
		} catch (Exception e) {
		}
	}

	public static String hash(String str) {
		synchronized (md) {
			try {
				md.update(str.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}

			return Base64.getEncoder().encodeToString(md.digest());
		}
	}

	private static HashMap<Class<?>, HashSet<HashProperty>> htHashProperties = new HashMap<Class<?>, HashSet<HashProperty>>();

	public static HashSet<Field> getFields(Class<?> c) {
		HashSet<Field> fields = new HashSet<Field>();
		Arrays.stream(c.getDeclaredFields()).forEach(fields::add);
		if (c.getSuperclass() != null && c.getSuperclass() != c)
			fields.addAll(getFields(c.getSuperclass()));
		return fields;
	}

	private static HashSet<HashProperty> getHashProperties(Object obj) {
		Class<?> c = obj.getClass();

		if (!htHashProperties.containsKey(c)) {
			HashSet<HashProperty> props = new HashSet<HashProperty>();

			for (Field f : getFields(c)) {
				for (Annotation ann : f.getAnnotations())
					if (ann instanceof Hash)
						props.add(new HashProperty(f.getName(), ((Hash) ann)
								.value()));
			}

			htHashProperties.put(c, props);
		}

		return htHashProperties.get(c);
	}

	public static <T> T setHashProperties(T obj) {
		HashSet<HashProperty> props = getHashProperties(obj);

		if (props.size() > 0) {
			BeanWrapperImpl wrap = new BeanWrapperImpl(obj);

			for (HashProperty prop : props) {
				StringBuffer sb = new StringBuffer();

				for (String p : prop.fromProps) {
					Object val = wrap.getPropertyValue(p);
					sb.append(p).append(": ").append(val == null ? "" : val)
							.append("; ");
				}

				wrap.setPropertyValue(prop.name, hash(sb.toString()));
			}
		}
		return obj;
	}
}
