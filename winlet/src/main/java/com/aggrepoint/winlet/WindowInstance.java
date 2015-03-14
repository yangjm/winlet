package com.aggrepoint.winlet;

import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.winlet.spring.def.WindowDef;
import com.aggrepoint.winlet.spring.def.WinletDef;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WindowInstance {
	String windowId;

	/**
	 * 仅用于由action返回弹出的对话框中include的子窗口。由于action返回页面弹出对话框时，不会清空已经生成的所有子窗口，
	 * 因此如果弹出对话框中有include子窗口
	 * ，多次反复弹出对话框会导致创建多个被include子窗口对象。为了避免这种情况，在弹出对话框中include窗口时
	 * ，可以给被include的窗口指定一个unique id。当include一个带unique
	 * id参数的子窗口时，框架会把当前窗口中已经存在的相同unique id的子窗口先去除，从而避免多个相同子窗口的问题。
	 */
	String uniqueId;

	Object winlet;
	WindowDef windowDef;
	WindowInstance parent;
	WindowInstance root;
	Vector<WindowInstance> vecChildWindows;
	Hashtable<String, String> htParams;

	public WindowInstance(String wid, Object winlet, WindowDef windowDef,
			Hashtable<String, String> params) {
		windowId = wid;
		this.winlet = winlet;
		this.windowDef = windowDef;
		htParams = params;
		parent = null;
		root = this;
		vecChildWindows = null;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}

	public void setParams(Hashtable<String, String> params) {
		htParams = params;
	}

	public Hashtable<String, String> getParams() {
		return htParams;
	}

	public String getParam(String name) {
		String str = null;
		if (htParams != null)
			str = htParams.get(name);
		if (str != null)
			return str;
		if (parent == null)
			return null;
		return parent.getParam(name);
	}

	public String getId() {
		return windowId;
	}

	public WindowDef getWindowDef() {
		return windowDef;
	}

	public Object getWinlet() {
		return winlet;
	}

	public void clearSub() {
		vecChildWindows = null;
	}

	/**
	 * Window ID的规则：第一级ID长度+第一级ID+第二级ID长度+第二级ID+...
	 * 
	 * @param parent
	 * @param sub
	 * @return
	 */
	static String getWindowId(String parent, int sub) {
		String s = Integer.toString(sub);

		return parent + s.length() + s;
	}

	public static String getRootWindowId(String windowId) {
		if (windowId == null)
			return null;

		if (windowId.equals(""))
			return "";

		if (windowId.length() <= 1)
			return "";

		return windowId.substring(0,
				1 + Integer.parseInt(windowId.substring(0, 1)));
	}

	/**
	 * @param winlet
	 *            可选，如果不指定则以当前WindowInstance对应的winlet作为要添加的winlet
	 * @param windowName
	 *            要添加的winlet的Window名称
	 * @param htParams
	 * @return
	 * @throws Exception
	 */
	public WindowInstance addSub(ReqInfo req, Object winlet, String windowName,
			Hashtable<String, String> htParams, String uniqueId)
			throws Exception {
		WinletManager.privatiseWindowInstance(req, this);

		WinletDef def;

		if (winlet == null) {
			def = windowDef.getWinletDef();
			winlet = this.winlet;
		} else {
			def = WinletDef.getDef(winlet.getClass());
		}

		for (WindowDef wd : def.getWindows())
			if (wd.getName().equals(windowName)) {
				if (vecChildWindows == null)
					vecChildWindows = new Vector<WindowInstance>();

				if (uniqueId != null && !"".equals(uniqueId)) {
					for (WindowInstance wi : vecChildWindows) {
						if (uniqueId.equals(wi.getUniqueId())
								&& wd == wi.getWindowDef()) {
							vecChildWindows.remove(wi);
							break;
						}
					}
				}

				WindowInstance inst = new WindowInstance(getWindowId(windowId,
						vecChildWindows.size() + 1), winlet, wd, htParams);
				inst.setUniqueId(uniqueId);
				inst.parent = this;
				inst.root = this.root;
				vecChildWindows.add(inst);
				return inst;
			}

		throw new Exception("Unable to find window \"" + windowName
				+ "\" in winlet " + winlet.getClass().getName());
	}

	WindowInstance find(String wid) {
		if (windowId.equals(wid))
			return this;
		if (vecChildWindows != null && wid.startsWith(windowId)) {
			for (WindowInstance child : vecChildWindows) {
				if (wid.equals(child.windowId))
					return child;

				if (wid.startsWith(child.windowId))
					return child.find(wid);
			}
		}
		return null;
	}

	/**
	 * 在本WindowInstance以及子WindowInstance中查找与updates匹配的WindowInstance的windowId，
	 * 把找到的windowId加到vecUpdates中
	 * 。updates中要更新的winlet如果没有指定winlet名称，则用defaultWinletName作为名称
	 * 
	 * @param updates
	 * @param vecUpdates
	 * @param defaultWinletName
	 */
	private void getUpdateWindows(Vector<WindowToUpdate> updates,
			Vector<String> vecUpdates, String defaultWinletName) {
		for (WindowToUpdate t : updates) {
			String winletName = t.winletName == null ? defaultWinletName
					: t.winletName;

			if (windowDef.getWinletDef().getName().equals(winletName)
					&& t.windowName.equals(windowDef.getName())) {
				if (t.ensureVisible)
					vecUpdates.add("!" + windowId);
				else
					vecUpdates.add(windowId);
				return;
			}
		}

		if (vecChildWindows != null)
			for (WindowInstance vi : vecChildWindows)
				vi.getUpdateWindows(updates, vecUpdates, defaultWinletName);
	}

	public String translateUpdateWindows(ReqInfo req, String from) {
		if (from == null || from.equals("")) {
			return "";
		}

		boolean bUpdateWholeWin = false;
		Vector<WindowToUpdate> updates = WindowToUpdate.parse(from);
		Vector<String> vecUpdates = new Vector<String>();
		Vector<WindowToUpdate> toRemove = new Vector<WindowToUpdate>();

		for (WindowToUpdate update : updates) {
			if (update.winletName == null) {
				if (update.windowName.equals("window")) {
					bUpdateWholeWin = true;
					toRemove.add(update);
				} else if (update.windowName.equals("parent")) {
					if (parent != null)
						update.windowName = parent.windowId;
					else {
						bUpdateWholeWin = true;
						toRemove.add(update);
					}
				} else if (update.windowName.equals("area"))
					return "area";
				else if (update.windowName.equals("page"))
					return "page";
			}
		}

		updates.removeAll(toRemove);

		// { Match all other windows within same page
		for (WindowInstance wi : WinletManager
				.getAllRootWindowInstancesInPage(req)) {
			if (wi == root)
				continue;

			wi.getUpdateWindows(updates, vecUpdates, windowDef.getWinletDef()
					.getName());
		}
		// }

		if (bUpdateWholeWin)
			vecUpdates.add(root.getId());
		else
			root.getUpdateWindows(updates, vecUpdates, windowDef.getWinletDef()
					.getName());

		StringBuffer sb = new StringBuffer();
		boolean bFirst = true;
		for (String str : vecUpdates) {
			if (bFirst)
				bFirst = false;
			else
				sb.append(",");
			sb.append(str);
		}

		return sb.toString();
	}

	public Object getEmbedded(Class<?> c) throws Exception {
		if (vecChildWindows == null)
			return null;

		for (WindowInstance sub : vecChildWindows) {
			if (sub.winlet.getClass().equals(c))
				return sub.winlet;
		}

		Object winlet = null;
		for (WindowInstance sub : vecChildWindows) {
			winlet = sub.getEmbedded(c);
			if (winlet != null)
				return winlet;
		}
		return null;
	}

	public Object getEmbedded(String name) throws Exception {
		if (vecChildWindows == null)
			return null;

		for (WindowInstance sub : vecChildWindows) {
			if (sub.getWindowDef().getWinletDef().getName().equals(name))
				return sub.winlet;
		}

		Object winlet = null;
		for (WindowInstance sub : vecChildWindows) {
			winlet = sub.getEmbedded(name);
			if (winlet != null)
				return winlet;
		}
		return null;
	}
}
