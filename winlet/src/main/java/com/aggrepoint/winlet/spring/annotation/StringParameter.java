package com.aggrepoint.winlet.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface StringParameter {
	String value();

	String[] options() default {};

	boolean caseInsensitive() default true;

	/** 如果def为空或空字符串，则用options中的第一个值作为缺省值 */
	String def() default "";
}
