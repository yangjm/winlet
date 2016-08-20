package com.aggrepoint.utils.linkedtable;

public class RowHead<TColumn, TRow, TValue> extends PropObject{
	RowHead<TColumn, TRow, TValue> m_nextHead;
	Node<TColumn, TRow, TValue> m_firstNode;
	Node<TColumn, TRow, TValue> m_lastNode;
	TRow m_value;

	public RowHead(TRow header) {
		m_value = header;
	}

	public RowHead<TColumn, TRow, TValue> find(TRow header) {
		if (m_value.equals(header))
			return this;
		if (m_nextHead == null)
			return null;
		return m_nextHead.find(header);
	}
}
