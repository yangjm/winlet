package com.aggrepoint.winlet.spring.def;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import com.aggrepoint.winlet.spring.annotation.Action;
import com.aggrepoint.winlet.spring.annotation.Window;
import com.aggrepoint.winlet.spring.annotation.Winlet;

/**
 * Winlet Definition
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletDef {
	private String name;
	private String viewPath;
	private Map<String, WindowDef> windows = new HashMap<String, WindowDef>();
	private Map<String, ActionDef> actions = new HashMap<String, ActionDef>();

	private WinletDef(Class<?> clz) {
		Winlet winlet = AnnotationUtils.findAnnotation(clz, Winlet.class);
		if (winlet == null)
			return;

		this.name = winlet.value();
		if ("".equals(winlet.viewPath()))
			this.viewPath = this.name;
		else
			this.viewPath = winlet.viewPath();

		for (Method method : clz.getMethods()) {
			Action action = AnnotationUtils
					.findAnnotation(method, Action.class);
			if (action != null) {
				actions.put(action.value(), new ActionDef(action.value(), this,
						method));
			} else {
				Window window = AnnotationUtils.findAnnotation(method,
						Window.class);
				if (window != null) {
					windows.put(window.value(), new WindowDef(window.value(),
							this, method));
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public String getViewPath() {
		return viewPath;
	}

	public Collection<WindowDef> getWindows() {
		return windows.values();
	}

	public Collection<ActionDef> getActions() {
		return actions.values();
	}

	public WindowDef getWindow(String name) {
		return windows.get(name);
	}

	public ActionDef getAction(String name) {
		return actions.get(name);
	}

	private static HashMap<Class<?>, WinletDef> htDefs = new HashMap<Class<?>, WinletDef>();
	private static HashMap<String, WinletDef> htDefsByName = new HashMap<String, WinletDef>();

	public static WinletDef getDef(Class<?> clz) {
		synchronized (htDefs) {
			if (htDefs.containsKey(clz))
				return htDefs.get(clz);

			WinletDef def = new WinletDef(clz);
			if (def.getName() == null)
				def = null;
			htDefs.put(clz, def);

			return def;
		}
	}

	public static WinletDef getDef(ApplicationContext context, String name)
			throws Exception {
		synchronized (htDefsByName) {
			if (htDefsByName.containsKey(name))
				return htDefsByName.get(name);

			WinletDef def = getDef(context.getType(name));
			htDefsByName.put(name, def);

			return def;
		}
	}
}
