package com.aggrepoint.utils.linkedtable;

import java.util.Hashtable;

public class PropObject {
	Hashtable<String, Object> m_htProps;

	public void setProp(String name, Object value) {
		if (m_htProps == null)
			m_htProps = new Hashtable<String, Object>();
		if (value == null)
			m_htProps.remove(name);
		else
			m_htProps.put(name, value);
	}

	public Object getProp(String name) {
		if (m_htProps == null)
			return null;
		return m_htProps.get(name);
	}

	public void clearProp() {
		m_htProps = null;
	}

	public Hashtable<String, Object> getProps() {
		return m_htProps;
	}
}
