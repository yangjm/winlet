package com.aggrepoint.winlet.form;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
/**
 * 校验规则
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public @interface Validate {
	/** 要校验的属性名称 */
	String name() default "";

	/** 要校验的属性模式 */
	String pattern() default "";

	/** 内置校验方法id */
	String id() default "";

	/** 自定义校验方法 */
	String method() default "";

	/** 校验错误信息 */
	String error() default "";

	/** 是否在校验方法后执行。缺省在执行Validate所属的校验方法前执行该校验规则 */
	boolean after() default false;

	/** 校验成功后的处理 */
	ValidateResultType passskip() default ValidateResultType.PASS_CONTINUE;

	/** 校验失败后的处理 */
	ValidateResultType failskip() default ValidateResultType.FAILED_SKIP_PROPERTY;

	/** 校验参数 */
	String[] args() default {};
}
