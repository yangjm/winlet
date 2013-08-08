package com.aggrepoint.winlet.form;

import java.util.Vector;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public abstract class Change {
	String input;
	String type;

	protected Change(String input, String type) {
		this.input = input;
		this.type = type;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public static Vector<Change> find(Vector<Change> changes, String type,
			String input) {
		Vector<Change> matches = new Vector<Change>();
		for (Change c : changes)
			if (c.input.equals(input) && c.type.equals(type))
				matches.add(c);

		return matches;
	}

	public void addTo(Vector<Change> changes) {
		changes.removeAll(find(changes, type, input));
		changes.add(this);
	}
}
