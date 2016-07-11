package com.aggrepoint.winlet.form;

import java.util.Collection;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class ChangeUpdateList extends Change {
	Collection<? extends SelectOption> list;

	public ChangeUpdateList(String input,
			Collection<? extends SelectOption> list) {
		super(input, "l");
		this.list = list;
	}

	public Collection<? extends SelectOption> getList() {
		return list;
	}

	public void setList(Collection<? extends SelectOption> list) {
		this.list = list;
	}
}
