package com.aggrepoint.winlet.form;

import java.util.Map;
import java.util.Vector;

public interface InputSelect extends Input {
	public void updateOptions(Map<String, String> options);

	public Vector<SelectOption> getOptions();
}
