package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.tagext.*;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class PageTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		String str = data.getAttributeString("name");
		if (str == null || str.equals(""))
			return new VariableInfo[0];

		VariableInfo info1 = new VariableInfo(str,
				"com.aggrepoint.winlet.site.domain.Page", true,
				VariableInfo.AT_END);
		VariableInfo[] info = { info1 };
		return info;
	}
}
