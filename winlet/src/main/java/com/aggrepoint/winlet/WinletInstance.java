package com.aggrepoint.winlet;

import com.aggrepoint.winlet.spring.def.ViewDef;

public class WinletInstance {
	String iid;
	ViewInstance viewInstance;

	public WinletInstance(String iid, ViewDef def, Object winlet) {
		this.iid = iid;
		viewInstance = new ViewInstance(this, iid, winlet, def, null);
	}

	public ViewInstance findView(String vid) {
		return viewInstance.find(vid);
	}
}
