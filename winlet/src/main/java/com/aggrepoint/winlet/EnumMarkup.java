package com.aggrepoint.winlet;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public enum EnumMarkup {
	HTML(0, "html"), XHTML(2, "xhtml"), WML(1, "wml"), UNDEFINED(-1, "undef");

	int m_iId;
	String m_strName;
	static EnumMarkup[] validValues;
	static {
		validValues = new EnumMarkup[EnumMarkup.values().length - 1];
		int i = 0;
		for (EnumMarkup markup : EnumMarkup.values()) {
			if (markup == UNDEFINED)
				continue;
			validValues[i++] = markup;
		}
	}

	EnumMarkup(int id, String name) {
		m_iId = id;
		m_strName = name;
	}

	public static EnumMarkup[] getValidValues() {
		return validValues;
	}

	public String getName() {
		return m_strName;
	}

	public String getStrId() {
		return Integer.toString(m_iId);
	}

	public int getId() {
		return m_iId;
	}

	public static EnumMarkup fromName(String s) {
		if (s == null)
			return HTML;

		for (EnumMarkup wm : EnumMarkup.values()) {
			if (wm.getName().equalsIgnoreCase(s))
				return wm;
		}
		return HTML;
	}

	public static EnumMarkup fromId(int id) {
		for (EnumMarkup wm : EnumMarkup.values()) {
			if (wm.getId() == id)
				return wm;
		}
		return HTML;
	}

	public static EnumMarkup fromStrId(String id) {
		if (id == null)
			return HTML;

		for (EnumMarkup wm : EnumMarkup.values()) {
			if (wm.getStrId().equalsIgnoreCase(id))
				return wm;
		}
		return HTML;
	}

}
