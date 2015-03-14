package com.aggrepoint.winlet.utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class BufferedResponseWrapper extends HttpServletResponseWrapper {
	protected BufferedResponseStream stream = null;
	protected PrintWriter writer = null;

	public BufferedResponseWrapper(HttpServletResponse response) {
		super(response);
	}

	public void setHeader(String name, String value) {
	}

	public void finishResponse() {
		try {
			if (writer != null) {
				writer.close();
			} else {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (IOException e) {
		}
	}

	public void flushBuffer() throws IOException {
		if (stream != null)
			stream.flush();
	}

	public ServletOutputStream getOutputStream() throws IOException {
		if (writer != null) {
			throw new IllegalStateException(
					"getWriter() has already been called!");
		}

		if (stream == null)
			stream = new BufferedResponseStream();
		return (stream);
	}

	public PrintWriter getWriter() throws IOException {
		if (writer != null) {
			return (writer);
		}

		if (stream != null) {
			throw new IllegalStateException(
					"getOutputStream() has already been called!");
		}

		stream = new BufferedResponseStream();
		writer = new PrintWriter(new OutputStreamWriter(stream, "UTF-8"));
		return (writer);
	}

	public void setContentLength(int length) {
	}

	public byte[] getBuffered() throws IOException {
		if (stream == null)
			return null;
		if (writer != null)
			writer.flush();
		return stream.getBuffered();
	}
}
