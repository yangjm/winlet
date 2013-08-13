package com.aggrepoint.winlet.form.del;

import java.util.Collection;
import java.util.Vector;

import com.icebean.core.common.StringUtils;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class BasicSelectOption implements SelectOption {
	String id;
	String logo;
	String name;
	Vector<SelectOption> subs;
	String tips;
	int type;

	public BasicSelectOption(SelectOption o) {
		this.type = o.getType();
		this.id = o.getId();
		this.logo = o.getLogo();
		this.name = o.getName();
		this.tips = o.getTips();
		if (o.getSubs() != null) {
			this.subs = new Vector<SelectOption>();
			this.subs.addAll(o.getSubs());
		}
	}

	public BasicSelectOption(int type, String id, String name, String logo,
			String tips, Collection<? extends SelectOption> subs) {
		this.type = type;
		this.id = id;
		this.name = name;
		this.logo = logo;
		this.tips = tips;
		if (subs != null) {
			this.subs = new Vector<SelectOption>();
			this.subs.addAll(subs);
		}
	}

	public String getId() {
		return id;
	}

	public String getLogo() {
		return logo;
	}

	public String getName() {
		return name;
	}

	public SelectOption getSub(String id) {
		if (subs == null)
			return null;
		for (SelectOption sub : subs)
			if (sub.getId().equals(id))
				return sub;

		return null;
	}

	public String getSubName(String id) {
		SelectOption sub = getSub(id);
		if (sub == null)
			return null;
		return sub.getName();
	}

	public Collection<? extends SelectOption> getSubs() {
		return subs;
	}

	public void addSub(SelectOption o) {
		if (subs == null)
			subs = new Vector<SelectOption>();
		subs.add(o);
	}

	public SelectOption addSub(Collection<? extends SelectOption> c) {
		if (subs == null)
			subs = new Vector<SelectOption>();
		subs.addAll(c);
		return this;
	}

	public String getTips() {
		return tips;
	}

	public int getType() {
		return type;
	}

	/**
	 * Flatten multilevel select list into single level, for populate basic
	 * select list
	 * 
	 * @param sos
	 * @param prefix
	 * @param nextLevelPrefix
	 * @return
	 */
	public static Collection<BasicSelectOption> flatten(
			Collection<? extends SelectOption> sos, String prefix) {
		Vector<BasicSelectOption> options = new Vector<BasicSelectOption>();
		if (sos != null)
			for (SelectOption so : sos) {
				if (so.getType() == TYPE_LABEL || so.getType() == TYPE_SEPERATE)
					continue;

				if (so.getType() == TYPE_OPTION) {
					BasicSelectOption bso = new BasicSelectOption(so);
					if (prefix != null && !prefix.equals(""))
						bso.name = prefix + " - " + bso.name;
					options.add(bso);
				}

				if (so.getSubs() != null)
					options.addAll(flatten(so.getSubs(),
							prefix != null && !prefix.equals("") ? prefix
									+ " - " + so.getName() : so.getName()));
			}

		return options;
	}

	public static String toJson(Collection<? extends SelectOption> sos) {
		boolean bFirst = true;
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		if (sos != null) {
			for (SelectOption o : sos) {
				if (bFirst)
					bFirst = false;
				else
					sb.append(", ");

				sb.append("{\"type\": \"");
				switch (o.getType()) {
				case (SelectOption.TYPE_LABEL):
					sb.append("label");
					break;
				case (SelectOption.TYPE_OPTION):
					sb.append("option");
					break;
				case (SelectOption.TYPE_SEPERATE):
					sb.append("sep");
					break;
				case (SelectOption.TYPE_SUB):
					sb.append("sub");
					break;
				}

				if (o.getId() != null) {
					sb.append("\", \"value\": \"");
					sb.append(StringUtils.toJson(o.getId()));
				}

				if (o.getLogo() != null) {
					sb.append("\", \"logo\": \"");
					sb.append(StringUtils.toJson(o.getLogo()));
				}

				sb.append("\", \"label\": \"");
				sb.append(StringUtils.toJson(o.getName()));

				if (o.getTips() != null) {
					sb.append("\", \"tip\": \"");
					sb.append(StringUtils.toJson(o.getTips()));
				}

				if (o.getSubs() != null) {
					sb.append("\", \"sub\": ");
					sb.append(toJson(o.getSubs()));
					sb.append("}");
				} else
					sb.append("\"}");
			}
		}
		sb.append("]");
		return sb.toString();
	}
}
