package com.aggrepoint.utils.linkedtable;

public class Node<TColumn, TRow, TValue> {
	ColumnHead<TColumn, TRow, TValue> m_col;
	RowHead<TColumn, TRow, TValue> m_row;
	Node<TColumn, TRow, TValue> m_nextInRow;
	Node<TColumn, TRow, TValue> m_nextInColumn;
	TValue m_value;

	public Node(ColumnHead<TColumn, TRow, TValue> col,
			RowHead<TColumn, TRow, TValue> row, TValue val) {
		m_col = col;
		m_row = row;
		m_value = val;
	}
}
