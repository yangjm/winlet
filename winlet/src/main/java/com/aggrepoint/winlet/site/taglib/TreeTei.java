package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class TreeTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		String str = data.getAttributeString("name");
		if (str == null || str.equals(""))
			return new VariableInfo[0];

		VariableInfo info1 = new VariableInfo(str,
				"com.aggrepoint.winlet.site.domain.Page", true,
				VariableInfo.NESTED);
		VariableInfo[] info = { info1 };
		return info;
	}
}
