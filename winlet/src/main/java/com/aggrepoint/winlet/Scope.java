package com.aggrepoint.winlet;

import com.aggrepoint.winlet.spring.annotation.Winlet;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public enum Scope {
	PROTOTYPE(Winlet.SCOPE_PROTOTYPE, 0),
	SESSION(Winlet.SCOPE_SESSION, 1),
	PAGE(Winlet.SCOPE_PAGE, 2),
	INSTANCE(Winlet.SCOPE_INSTANCE, 3);

	String name;
	int id;

	Scope(String str, int i) {
		name = str;
		id = i;
	}

	public String getName() {
		return name;
	}

	public int getScope() {
		return id;
	}

	public static Scope fromName(String name) {
		if (name == null)
			return PROTOTYPE;

		for (Scope scope : Scope.values()) {
			if (scope.getName().equalsIgnoreCase(name))
				return scope;
		}
		return PROTOTYPE;
	}
}
