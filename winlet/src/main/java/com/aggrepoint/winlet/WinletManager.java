package com.aggrepoint.winlet;

import java.util.Hashtable;
import java.util.Optional;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;

import com.aggrepoint.winlet.spring.def.WindowDef;
import com.aggrepoint.winlet.spring.def.WinletDef;

/**
 * 负责管理Winlet实例和WindowInstance实例
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletManager implements WinletConst {
	public static final String WINLET_SESSION_KEY = "com.aggrepoint.winlet";
	public static final String WINDOW_INSTS_SESSION_KEY = "com.aggrepoint.winsts";
	public static final String FORM_BY_WIDGET_SESSION_KEY = "com.aggrepoint.formbywinlet";
	public static final String FORM_BY_ID_SESSION_KEY = "com.aggrepoint.formbyid";

	static long FORM_ID = 0;

	/******************************************************************************
	 *
	 * Winlet实例
	 *
	 *****************************************************************************/

	/**
	 * <pre>
	 * WINLETS用于保存实例范围为PROTOTYPE的Winlet的实例，Key为winlet名称。
	 * 其他实例范围的Winlet实例保存在会话中，会话参数名称WINLET_SESSION_KEY。Key取决于Winlet的实例范围，参见方法putWinlet()
	 * </pre>
	 */
	static Hashtable<String, Object> WINLETS = new Hashtable<String, Object>();

	static Hashtable<String, Object> getWinletHashtable(Scope scope,
			HttpSession session) {
		if (scope == Scope.PROTOTYPE)
			return WINLETS;

		synchronized (session) {
			@SuppressWarnings("unchecked")
			Hashtable<String, Object> ht = (Hashtable<String, Object>) session
					.getAttribute(WINLET_SESSION_KEY);

			if (ht == null) {
				ht = new Hashtable<String, Object>();
				session.setAttribute(WINLET_SESSION_KEY, ht);
			}

			return ht;
		}
	}

	/**
	 * 获取Winlet实例
	 * 
	 * @param req
	 * @param winletDef
	 * @return
	 */
	static Object getWinlet(HttpSession session, String pageId,
			String windowId, String winletName, Scope winletScope) {
		Hashtable<String, Object> ht = getWinletHashtable(winletScope, session);

		switch (winletScope) {
		case PAGE:
			return ht.get(winletName + "/P/" + pageId);
		case SESSION:
			return ht.get(winletName + "/S");
		case INSTANCE:
			return ht.get(winletName + "/P/" + pageId + "/" + windowId);
		case PROTOTYPE:
		default:
			return ht.get(winletName);
		}
	}

	/**
	 * 保存Winlet实例
	 * 
	 * @param session
	 * @param pageId
	 * @param windowId
	 * @param winletName
	 * @param winletScope
	 * @param winlet
	 */
	static void putWinlet(HttpSession session, String pageId, String windowId,
			String winletName, Scope winletScope, Object winlet) {
		Hashtable<String, Object> ht = getWinletHashtable(winletScope, session);

		switch (winletScope) {
		case PAGE:
			ht.put(winletName + "/P/" + pageId, winlet);
			break;
		case SESSION:
			ht.put(winletName + "/S", winlet);
			break;
		case INSTANCE:
			ht.put(winletName + "/P/" + pageId + "/" + windowId, winlet);
			break;
		default:
		}

		ht.put(winletName + "/" + pageId, winlet);
		ht.put(winletName, winlet);
	}

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
			ReqInfo req, WinletDef winletDef) throws Exception {
		Object winlet = getWinlet(req.getSession(), req.getPageId(),
				req.getWindowId(), winletDef.getName(), winletDef.getScope());
		if (winlet == null) {
			winlet = context.getBean(winletDef.getName());
			putWinlet(req.getSession(), req.getPageId(), req.getWindowId(),
					winletDef.getName(), winletDef.getScope(), winlet);
		}
		return winlet;
	}

	/**
	 * 根据名称在当前页面查找Winlet实例
	 */
	public static Object getWinletInPage(ReqInfo req, String name)
			throws Exception {
		// 首先在嵌套Winlet中查找
		Object w = req.getWindowInstance().root.getEmbedded(name);
		if (w != null)
			return w;

		// 其次查找PROTOTYPE实例范围
		Hashtable<String, Object> ht = getWinletHashtable(Scope.PROTOTYPE,
				req.getSession());
		w = ht.get(name + "/" + req.getPageId());
		if (w != null)
			return w;

		// 再查找其他实例范围
		ht = getWinletHashtable(Scope.SESSION, req.getSession());
		return ht.get(name + "/" + req.getPageId());
	}

	/**
	 * 根据Winlet名称在所有已出浏览过的（若未浏览过Winlet实例不存在）页面中查找Winlet实例
	 * 
	 * @param req
	 * @param className
	 * @return
	 */
	public static Object getWinletInSite(ReqInfo req, String name) {
		Hashtable<String, Object> ht = getWinletHashtable(Scope.PROTOTYPE,
				req.getSession());
		Object w = ht.get(name);
		if (w != null)
			return w;
		ht = getWinletHashtable(Scope.SESSION, req.getSession());
		return ht.get(name);
	}

	/******************************************************************************
	 *
	 * WindowInstance实例管理
	 *
	 *****************************************************************************/

	/**
	 * <pre>
	 * WINDOW_INSTS用于保存实例范围为PROTOTYPE并且没有子Window的Winlet对应的WindowInstance实例，Key为页面ID。
	 * 其它的WinInstance实例保存在会话中，会话参数名称WINDOW_INSTS_SESSION_KEY，Key为页面ID。
	 * </pre>
	 */
	static Hashtable<String, Vector<WindowInstance>> WINDOW_INSTS = new Hashtable<String, Vector<WindowInstance>>();

	static Hashtable<String, Vector<WindowInstance>> getSessionWindowInstances(
			HttpServletRequest req) {
		HttpSession session = req.getSession(true);

		synchronized (session) {
			@SuppressWarnings("unchecked")
			Hashtable<String, Vector<WindowInstance>> ht = (Hashtable<String, Vector<WindowInstance>>) session
					.getAttribute(WINDOW_INSTS_SESSION_KEY);

			if (ht == null) {
				ht = new Hashtable<String, Vector<WindowInstance>>();
				session.setAttribute(WINDOW_INSTS_SESSION_KEY, ht);
			}
			return ht;
		}
	}

	public static Vector<WindowInstance> getAllRootWindowInstancesInPage(
			ReqInfo req) {
		Vector<WindowInstance> vec = new Vector<WindowInstance>();

		Vector<WindowInstance> v = WINDOW_INSTS.get(req.getPageId());
		if (v != null)
			vec.addAll(v);

		v = getSessionWindowInstances(req.getRequest()).get(req.getPageId());
		if (v != null)
			vec.addAll(v);

		return vec;
	}

	static Vector<WindowInstance> getWindowInstancesInPage(
			Hashtable<String, Vector<WindowInstance>> ht, String pageId) {
		synchronized (ht) {
			Vector<WindowInstance> wis = ht.get(pageId);
			if (wis == null) {
				wis = new Vector<WindowInstance>();
				ht.put(pageId, wis);
			}
			return wis;
		}
	}

	static WindowInstance findRootWindowInstance(Vector<WindowInstance> wis,
			String wid) {
		Optional<WindowInstance> find = wis.stream()
				.filter(p -> wid.equals(p.getId())).findFirst();
		return find.isPresent() ? find.get() : null;
	}

	public static WindowInstance getOrCreateRootWindowInstance(
			ApplicationContext context, ReqInfo req, WindowDef def)
			throws Exception {
		// 先在会话中查找
		Vector<WindowInstance> wis = getWindowInstancesInPage(
				getSessionWindowInstances(req.getRequest()), req.getPageId());
		WindowInstance wi = findRootWindowInstance(wis, req.getRootWindowId());

		if (wi == null && def.getWinletDef().getScope() == Scope.PROTOTYPE) {
			wis = getWindowInstancesInPage(WINDOW_INSTS, req.getPageId());
			wi = findRootWindowInstance(wis, req.getRootWindowId());
		}

		if (wi == null || wi != null && wi.windowDef != def) {
			wis.remove(wi);

			wi = new WindowInstance(req.getRootWindowId(), getWinlet(context,
					req, def.getWinletDef()), def, null);
			wis.add(wi);
		}

		return wi;
	}

	public static WindowInstance getWindowInstance(ApplicationContext context,
			ReqInfo req, WindowDef def) throws Exception {
		return getOrCreateRootWindowInstance(context, req, def).find(
				req.getWindowId());
	}

	/**
	 * 把WINDOW_INSTS中的窗口移到会话中。当一个公用的窗口添加了子窗口时，需要把这个窗口私有化。
	 * 
	 * @param wi
	 * @return
	 */
	static boolean privatiseWindowInstance(ReqInfo req, WindowInstance wi) {
		if (wi.root == wi
				&& wi.windowDef.getWinletDef().getScope() == Scope.PROTOTYPE) {
			Vector<WindowInstance> wis = getWindowInstancesInPage(WINDOW_INSTS,
					req.getPageId());

			if (wis.contains(wi)) {
				wis.remove(wi);
				getWindowInstancesInPage(
						getSessionWindowInstances(req.getRequest()),
						req.getPageId()).add(wi);

				return true;
			}
		}

		return false;
	}
}
