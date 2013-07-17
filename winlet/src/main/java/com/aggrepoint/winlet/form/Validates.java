package com.aggrepoint.winlet.form;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
/**
 * 校验规则组
 */
public @interface Validates {
	Validate[] value();
}
