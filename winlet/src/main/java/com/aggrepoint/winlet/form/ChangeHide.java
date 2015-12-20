package com.aggrepoint.winlet.form;

import java.util.Vector;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ChangeHide extends Change {
	public ChangeHide(String selector) {
		super(selector, "h");
	}

	public void addTo(Vector<Change> changes) {
		changes.removeAll(find(changes, "s", input));
		changes.removeAll(find(changes, "h", input));
		changes.add(this);
	}
}
