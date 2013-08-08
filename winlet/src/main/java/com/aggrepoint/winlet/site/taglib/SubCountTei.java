package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.tagext.*;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class SubCountTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		String str = data.getAttributeString("name");

		if (str != null && !str.equals("")) {
			VariableInfo info1 =
				new VariableInfo(
					str,
					"java.lang.Integer",
					true,
					VariableInfo.AT_END);
			VariableInfo[] info = { info1 };
			return info;
		}

		VariableInfo[] info = {
		};
		return info;
	}
}
