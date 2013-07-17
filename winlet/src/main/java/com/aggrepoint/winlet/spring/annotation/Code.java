package com.aggrepoint.winlet.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Code {
	String value() default Return.NOT_SPECIFIED;

	String rule() default "";

	String update() default "";

	boolean dialog() default false;

	boolean cache() default false;

	String log() default "";

	String logexclude() default "";

	String title() default "";

	String view() default Return.NOT_SPECIFIED;
}
