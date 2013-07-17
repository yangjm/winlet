package com.aggrepoint.winlet.spring.def;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import com.aggrepoint.winlet.Scope;
import com.aggrepoint.winlet.spring.annotation.Action;
import com.aggrepoint.winlet.spring.annotation.Window;
import com.aggrepoint.winlet.spring.annotation.Winlet;

public class WidgetDef {
	private String name;
	private Scope scope = Scope.PROTOTYPE;
	private Map<String, ViewDef> views = new HashMap<String, ViewDef>();
	private Map<String, ActionDef> actions = new HashMap<String, ActionDef>();

	private WidgetDef(Class<?> clz) {
		Winlet winlet = AnnotationUtils.findAnnotation(clz, Winlet.class);
		if (winlet == null)
			return;

		this.name = winlet.value();
		this.scope = Scope.fromName(winlet.scope());

		for (Method method : clz.getMethods()) {
			Action action = AnnotationUtils
					.findAnnotation(method, Action.class);
			if (action != null) {
				actions.put(action.value(), new ActionDef(action.value(), this,
						method));
			} else {
				Window view = AnnotationUtils.findAnnotation(method,
						Window.class);
				if (view != null) {
					views.put(view.value(), new ViewDef(view.value(), this,
							method));
				}
			}
		}
	}

	public String getName() {
		return name;
	}

	public Scope getScope() {
		return scope;
	}

	public Collection<ViewDef> getViews() {
		return views.values();
	}

	public Collection<ActionDef> getActions() {
		return actions.values();
	}

	public ViewDef getView(String name) {
		return views.get(name);
	}

	public ActionDef getAction(String name) {
		return actions.get(name);
	}

	private static HashMap<Class<?>, WidgetDef> htDefs = new HashMap<Class<?>, WidgetDef>();
	private static HashMap<String, WidgetDef> htDefsByName = new HashMap<String, WidgetDef>();

	public static WidgetDef getDef(Class<?> clz) {
		if (htDefs.containsKey(clz))
			return htDefs.get(clz);

		WidgetDef def = new WidgetDef(clz);
		if (def.getName() == null)
			def = null;
		htDefs.put(clz, def);

		return def;
	}

	public static WidgetDef getDef(ApplicationContext context, String name)
			throws Exception {
		if (htDefsByName.containsKey(name))
			return htDefsByName.get(name);

		WidgetDef def = getDef(context.getType(name));
		htDefsByName.put(name, def);

		return def;
	}
}
