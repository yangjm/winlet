package com.aggrepoint.winlet;

import java.util.Hashtable;

import javax.servlet.ServletRequest;

import org.springframework.context.ApplicationContext;

import com.aggrepoint.winlet.spring.def.WinletDef;

/**
 * 负责管理Winlet实例和WindowInstance实例
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletManager implements WinletConst {
	/******************************************************************************
	 *
	 * Winlet实例
	 *
	 *****************************************************************************/
	static Hashtable<String, Object> WINLETS = new Hashtable<String, Object>();

	/**
	 * 获取Winlet实例，如果不存在则创建
	 * 
	 * @param context
	 * @param req
	 * @param winletDef
	 * @return
	 * @throws Exception
	 */
	public static synchronized Object getWinlet(ApplicationContext context,
			WinletDef winletDef) throws Exception {
		Object winlet = WINLETS.get(winletDef.getName());
		if (winlet == null) {
			winlet = context.getBean(winletDef.getName());
			WINLETS.put(winletDef.getName(), winlet);
		}
		return winlet;
	}

	static final String PRELOAD_WINLET_ID_KEY = "PRELOAD_WINLET_ID";

	public static String getPreloadWindowId(ServletRequest req) {
		Integer idx = (Integer) req.getAttribute(PRELOAD_WINLET_ID_KEY);
		if (idx == null) {
			idx = 0;
			req.setAttribute(PRELOAD_WINLET_ID_KEY, 1);
		} else
			req.setAttribute(PRELOAD_WINLET_ID_KEY, idx + 1);

		String wid = Integer.toString(1000 + idx);
		return wid.length() + wid;
	}

	static final String CHILD_WINDOW_ID_KEY = "CHILD_WINDOW_ID";

	public static String getChildWindowId(String parentWid, ServletRequest req) {
		Integer idx = (Integer) req.getAttribute(CHILD_WINDOW_ID_KEY);
		if (idx == null) {
			idx = 0;
			req.setAttribute(CHILD_WINDOW_ID_KEY, 1);
		} else
			req.setAttribute(CHILD_WINDOW_ID_KEY, idx + 1);

		String wid = Integer.toString(idx);
		return parentWid + wid.length() + wid;
	}
}
