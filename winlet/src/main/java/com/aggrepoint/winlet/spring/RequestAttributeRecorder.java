package com.aggrepoint.winlet.spring;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

/**
 * Record request attributes set by action in order to pass them to window
 * 
 * @author Jim
 */
public class RequestAttributeRecorder extends WinletRequestWrapper {
	boolean recording = false;
	HashMap<String, Object> attrs = new HashMap<String, Object>();

	RequestAttributeRecorder(HttpServletRequest wrapped) {
		super(wrapped, null, null);
	}

	public void startRecord() {
		recording = true;
	}

	public HashMap<String, Object> getRecorded() {
		return attrs;
	}

	@Override
	public void setAttribute(String name, Object o) {
		if (recording && name.indexOf("org.springframework.") == -1)
			attrs.put(name, o);
		super.setAttribute(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		if (recording)
			attrs.remove(name);
		super.removeAttribute(name);
	}
}
