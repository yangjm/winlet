package com.aggrepoint.winlet.taglib;

import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.VariableInfo;

import com.aggrepoint.winlet.form.InputImpl;

/**
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class GetInputTei extends TagExtraInfo {
	public VariableInfo[] getVariableInfo(TagData data) {
		return new VariableInfo[] { new VariableInfo(
				data.getAttributeString("var"), InputImpl.getClassName(data
						.getAttributeString("type")), true,
				VariableInfo.AT_BEGIN) };
	}
}
