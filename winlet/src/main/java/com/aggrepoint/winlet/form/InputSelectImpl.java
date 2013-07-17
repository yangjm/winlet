package com.aggrepoint.winlet.form;

import java.util.Map;
import java.util.Vector;

import com.icebean.core.common.TypeCast;

public class InputSelectImpl extends InputImpl implements InputSelect {
	Vector<SelectOption> options;

	@Override
	public void setAttr(String name, Object value) {
		if (name.equals("options")) {
			if (value != null && options == null) { // 选项可能在运行过程中发生改变，因此只在第一次显示时根据JSP定义加载
				options = new Vector<SelectOption>();
				Map<String, String> map = TypeCast.cast(value);
				for (String key : map.keySet()) {
					options.add(new BasicSelectOption(SelectOption.TYPE_OPTION,
							key, map.get(key), null, null, null));
				}
			}
		} else
			super.setAttr(name, value);
	}

	@Override
	public void updateOptions(Map<String, String> options) {
		this.options = new Vector<SelectOption>();
		for (String key : options.keySet()) {
			this.options.add(new BasicSelectOption(SelectOption.TYPE_OPTION,
					key, options.get(key), null, null, null));
		}

		form.recordChange(new ChangeUpdateList(strName, this.options));
	}

	@Override
	public Vector<SelectOption> getOptions() {
		return options;
	}
}