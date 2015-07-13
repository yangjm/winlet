package com.aggrepoint.servlet;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;

import com.aggrepoint.winlet.RespHeaderConst;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CORSResponseStream extends ServletOutputStream implements
		RespHeaderConst {
	protected HttpServletResponse response = null;
	protected ServletOutputStream output = null;

	public CORSResponseStream(HttpServletResponse response) throws IOException {
		super();
		this.response = response;
		this.output = response.getOutputStream();
	}

	Pattern P_COOKIE = Pattern.compile("JSESSIONID=([^;]+);");

	@Override
	public void close() throws IOException {
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		for (String header : new String[] { HEADER_UPDATE, HEADER_TITLE,
				HEADER_DIALOG, HEADER_REDIRECT, HEADER_CACHE, HEADER_MSG }) {
			String value = response.getHeader(header);
			if (value != null && !value.trim().equals("")) {
				node.put(header, value);
			}
		}

		try {
			Matcher m = P_COOKIE.matcher(response.getHeader("Set-Cookie"));
			if (m.find()) {
				node.put("X-Winlet-Session-ID", m.group(1));
			}
		} catch (Exception e) {

		}

		output.write(("<div id=\"winlet_header\" style=\"display:none\">"
				+ node.toString() + "</div>").getBytes());
		output.flush();
		output.close();
	}

	@Override
	public void flush() throws IOException {
		output.flush();
	}

	@Override
	public void write(int b) throws IOException {
		output.write((byte) b);
	}

	@Override
	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	@Override
	public void write(byte b[], int off, int len) throws IOException {
		output.write(b, off, len);
	}

	@Override
	public boolean isReady() {
		return output.isReady();
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		output.setWriteListener(writeListener);
	}
}
