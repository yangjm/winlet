package com.aggrepoint.winlet.spring.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Controller;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface Winlet {
	/** 单实例，与会话无关 */
	static final String SCOPE_PROTOTYPE = "prototype";
	/** 每个会话一个实例 */
	static final String SCOPE_SESSION = "session";
	/** 会话中每个页面一个实例 */
	static final String SCOPE_PAGE = "page";
	/** 会话中每个页面每个iid一个实例 */
	static final String SCOPE_INSTANCE = "instance";

	String value();

	String scope() default SCOPE_PROTOTYPE;
}
