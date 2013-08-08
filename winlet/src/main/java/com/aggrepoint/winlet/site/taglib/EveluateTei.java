package com.aggrepoint.winlet.site.taglib;

import javax.servlet.jsp.tagext.*;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class EveluateTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		String str = data.getAttributeString("name");

		VariableInfo info1 =
			new VariableInfo(
				str,
				"java.lang.Boolean",
				true,
				VariableInfo.AT_END);
		VariableInfo[] info = { info1 };
		return info;
	}
}
