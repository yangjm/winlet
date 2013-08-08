package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.tagext.*;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PathTei extends TagExtraInfo {
	public static int m_iIdx = 0;

	public VariableInfo[] getVariableInfo(TagData data) {
		String str = data.getAttributeString("name");
		if (str == null || str.equals("")) {
			str = "AE_ANONYMOUS_PATH_PAGE_" + Integer.toString(m_iIdx++);
		}

		VariableInfo info1 =
			new VariableInfo(
				str,
				"com.aggrepoint.winlet.site.domain.Page",
				true,
				VariableInfo.NESTED);
		VariableInfo[] info = { info1 };
		return info;
	}
}
