package com.aggrepoint.winlet.form;

import java.util.Vector;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ChangeUpdateList extends Change {
	Vector<SelectOption> list;

	public ChangeUpdateList(String input, Vector<SelectOption> list) {
		super(input, "l");
		this.list = list;
	}

	public Vector<SelectOption> getList() {
		return list;
	}

	public void setList(Vector<SelectOption> list) {
		this.list = list;
	}
}
