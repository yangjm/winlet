package com.aggrepoint.winlet;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WindowToUpdate {
	public boolean ensureVisible;
	public String winletName;
	public String windowName;

	private WindowToUpdate(String token) {
		if (token.startsWith("!")) {
			ensureVisible = true;
			token = token.substring(1);
		} else
			ensureVisible = false;
		int idx = token.lastIndexOf(".");
		if (idx == -1) {
			winletName = null;
			windowName = token;
		} else {
			winletName = token.substring(0, idx);
			windowName = token.substring(idx + 1);
		}
	}

	public static Vector<WindowToUpdate> parse(String str) {
		if (str == null || str.equals(""))
			return null;

		StringTokenizer st = new StringTokenizer(str, ", ");
		Vector<WindowToUpdate> vec = new Vector<WindowToUpdate>();

		while (st.hasMoreTokens())
			vec.add(new WindowToUpdate(st.nextToken()));

		return vec;
	}
}
