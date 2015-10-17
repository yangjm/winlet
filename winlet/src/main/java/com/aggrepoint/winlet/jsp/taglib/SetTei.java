package com.aggrepoint.winlet.jsp.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class SetTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		return new VariableInfo[] { new VariableInfo(
				data.getAttributeString("var"), "java.lang.String", true,
				VariableInfo.AT_END) };
	}
}
