package com.aggrepoint.winlet.jsp.site.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class EveluateTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		String str = data.getAttributeString("name");
		String type = data.getAttributeString("type");

		VariableInfo info1 = new VariableInfo(str,
				type.startsWith("data.") ? "java.lang.String"
						: "java.lang.Boolean", true, VariableInfo.AT_END);
		VariableInfo[] info = { info1 };
		return info;
	}
}
