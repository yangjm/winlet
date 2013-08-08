package com.aggrepoint.winlet.form;

import java.util.Vector;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ChangeDisable extends Change {
	public ChangeDisable(String input) {
		super(input, "d");
	}

	public void addTo(Vector<Change> changes) {
		changes.removeAll(find(changes, "d", input));
		changes.removeAll(find(changes, "e", input));
		changes.add(this);
	}
}
