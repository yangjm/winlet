package com.aggrepoint.utils.linkedtable;

public class ColumnHead<TColumn, TRow, TValue> extends PropObject {
	ColumnHead<TColumn, TRow, TValue> m_nextHead;
	Node<TColumn, TRow, TValue> m_firstNode;
	Node<TColumn, TRow, TValue> m_lastNode;
	TColumn m_value;

	public ColumnHead(TColumn header) {
		m_value = header;
	}

	public ColumnHead<TColumn, TRow, TValue> find(TColumn header) {
		if (m_value.equals(header))
			return this;
		if (m_nextHead == null)
			return null;
		return m_nextHead.find(header);
	}
}
