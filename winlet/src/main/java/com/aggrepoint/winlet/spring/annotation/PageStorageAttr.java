package com.aggrepoint.winlet.spring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface PageStorageAttr {
	String value();

	/** 请求参数名称 */
	String reqparam() default "";

	boolean createIfNotExist() default false;
}
