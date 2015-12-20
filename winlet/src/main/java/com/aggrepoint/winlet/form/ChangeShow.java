package com.aggrepoint.winlet.form;

import java.util.Vector;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ChangeShow extends Change {
	public ChangeShow(String input) {
		super(input, "s");
	}

	public void addTo(Vector<Change> changes) {
		changes.removeAll(find(changes, "s", input));
		changes.removeAll(find(changes, "h", input));
		changes.add(this);
	}
}
