package com.aggrepoint.winlet.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class BufferedResponseStream extends ServletOutputStream {
	protected ByteArrayOutputStream baos = null;
	protected boolean closed = false;

	public BufferedResponseStream() throws IOException {
		super();
		closed = false;
		baos = new ByteArrayOutputStream();
	}

	public byte[] getBuffered() {
		return baos.toByteArray();
	}

	public void close() throws IOException {
		if (closed) {
			throw new IOException("This output stream has already been closed");
		}
		closed = true;
	}

	public void flush() throws IOException {
		if (closed) {
			throw new IOException("Cannot flush a closed output stream");
		}
		baos.flush();
	}

	public void write(int b) throws IOException {
		if (closed) {
			throw new IOException("Cannot write to a closed output stream");
		}
		baos.write((byte) b);
	}

	public void write(byte b[]) throws IOException {
		write(b, 0, b.length);
	}

	public void write(byte b[], int off, int len) throws IOException {
		if (closed) {
			throw new IOException("Cannot write to a closed output stream");
		}
		baos.write(b, off, len);
	}

	public boolean closed() {
		return (this.closed);
	}

	public void reset() {
	}

	@Override
	public boolean isReady() {
		return this.isReady();
	}

	@Override
	public void setWriteListener(WriteListener writeListener) {
		this.setWriteListener(writeListener);
	}
}
