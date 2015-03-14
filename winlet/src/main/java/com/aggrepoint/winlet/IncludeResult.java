package com.aggrepoint.winlet;

public class IncludeResult {
	private WindowInstance childWindow;
	private String response;

	IncludeResult(WindowInstance childWindow, String response) {
		this.childWindow = childWindow;
		this.response = response;
	}

	public WindowInstance getChildWindow() {
		return childWindow;
	}

	public String getResponse() {
		return response;
	}
}
