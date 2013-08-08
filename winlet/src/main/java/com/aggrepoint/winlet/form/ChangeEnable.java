package com.aggrepoint.winlet.form;

import java.util.Vector;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ChangeEnable extends Change {
	public ChangeEnable(String input) {
		super(input, "e");
	}

	public void addTo(Vector<Change> changes) {
		changes.removeAll(find(changes, "d", input));
		changes.removeAll(find(changes, "e", input));
		changes.add(this);
	}
}
