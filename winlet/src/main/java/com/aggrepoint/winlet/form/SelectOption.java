package com.aggrepoint.winlet.form;

import java.util.Collection;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface SelectOption {
	public static final int TYPE_OPTION = 0;
	public static final int TYPE_SUB = 1;
	public static final int TYPE_LABEL = 2;
	public static final int TYPE_SEPERATE = 3;

	public int getType();

	public String getId();

	public String getName();

	public String getLogo();

	public String getTips();

	public Collection<? extends SelectOption> getSubs();

	public void addSub(SelectOption o);

	public SelectOption addSub(Collection<? extends SelectOption> c);
}
