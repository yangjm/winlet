package com.aggrepoint.winlet.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 校验规则组
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public abstract @interface Validates {
	Validate[] value();
}
