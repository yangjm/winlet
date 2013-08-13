package com.aggrepoint.winlet;

import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;

import com.aggrepoint.winlet.spring.def.ViewDef;
import com.aggrepoint.winlet.spring.def.WidgetDef;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletManager implements WinletConst {
	public static final String WINLET_SESSION_KEY = "com.aggrepoint.winlet";
	public static final String WINLETINSTS_SESSION_KEY = "com.aggrepoint.winsts";
	public static final String FORM_BY_WIDGET_SESSION_KEY = "com.aggrepoint.formbywinlet";
	public static final String FORM_BY_ID_SESSION_KEY = "com.aggrepoint.formbyid";

	static long FORM_ID = 0;

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
	 * <pre>
	 * WIN_INSTANCES用于保存实例范围为PROTOTYPE的Winlet对应的WinInstance实例，Key为页面ID。
	 * 其他实例范围的Winlet的WinInstance实例保存在会话中，会话参数名称WININSTS_SESSION_KEY，Key为页面ID。
	 * </pre>
	 */
	static Hashtable<String, Vector<WinletInstance>> WIN_INSTANCES = new Hashtable<String, Vector<WinletInstance>>();

	static Hashtable<String, Vector<WinletInstance>> getWinInstances(
			Scope scope, HttpServletRequest req) {
		if (scope == Scope.PROTOTYPE)
			return WIN_INSTANCES;

		HttpSession session = req.getSession(true);

		synchronized (session) {
			@SuppressWarnings("unchecked")
			Hashtable<String, Vector<WinletInstance>> ht = (Hashtable<String, Vector<WinletInstance>>) session
					.getAttribute(WINLETINSTS_SESSION_KEY);

			if (ht == null) {
				ht = new Hashtable<String, Vector<WinletInstance>>();
				session.setAttribute(WINLETINSTS_SESSION_KEY, ht);
			}
			return ht;
		}
	}

	static Object getWinlet(ReqInfo req, WidgetDef winletDef) {
		Hashtable<String, Object> ht = getWinletHashtable(winletDef.getScope(),
				req.getSession());

		switch (winletDef.getScope()) {
		case PAGE:
			return ht.get(winletDef.getName() + "/P/" + req.getPageId());
		case SESSION:
			return ht.get(winletDef.getName() + "/S");
		case INSTANCE:
			return ht.get(winletDef.getName() + req.getWinId());
		case PROTOTYPE:
		default:
			return ht.get(winletDef.getName());
		}
	}

	static void putWinlet(ReqInfo req, WidgetDef winletDef, Object winlet) {
		Hashtable<String, Object> ht = getWinletHashtable(winletDef.getScope(),
				req.getSession());

		switch (winletDef.getScope()) {
		case PAGE:
			ht.put(winletDef.getName() + "/P/" + req.getPageId(), winlet);
			break;
		case SESSION:
			ht.put(winletDef.getName() + "/S", winlet);
			break;
		case INSTANCE:
			ht.put(winletDef.getName() + req.getWinId(), winlet);
			break;
		default:
		}
		ht.put(winletDef.getName() + req.getPageId(), winlet);
		ht.put(winletDef.getName(), winlet);
	}

	static synchronized Object getWinlet(ApplicationContext context,
			ReqInfo req, WidgetDef winletDef) throws Exception {
		Object winlet = getWinlet(req, winletDef);
		if (winlet == null) {
			winlet = context.getBean(winletDef.getName());
			putWinlet(req, winletDef, winlet);
		}
		return winlet;
	}

	/**
	 * 根据Winlet名称在当前页面查找Winlet
	 */
	public static Object getWinletInPage(ReqInfo req, String name)
			throws Exception {
		// 首先在嵌套Winlet中查找
		Object w = req.getViewInstance().wis.viewInstance.getEmbedded(name);
		if (w != null)
			return w;

		// 其次查找PROTOTYPE实例范围
		Hashtable<String, Object> ht = getWinletHashtable(Scope.PROTOTYPE,
				req.getSession());
		w = ht.get(name + req.getPageId());
		if (w != null)
			return w;

		// 再查找其他实例范围
		ht = getWinletHashtable(Scope.SESSION, req.getSession());
		return ht.get(name + req.getPageId());
	}

	/**
	 * 根据Winlet名称在所有已出浏览过的（若未浏览过Winlet实例不存在）页面中查找Winlet
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

	public static Vector<WinletInstance> getWinInstancesInPage(ReqInfo req) {
		Vector<WinletInstance> vec = new Vector<WinletInstance>();

		Vector<WinletInstance> v = getWinInstances(Scope.PROTOTYPE,
				req.getRequest()).get(req.getPageId());
		if (v != null)
			vec.addAll(v);

		v = getWinInstances(Scope.SESSION, req.getRequest()).get(
				req.getPageId());
		if (v != null)
			vec.addAll(v);

		return vec;
	}

	public static WinletInstance getWinInstance(ApplicationContext context,
			ReqInfo req, ViewDef def) throws Exception {
		Hashtable<String, Vector<WinletInstance>> ht = getWinInstances(def
				.getWinletDef().getScope(), req.getRequest());
		Vector<WinletInstance> wis;
		synchronized (ht) {
			wis = ht.get(req.getPageId());
			if (wis == null) {
				wis = new Vector<WinletInstance>();
				ht.put(req.getPageId(), wis);
			}
		}

		for (WinletInstance wi : wis) {
			if (wi.iid.equals(req.getWinId())) {
				if (wi.viewInstance.viewDef != def) {
					// Update the WinInstance
					wis.remove(wi);
					wi = new WinletInstance(req.getWinId(), def, getWinlet(
							context, req, def.getWinletDef()));
					wis.add(wi);
				}
				return wi;
			}
		}

		WinletInstance wi = new WinletInstance(req.getWinId(), def, getWinlet(
				context, req, def.getWinletDef()));
		wis.add(wi);

		return wi;
	}

	public static ViewInstance getViewInstance(ApplicationContext context,
			ReqInfo req, ViewDef def) throws Exception {
		return getWinInstance(context, req, def).findView(req.getViewId());
	}
}
