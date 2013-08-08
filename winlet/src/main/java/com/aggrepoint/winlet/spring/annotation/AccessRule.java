package com.aggrepoint.winlet.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AccessRule {
	String value() default "";

	Class<? extends Exception> exception() default Unspecified.class;
}
