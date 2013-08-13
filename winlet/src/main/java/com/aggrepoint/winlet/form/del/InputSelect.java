package com.aggrepoint.winlet.form.del;

import java.util.Map;
import java.util.Vector;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public interface InputSelect extends Input {
	public void updateOptions(Map<String, String> options);

	public Vector<SelectOption> getOptions();
}
