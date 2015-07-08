package com.aggrepoint.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CORSResponseWrapper extends HttpServletResponseWrapper {
	protected HttpServletResponse origResponse = null;
	protected ServletOutputStream stream = null;
	protected PrintWriter writer = null;

	public CORSResponseWrapper(HttpServletResponse response) {
		super(response);
		origResponse = response;
	}

	public ServletOutputStream createOutputStream() throws IOException {
		return new CORSResponseStream(origResponse);
	}

	@Override
	public void flushBuffer() throws IOException {
		stream.flush();
	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (stream != null)
			return stream;

		if (writer != null) {
			throw new IllegalStateException(
					"getWriter() has already been called!");
		}

		stream = createOutputStream();
		return (stream);
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (writer != null) {
			return (writer);
		}

		if (stream != null) {
			throw new IllegalStateException(
					"getOutputStream() has already been called!");
		}

		stream = createOutputStream();
		writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
		return (writer);
	}

	@Override
	public void setContentLength(int length) {
	}
}
