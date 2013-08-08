package com.aggrepoint.winlet.form;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
/**
 * 校验规则组
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public @interface Validates {
	Validate[] value();
}
