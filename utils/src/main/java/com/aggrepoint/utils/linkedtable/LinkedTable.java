package com.aggrepoint.utils.linkedtable;

import java.util.Hashtable;
import java.util.Vector;

import com.aggrepoint.utils.TypeCast;

/**
 * @param <TColumn>
 *            Type of column key
 * @param <TRow>
 *            Type of row key
 * @param <TValue>
 *            Table content type
 */
public class LinkedTable<TColumn, TRow, TValue> extends PropObject {
	ColumnHead<TColumn, TRow, TValue> m_colHead;
	ColumnHead<TColumn, TRow, TValue> m_colTail;
	RowHead<TColumn, TRow, TValue> m_rowHead;
	RowHead<TColumn, TRow, TValue> m_rowTail;

	public TValue[][] toArray(TValue[][] arr) {
		if (m_rowHead == null || m_colHead == null)
			return null;

		int rc = getRowCount();
		int cc = getColumnCount();

		boolean bCreate = true;
		if (arr.length != rc)
			bCreate = true;
		else
			for (int i = 0; i < arr.length; i++)
				if (arr[i].length != cc) {
					bCreate = true;
					break;
				}

		if (bCreate)
			arr = TypeCast.cast(java.lang.reflect.Array.newInstance(arr
					.getClass().getComponentType().getComponentType(), rc, cc));

		RowHead<TColumn, TRow, TValue> row = m_rowHead;
		for (int r = 0; r < rc; r++) {
			Node<TColumn, TRow, TValue> node = row.m_firstNode;
			int c = 0;
			while (node != null) {
				arr[r][c++] = node.m_value;
				node = node.m_nextInRow;
			}
			row = row.m_nextHead;
		}

		return arr;
	}

	RowHead<TColumn, TRow, TValue> getRow(TRow row, boolean createIfNotExists) {
		if (m_rowHead == null) {
			if (!createIfNotExists)
				return null;

			// This is the first row
			// Create the row
			m_rowHead = m_rowTail = new RowHead<TColumn, TRow, TValue>(row);

			// { create nodes in the row
			ColumnHead<TColumn, TRow, TValue> col = m_colHead;
			Node<TColumn, TRow, TValue> nodePrev = null;
			while (col != null) {
				Node<TColumn, TRow, TValue> node = new Node<TColumn, TRow, TValue>(
						col, m_rowHead, null);
				col.m_firstNode = col.m_lastNode = node;
				if (nodePrev == null)
					m_rowHead.m_firstNode = node;
				else
					nodePrev.m_nextInRow = node;
				nodePrev = node;
				col = col.m_nextHead;
			}
			m_rowHead.m_lastNode = nodePrev;
			// }

			return m_rowHead;
		}

		RowHead<TColumn, TRow, TValue> r = m_rowHead.find(row);
		if (r != null)
			return r;

		if (!createIfNotExists)
			return null;

		// append a row
		r = new RowHead<TColumn, TRow, TValue>(row);
		m_rowTail.m_nextHead = r;
		m_rowTail = r;

		// { create nodes in the row
		ColumnHead<TColumn, TRow, TValue> col = m_colHead;
		Node<TColumn, TRow, TValue> nodePrev = null;
		while (col != null) {
			Node<TColumn, TRow, TValue> node = new Node<TColumn, TRow, TValue>(
					col, r, null);
			col.m_lastNode.m_nextInColumn = node;
			col.m_lastNode = node;
			if (nodePrev == null)
				r.m_firstNode = node;
			else
				nodePrev.m_nextInRow = node;
			nodePrev = node;
			col = col.m_nextHead;
		}
		r.m_lastNode = nodePrev;
		// }

		return r;
	}

	ColumnHead<TColumn, TRow, TValue> getColumn(TColumn column,
			boolean createIfNotExists) {
		if (m_colHead == null) {
			if (!createIfNotExists)
				return null;

			// This is the first column
			// Create the column
			m_colHead = m_colTail = new ColumnHead<TColumn, TRow, TValue>(
					column);

			// { create nodes in the column
			RowHead<TColumn, TRow, TValue> row = m_rowHead;
			Node<TColumn, TRow, TValue> nodePrev = null;
			while (row != null) {
				Node<TColumn, TRow, TValue> node = new Node<TColumn, TRow, TValue>(
						m_colHead, row, null);
				row.m_firstNode = row.m_lastNode = node;
				if (nodePrev == null)
					m_colHead.m_firstNode = node;
				else
					nodePrev.m_nextInColumn = node;
				nodePrev = node;
				row = row.m_nextHead;
			}
			m_colHead.m_lastNode = nodePrev;
			// }

			return m_colHead;
		}

		ColumnHead<TColumn, TRow, TValue> col = m_colHead.find(column);
		if (col != null)
			return col;

		if (!createIfNotExists)
			return null;

		// append a column
		col = new ColumnHead<TColumn, TRow, TValue>(column);
		m_colTail.m_nextHead = col;
		m_colTail = col;

		// { create nodes in the column
		RowHead<TColumn, TRow, TValue> row = m_rowHead;
		Node<TColumn, TRow, TValue> nodePrev = null;
		while (row != null) {
			Node<TColumn, TRow, TValue> node = new Node<TColumn, TRow, TValue>(
					col, row, null);
			row.m_lastNode.m_nextInRow = node;
			row.m_lastNode = node;
			if (nodePrev == null)
				col.m_firstNode = node;
			else
				nodePrev.m_nextInColumn = node;
			nodePrev = node;
			row = row.m_nextHead;
		}
		col.m_lastNode = nodePrev;
		// }

		return col;
	}

	public ColumnHead<TColumn, TRow, TValue> addColumn(TColumn col) {
		return getColumn(col, true);
	}

	public RowHead<TColumn, TRow, TValue> addRow(TRow row) {
		return getRow(row, true);
	}

	public void insertColumn(TColumn col, int posi) {
		ColumnHead<TColumn, TRow, TValue> newCol = new ColumnHead<TColumn, TRow, TValue>(
				col);

		if (posi == 0) {
			newCol.m_nextHead = m_colHead;
			m_colHead = newCol;
			if (m_colTail == null)
				m_colTail = newCol;

			// create nodes in the column
			RowHead<TColumn, TRow, TValue> row = m_rowHead;
			Node<TColumn, TRow, TValue> nodePrev = null;
			while (row != null) {
				Node<TColumn, TRow, TValue> node = new Node<TColumn, TRow, TValue>(
						newCol, row, null);
				node.m_nextInRow = row.m_firstNode;
				row.m_firstNode = node;
				if (row.m_lastNode == null)
					row.m_lastNode = node;
				if (nodePrev == null)
					newCol.m_firstNode = node;
				else
					nodePrev.m_nextInColumn = node;
				nodePrev = node;
				row = row.m_nextHead;
			}
			newCol.m_lastNode = nodePrev;

			return;
		}

		ColumnHead<TColumn, TRow, TValue> prev = m_colHead;
		for (int i = 0; i < posi - 1 && prev != null; i++)
			prev = prev.m_nextHead;
		if (prev == null)
			return;

		newCol.m_nextHead = prev.m_nextHead;
		prev.m_nextHead = newCol;
		if (newCol.m_nextHead == null)
			m_colTail = newCol;

		// create nodes in the column
		RowHead<TColumn, TRow, TValue> row = m_rowHead;
		Node<TColumn, TRow, TValue> nodePrev = null;
		while (row != null) {
			Node<TColumn, TRow, TValue> pn = row.m_firstNode;
			for (int i = 0; i < posi - 1 && pn != null; i++)
				pn = pn.m_nextInRow;

			Node<TColumn, TRow, TValue> node = new Node<TColumn, TRow, TValue>(
					newCol, row, null);
			node.m_nextInRow = pn.m_nextInRow;
			pn.m_nextInRow = node;
			if (node.m_nextInRow == null)
				row.m_lastNode = node;
			if (nodePrev == null)
				newCol.m_firstNode = node;
			else
				nodePrev.m_nextInColumn = node;
			nodePrev = node;
			row = row.m_nextHead;
		}
		newCol.m_lastNode = nodePrev;
	}

	public void set(TColumn column, TRow row, TValue value) {
		ColumnHead<TColumn, TRow, TValue> col = getColumn(column, true);
		RowHead<TColumn, TRow, TValue> r = getRow(row, true);

		Node<TColumn, TRow, TValue> node = col.m_firstNode;
		while (node != null) {
			if (node.m_row == r) {
				node.m_value = value;
				return;
			}
			node = node.m_nextInColumn;
		}
	}

	Object add(TValue a, TValue b) {
		if (a instanceof Double)
			return ((Double) a).doubleValue() + ((Double) b).doubleValue();
		else if (a instanceof Float)
			return ((Float) a).doubleValue() + ((Float) b).doubleValue();
		else if (a instanceof Long)
			return ((Long) a).doubleValue() + ((Long) b).doubleValue();
		else if (a instanceof Integer)
			return ((Integer) a).doubleValue() + ((Integer) b).doubleValue();
		else if (a instanceof Short)
			return ((Short) a).doubleValue() + ((Short) b).doubleValue();
		return b;
	}

	@SuppressWarnings("unchecked")
	public void add(TColumn column, TRow row, TValue value) {
		ColumnHead<TColumn, TRow, TValue> col = getColumn(column, true);
		RowHead<TColumn, TRow, TValue> r = getRow(row, true);

		Node<TColumn, TRow, TValue> node = col.m_firstNode;
		while (node != null) {
			if (node.m_row == r) {
				node.m_value = (TValue) add(node.m_value, value);
				return;
			}
			node = node.m_nextInColumn;
		}
	}

	public TValue get(TColumn column, TRow row) {
		if (m_colHead == null || m_rowHead == null)
			return null;

		ColumnHead<TColumn, TRow, TValue> col = m_colHead.find(column);
		RowHead<TColumn, TRow, TValue> r = m_rowHead.find(row);
		if (col == null || r == null)
			return null;

		Node<TColumn, TRow, TValue> node = col.m_firstNode;
		while (node != null) {
			if (node.m_row == r) {
				return node.m_value;
			}
			node = node.m_nextInColumn;
		}

		return null;
	}

	public TColumn getFirstColumn() {
		if (m_colHead == null)
			return null;
		return m_colHead.m_value;
	}

	public TRow getFirstRow() {
		if (m_rowHead == null)
			return null;
		return m_rowHead.m_value;
	}

	public Vector<TColumn> getColumns() {
		if (m_colHead == null)
			return null;

		ColumnHead<TColumn, TRow, TValue> col = m_colHead;
		while (col != null) {
			col = col.m_nextHead;
		}

		Vector<TColumn> cols = new Vector<TColumn>();

		col = m_colHead;
		while (col != null) {
			cols.add(col.m_value);
			col = col.m_nextHead;
		}

		return cols;
	}

	public Vector<Hashtable<String, Object>> getColumnProps() {
		if (m_colHead == null)
			return null;

		ColumnHead<TColumn, TRow, TValue> col = m_colHead;
		while (col != null) {
			col = col.m_nextHead;
		}

		Vector<Hashtable<String, Object>> cols = new Vector<Hashtable<String, Object>>();

		col = m_colHead;
		while (col != null) {
			cols.add(col.getProps());
			col = col.m_nextHead;
		}

		return cols;
	}

	public Vector<TRow> getRows() {
		if (m_rowHead == null)
			return null;

		RowHead<TColumn, TRow, TValue> row = m_rowHead;
		while (row != null) {
			row = row.m_nextHead;
		}

		Vector<TRow> rows = new Vector<TRow>();

		row = m_rowHead;
		while (row != null) {
			rows.add(row.m_value);
			row = row.m_nextHead;
		}

		return rows;
	}

	public Vector<Hashtable<String, Object>> getRowProps() {
		if (m_rowHead == null)
			return null;

		RowHead<TColumn, TRow, TValue> row = m_rowHead;
		while (row != null) {
			row = row.m_nextHead;
		}

		Vector<Hashtable<String, Object>> rows = new Vector<Hashtable<String, Object>>();

		row = m_rowHead;
		while (row != null) {
			rows.add(row.getProps());
			row = row.m_nextHead;
		}

		return rows;
	}

	public int getColumnCount() {
		if (m_colHead == null)
			return 0;
		int c = 0;
		ColumnHead<TColumn, TRow, TValue> col = m_colHead;
		while (col != null) {
			c++;
			col = col.m_nextHead;
		}
		return c;
	}

	public int getRowCount() {
		if (m_rowHead == null)
			return 0;
		int c = 0;
		RowHead<TColumn, TRow, TValue> row = m_rowHead;
		while (row != null) {
			c++;
			row = row.m_nextHead;
		}
		return c;
	}

	int findRowPosi(TRow row) {
		if (row == null)
			return -1;

		RowHead<TColumn, TRow, TValue> r = m_rowHead;
		int posi = 0;
		while (r != null) {
			if (r.m_value.equals(row))
				return posi;
			posi++;
			r = r.m_nextHead;
		}
		return -1;
	}

	RowHead<TColumn, TRow, TValue> getRowHead(int posi) {
		if (posi < 0)
			return null;

		RowHead<TColumn, TRow, TValue> r = m_rowHead;
		while (r != null) {
			if (posi == 0)
				return r;
			posi--;
			r = r.m_nextHead;
		}
		return null;
	}

	public TRow getRow(int posi) {
		RowHead<TColumn, TRow, TValue> head = getRowHead(posi);
		if (head == null)
			return null;
		return head.m_value;
	}

	int findColumnPosi(TColumn column) {
		if (column == null)
			return -1;

		ColumnHead<TColumn, TRow, TValue> col = m_colHead;
		int posi = 0;
		while (col != null) {
			if (col.m_value.equals(column))
				return posi;
			posi++;
			col = col.m_nextHead;
		}
		return -1;
	}

	ColumnHead<TColumn, TRow, TValue> getColumnHead(int posi) {
		if (posi < 0)
			return null;

		ColumnHead<TColumn, TRow, TValue> col = m_colHead;
		while (col != null) {
			if (posi == 0)
				return col;
			posi--;
			col = col.m_nextHead;
		}
		return null;
	}

	public TColumn getColumn(int posi) {
		ColumnHead<TColumn, TRow, TValue> head = getColumnHead(posi);
		if (head == null)
			return null;
		return head.m_value;
	}

	public void deleteColumn(TColumn column) {
		int posi = findColumnPosi(column);
		if (posi == -1)
			return;
		if (posi == 0) {
			if (m_colTail == m_colHead)
				m_colHead = m_colTail = null;
			else
				m_colHead = m_colHead.m_nextHead;

			RowHead<TColumn, TRow, TValue> row = m_rowHead;
			while (row != null) {
				if (m_colHead == null)
					row.m_firstNode = row.m_lastNode = null;
				else
					row.m_firstNode = row.m_firstNode.m_nextInRow;

				row = row.m_nextHead;
			}
		} else {
			ColumnHead<TColumn, TRow, TValue> col = m_colHead;
			posi--;
			for (int i = 0; i < posi; i++)
				col = col.m_nextHead;
			if (m_colTail == col.m_nextHead)
				m_colTail = col;
			col.m_nextHead = col.m_nextHead.m_nextHead;

			RowHead<TColumn, TRow, TValue> row = m_rowHead;
			while (row != null) {
				Node<TColumn, TRow, TValue> node = row.m_firstNode;
				for (int i = 0; i < posi; i++)
					node = node.m_nextInRow;
				if (row.m_lastNode == node.m_nextInRow)
					row.m_lastNode = node;
				node.m_nextInRow = node.m_nextInRow.m_nextInRow;

				row = row.m_nextHead;
			}
		}
	}

	public void deleteRow(TRow row) {
		int posi = findRowPosi(row);
		if (posi == -1)
			return;
		if (posi == 0) {
			if (m_rowTail == m_rowHead)
				m_rowHead = m_rowTail = null;
			else
				m_rowHead = m_rowHead.m_nextHead;

			ColumnHead<TColumn, TRow, TValue> col = m_colHead;
			while (col != null) {
				if (m_rowHead == null)
					col.m_firstNode = col.m_lastNode = null;
				else
					col.m_firstNode = col.m_firstNode.m_nextInColumn;

				col = col.m_nextHead;
			}
		} else {
			RowHead<TColumn, TRow, TValue> r = m_rowHead;
			posi--;
			for (int i = 0; i < posi; i++)
				r = r.m_nextHead;
			if (m_rowTail == r.m_nextHead)
				m_rowTail = r;
			r.m_nextHead = r.m_nextHead.m_nextHead;

			ColumnHead<TColumn, TRow, TValue> col = m_colHead;
			while (col != null) {
				Node<TColumn, TRow, TValue> node = col.m_firstNode;
				for (int i = 0; i < posi; i++)
					node = node.m_nextInColumn;
				if (col.m_lastNode == node.m_nextInColumn)
					col.m_lastNode = node;
				node.m_nextInColumn = node.m_nextInColumn.m_nextInColumn;

				col = col.m_nextHead;
			}
		}
	}

	TValue[] getValuesInColumn(ColumnHead<TColumn, TRow, TValue> col,
			TValue[] vals) {
		if (col == null)
			return null;

		int count = getRowCount();
		if (vals.length != count)
			vals = TypeCast.cast(java.lang.reflect.Array.newInstance(vals
					.getClass().getComponentType(), count));

		Node<TColumn, TRow, TValue> node = col.m_firstNode;
		int c = 0;
		while (node != null) {
			vals[c++] = node.m_value;
			node = node.m_nextInColumn;
		}

		return vals;
	}

	public TValue[] getValuesInColumn(TColumn column, TValue[] vals) {
		return getValuesInColumn(getColumn(column, false), vals);
	}

	public TValue[] getValuesInColumn(int posi, TValue[] vals) {
		return getValuesInColumn(getColumnHead(posi), vals);
	}

	TValue[] getValuesInRow(RowHead<TColumn, TRow, TValue> r, TValue[] vals) {
		if (r == null)
			return null;

		int count = getColumnCount();
		if (vals.length != count)
			vals = TypeCast.cast(java.lang.reflect.Array.newInstance(vals
					.getClass().getComponentType(), count));

		Node<TColumn, TRow, TValue> node = r.m_firstNode;
		int c = 0;
		while (node != null) {
			vals[c++] = node.m_value;
			node = node.m_nextInRow;
		}

		return vals;
	}

	public TValue[] getValuesInRow(TRow row, TValue[] vals) {
		return getValuesInRow(getRow(row, false), vals);
	}

	public TValue[] getValuesInRow(int posi, TValue[] vals) {
		return getValuesInRow(getRowHead(posi), vals);
	}

	@SuppressWarnings("unchecked")
	public void sortColumns(TColumn[] order) {
		if (order == null || m_colHead == null)
			return;

		// { calculate the order by position id
		int[] orders = new int[getColumnCount()];
		TColumn[] cols = TypeCast.cast(new Object[orders.length]);

		int i = 0;
		ColumnHead<TColumn, TRow, TValue> col = m_colHead;
		while (col != null) {
			cols[i++] = col.m_value;
			col = col.m_nextHead;
		}

		i = 0;
		for (TColumn c : order) {
			for (int j = 0; j < cols.length; j++) {
				if (cols[j] == null)
					continue;
				if (cols[j].equals(c)) {
					orders[i++] = j;
					cols[j] = null;
					continue;
				}
			}
		}
		for (int j = 0; j < cols.length; j++)
			if (cols[j] != null)
				orders[i++] = j;
		// }

		// {sort header
		Object[] heads = new Object[orders.length];
		Object[] newHeads = new Object[orders.length];

		i = 0;
		col = m_colHead;
		while (col != null) {
			heads[i++] = col;
			col = col.m_nextHead;
		}

		for (int j = 0; j < orders.length; j++)
			newHeads[j] = heads[orders[j]];

		m_colHead = (ColumnHead<TColumn, TRow, TValue>) newHeads[0];
		m_colTail = (ColumnHead<TColumn, TRow, TValue>) newHeads[orders.length - 1];
		m_colTail.m_nextHead = null;

		for (int j = 0; j < orders.length - 1; j++)
			((ColumnHead<TColumn, TRow, TValue>) newHeads[j]).m_nextHead = TypeCast
					.cast(newHeads[j + 1]);
		// }

		// { sort data
		Object[] nodes = new Object[orders.length];
		Object[] newNodes = new Object[orders.length];

		RowHead<TColumn, TRow, TValue> row = m_rowHead;
		while (row != null) {
			Node<TColumn, TRow, TValue> node = row.m_firstNode;
			i = 0;
			while (node != null) {
				nodes[i++] = node;
				node = node.m_nextInRow;
			}

			for (int j = 0; j < orders.length; j++)
				newNodes[j] = nodes[orders[j]];

			row.m_firstNode = (Node<TColumn, TRow, TValue>) newNodes[0];
			row.m_lastNode = (Node<TColumn, TRow, TValue>) newNodes[orders.length - 1];
			row.m_lastNode.m_nextInRow = null;

			for (int j = 0; j < orders.length - 1; j++)
				((Node<TColumn, TRow, TValue>) newNodes[j]).m_nextInRow = (Node<TColumn, TRow, TValue>) newNodes[j + 1];

			row = row.m_nextHead;
		}
		// }
	}

	@SuppressWarnings("unchecked")
	public void sortRows(TRow[] order) {
		if (order == null || m_rowHead == null)
			return;

		// { calculate the order by position id
		int[] orders = new int[getRowCount()];
		TRow[] rows = TypeCast.cast(new Object[orders.length]);

		int i = 0;
		RowHead<TColumn, TRow, TValue> row = m_rowHead;
		while (row != null) {
			rows[i++] = row.m_value;
			row = row.m_nextHead;
		}

		i = 0;
		for (TRow r : order) {
			for (int j = 0; j < rows.length; j++) {
				if (rows[j] == null)
					continue;
				if (rows[j].equals(r)) {
					orders[i++] = j;
					rows[j] = null;
					continue;
				}
			}
		}
		for (int j = 0; j < rows.length; j++)
			if (rows[j] != null)
				orders[i++] = j;
		// }

		// {sort header
		Object[] heads = new Object[orders.length];
		Object[] newHeads = new Object[orders.length];

		i = 0;
		row = m_rowHead;
		while (row != null) {
			heads[i++] = row;
			row = row.m_nextHead;
		}

		for (int j = 0; j < orders.length; j++)
			newHeads[j] = heads[orders[j]];

		m_rowHead = (RowHead<TColumn, TRow, TValue>) newHeads[0];
		m_rowTail = (RowHead<TColumn, TRow, TValue>) newHeads[orders.length - 1];
		m_rowTail.m_nextHead = null;

		for (int j = 0; j < orders.length - 1; j++)
			((RowHead<TColumn, TRow, TValue>) newHeads[j]).m_nextHead = TypeCast
					.cast(newHeads[j + 1]);
		// }

		// { sort data
		Object[] nodes = new Object[orders.length];
		Object[] newNodes = new Object[orders.length];

		ColumnHead<TColumn, TRow, TValue> col = m_colHead;
		while (col != null) {
			Node<TColumn, TRow, TValue> node = col.m_firstNode;
			i = 0;
			while (node != null) {
				nodes[i++] = node;
				node = node.m_nextInColumn;
			}

			for (int j = 0; j < orders.length; j++)
				newNodes[j] = nodes[orders[j]];

			col.m_firstNode = (Node<TColumn, TRow, TValue>) newNodes[0];
			col.m_lastNode = (Node<TColumn, TRow, TValue>) newNodes[orders.length - 1];
			col.m_lastNode.m_nextInColumn = null;

			for (int j = 0; j < orders.length - 1; j++)
				((Node<TColumn, TRow, TValue>) newNodes[j]).m_nextInColumn = (Node<TColumn, TRow, TValue>) newNodes[j + 1];

			col = col.m_nextHead;
		}
		// }
	}

	public void mergeColumn(TColumn from, TColumn to, INodeMerger merger) {
		if (m_colHead == null)
			return;

		ColumnHead<TColumn, TRow, TValue> colFrom = m_colHead.find(from);
		ColumnHead<TColumn, TRow, TValue> colTo = m_colHead.find(to);

		if (colFrom == null)
			return;

		if (colTo == null) {
			colFrom.m_value = to;
			return;
		}

		Node<TColumn, TRow, TValue> ndFrom = colFrom.m_firstNode;
		Node<TColumn, TRow, TValue> ndTo = colTo.m_firstNode;
		while (ndFrom != null && ndTo != null) {
			ndTo.m_value = merger.merge(ndFrom.m_value, ndTo.m_value);
			ndFrom = ndFrom.m_nextInColumn;
			ndTo = ndTo.m_nextInColumn;
		}

		deleteColumn(from);
	}

	public void mergeColumn(TColumn[] froms, TColumn to, INodeMerger merger) {
		if (m_colHead == null)
			return;
		for (int i = 0; i < froms.length; i++)
			mergeColumn(froms[i], to, merger);
	}

	public void mergeRow(TRow from, TRow to, INodeMerger merger) {
		if (m_rowHead == null)
			return;

		RowHead<TColumn, TRow, TValue> rowFrom = m_rowHead.find(from);
		RowHead<TColumn, TRow, TValue> rowTo = m_rowHead.find(to);

		if (rowFrom == rowTo)
			return;

		if (rowFrom == null)
			return;

		if (rowTo == null) {
			rowFrom.m_value = to;
			return;
		}

		Node<TColumn, TRow, TValue> ndFrom = rowFrom.m_firstNode;
		Node<TColumn, TRow, TValue> ndTo = rowTo.m_firstNode;
		while (ndFrom != null && ndTo != null) {
			ndTo.m_value = merger.merge(ndFrom.m_value, ndTo.m_value);
			ndFrom = ndFrom.m_nextInRow;
			ndTo = ndTo.m_nextInRow;
		}

		deleteRow(from);
	}

	public void mergeRow(TRow[] froms, TRow to, INodeMerger merger) {
		if (m_rowHead == null)
			return;
		for (int i = 0; i < froms.length; i++)
			mergeRow(froms[i], to, merger);
	}

	public void updateColumns(Hashtable<TColumn, TColumn> map) {
		if (m_colHead == null)
			return;

		ColumnHead<TColumn, TRow, TValue> col = m_colHead;
		while (col != null) {
			TColumn c = map.get(col.m_value);
			if (c != null)
				col.m_value = c;
			col = col.m_nextHead;
		}
	}

	public void updateRows(Hashtable<TRow, TRow> map) {
		if (m_rowHead == null)
			return;

		RowHead<TColumn, TRow, TValue> row = m_rowHead;
		while (row != null) {
			TRow r = map.get(row.m_value);
			if (r != null)
				row.m_value = r;
			row = row.m_nextHead;
		}
	}

	public boolean setColumnProp(TColumn col, String name, Object value) {
		ColumnHead<TColumn, TRow, TValue> head = getColumn(col, false);
		if (head == null)
			return false;
		head.setProp(name, value);
		return true;
	}

	public boolean setColumnProp(int col, String name, Object value) {
		ColumnHead<TColumn, TRow, TValue> head = getColumnHead(col);
		if (head == null)
			return false;
		head.setProp(name, value);
		return true;
	}

	public void copyColumnToProp(String name) {
		ColumnHead<TColumn, TRow, TValue> col = m_colHead;
		while (col != null) {
			col.setProp(name, col.m_value);
			col = col.m_nextHead;
		}
	}

	public Object getColumnProp(TColumn col, String name) {
		ColumnHead<TColumn, TRow, TValue> c = getColumn(col, false);
		if (c == null)
			return null;
		if (name == null)
			return c.getProps();
		else
			return c.getProp(name);
	}

	public Object getColumnProp(int posi, String name) {
		ColumnHead<TColumn, TRow, TValue> c = getColumnHead(posi);
		if (c == null)
			return null;
		if (name == null)
			return c.getProps();
		else
			return c.getProp(name);
	}

	public boolean setRowProp(TRow row, String name, Object value) {
		RowHead<TColumn, TRow, TValue> head = getRow(row, false);
		if (head == null)
			return false;
		head.setProp(name, value);
		return true;
	}

	public boolean setRowProp(int row, String name, Object value) {
		RowHead<TColumn, TRow, TValue> head = getRowHead(row);
		if (head == null)
			return false;
		head.setProp(name, value);
		return true;
	}

	public void copyRowToProp(String name) {
		RowHead<TColumn, TRow, TValue> r = m_rowHead;
		while (r != null) {
			r.setProp(name, r.m_value);
			r = r.m_nextHead;
		}
	}

	public Object getRowProp(TRow row, String name) {
		RowHead<TColumn, TRow, TValue> head = getRow(row, false);
		if (head == null)
			return false;
		if (name == null)
			return head.getProps();
		else
			return head.getProp(name);
	}

	public Object getRowProp(int posi, String name) {
		RowHead<TColumn, TRow, TValue> head = getRowHead(posi);
		if (head == null)
			return false;
		if (name == null)
			return head.getProps();
		else
			return head.getProp(name);
	}
}
