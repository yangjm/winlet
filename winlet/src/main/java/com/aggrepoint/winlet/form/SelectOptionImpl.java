package com.aggrepoint.winlet.form;

import java.util.Collection;

public class SelectOptionImpl implements SelectOption {
	private String id;
	private String name;

	public SelectOptionImpl(String id, String name) {
		this.id = id;
		this.name = name;
	}

	@Override
	public int getType() {
		return 0;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLogo() {
		return null;
	}

	@Override
	public String getTips() {
		return null;
	}

	@Override
	public Collection<? extends SelectOption> getSubs() {
		return null;
	}

	@Override
	public void addSub(SelectOption o) {
	}

	@Override
	public SelectOption addSub(Collection<? extends SelectOption> c) {
		return null;
	}

}
