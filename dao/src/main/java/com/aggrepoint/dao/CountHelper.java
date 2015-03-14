package com.aggrepoint.dao;

import java.util.Stack;

public class CountHelper {
	static final int STATE_SELECT_START = 0;
	static final int STATE_SELECT = 1;
	static final int STATE_FROM = 2;
	static final int STATE_ORDER = 3;
	static final int STATE_QUOTE = 10;
	static final int STATE_PARENTESIS = 11;

	static final char[] SELECT = "SELECT ".toCharArray();
	static final char[] FROM = "FROM ".toCharArray();
	static final char[] ORDER_BY = "ORDER BY ".toCharArray();
	static final char[] DOUBLE_QUOTE = "''".toCharArray();

	private String select;
	private String from;
	private String order;

	private int match(char[] chars, int st, char[] match) {
		for (int i = 0; i < match.length; i++) {
			if (chars.length <= st + i)
				return -1;
			if (chars[st + i] != match[i])
				return -1;
			if (match[i] == ' ')
				while (chars.length <= st + i + 1 && chars[st + i + 1] == ' ')
					st++;
		}

		return st + match.length - 1;
	}

	public CountHelper(String sql) {
		Stack<Integer> state = new Stack<Integer>();
		state.push(STATE_SELECT_START);

		sql = sql.trim();
		char[] chars = sql.toUpperCase().toCharArray();

		int selectSt, selectEd, fromSt, fromEd, orderSt;
		selectSt = selectEd = fromSt = fromEd = orderSt = -1;

		for (int i = 0; i < chars.length; i++) {
			int posi;

			switch (state.peek()) {
			case STATE_SELECT_START:
				posi = match(chars, 0, SELECT);
				if (posi > 0)
					i = posi;
				else
					i--;
				state.pop();
				state.push(STATE_SELECT);
				selectSt = i + 1;
				break;
			case STATE_SELECT:
				switch (chars[i]) {
				case '(':
					state.push(STATE_PARENTESIS);
					break;
				case '\'':
					state.push(STATE_QUOTE);
					break;
				case 'F':
					if (i == 0 || i > 0 && chars[i - 1] == ' ') {
						posi = match(chars, i, FROM);
						if (posi > 0) {
							selectEd = i - 1;
							state.pop();
							state.push(STATE_FROM);
							i = posi;
							fromSt = i + 1;
						}
					}
					break;
				}
				break;
			case STATE_FROM:
				switch (chars[i]) {
				case '(':
					state.push(STATE_PARENTESIS);
					break;
				case '\'':
					state.push(STATE_QUOTE);
					break;
				case 'O':
					posi = match(chars, i, ORDER_BY);
					if (posi > 0) {
						fromEd = i - 1;
						state.pop();
						state.push(STATE_ORDER);
						i = posi;
						orderSt = i + 1;
					}
					break;
				}
				break;
			case STATE_QUOTE:
				switch (chars[i]) {
				case '\'':
					posi = match(chars, i, DOUBLE_QUOTE);
					if (posi > 0)
						i = posi;
					else
						state.pop();
					break;
				}
				break;
			case STATE_PARENTESIS:
				switch (chars[i]) {
				case '(':
					state.push(STATE_PARENTESIS);
					break;
				case '\'':
					state.push(STATE_QUOTE);
					break;
				case ')':
					state.pop();
					break;
				}
				break;
			}

			if (state.peek() == STATE_ORDER)
				break;
		}

		if (state.peek() == STATE_FROM)
			fromEd = chars.length;

		if (fromEd == -1)
			return;

		if (selectEd == -1) // hql可能没有select
			select = "";
		else
			select = sql.substring(selectSt, selectEd);
		from = sql.substring(fromSt, fromEd);

		if (orderSt > 0)
			order = sql.substring(orderSt);
	}

	public String getSelect() {
		return select;
	}

	public String getFrom() {
		return from;
	}

	public String getOrder() {
		return order;
	}
}
