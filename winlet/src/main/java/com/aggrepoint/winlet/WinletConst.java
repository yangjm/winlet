package com.aggrepoint.winlet;

public interface WinletConst {
	public static final int WINLET_SCOPE = 15;
	public static final String REQUEST_ATTR_REQUEST = "com.aggrepoint.winlet.request";

	// /////////////////////////////////////////////////
	//
	// Servlet Request属性
	//
	// /////////////////////////////////////////////////
	public static final String REQUEST_ATTR_WINLET = "com.aggrepoint.winlet.winlet";

	// /////////////////////////////////////////////////
	//
	// 嵌套视图请求头
	//
	// /////////////////////////////////////////////////

	/** 嵌套视图请求头：视图ID */
	public static final String REQUEST_HEADER_VIEW_HEADER_ID = "com.aggrepoint.winlet.view.id";
	/** 嵌套视图请求头：RequestPath */
	public static final String REQUEST_HEADER_VIEW_HEADER_REQ_PATH = "com.aggrepoint.winlet.req.path";
}
