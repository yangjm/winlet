package com.aggrepoint.winlet;

/**
 * @see RespHeaderConst
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface ReqConst {
	public final String PARAM_WIN_ACTION = "_a";
	public final String PARAM_WIN_VALIDATE_FIELD = "_vf";
	public final String PARAM_WIN_VALIDATE_FIELD_VALUE = "_vv";
	public final String PARAM_WIN_VALIDATE_FIELD_ID = "_vid";

	public final String PARAM_WIN_FORM_VALIDATE = "_v";
	public final String PARAM_WIN_FORM_FIELDS = "_ff";
	public final String PARAM_WIN_FORM_DISABLED_FIELD = "_fd";

	public final String PARAM_ESCAPED_FRAGMENT = "_escaped_fragment_";

	/**
	 * 与_a一样，_r的值可以包含_w和_wv的值，用!分隔
	 */
	public final String PARAM_WIN_PARAM = "_p";
	/** 路径部分,不包含域名,也不包含参数 */
	public final String PARAM_PAGE_PATH = "_pg";
	/** 完整的页面URL */
	public final String PARAM_PAGE_URL = "_purl";
	/** 是否为全页面刷新而显示 */
	public final String PARAM_PAGE_REFRESH = "_pr";
	/** 是否win$.include()引发的第一次Window请求 */
	public final String PARAM_FIRST_INCLUDE = "_fi";

	/** 不要预加载 */
	public final String PARAM_NO_PRELOAD = "_np";

	/** 从container发起的请求 */
	public final String PARAM_REQ_FROM_CONTAINER = "_c";
}
