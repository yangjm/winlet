package com.aggrepoint.dao.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
	String value() default "";

	String sql() default "";

	String count() default "*";

	String name();

	Class<?> entity();

	String alias();

	long ttl();
}
