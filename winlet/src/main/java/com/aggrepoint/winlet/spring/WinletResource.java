package com.aggrepoint.winlet.spring;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.springframework.core.io.Resource;

/**
 * 封装经过Winlet注解处理的类资源
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 */
public class WinletResource implements Resource {
	Resource inner;
	byte[] bytes;

	WinletResource(Resource inner, byte[] bytes) {
		this.inner = inner;
		this.bytes = bytes;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(bytes);
	}

	@Override
	public boolean exists() {
		return inner.exists();
	}

	@Override
	public boolean isReadable() {
		return inner.isReadable();
	}

	@Override
	public boolean isOpen() {
		return inner.isOpen();
	}

	@Override
	public URL getURL() throws IOException {
		return inner.getURL();
	}

	@Override
	public URI getURI() throws IOException {
		return inner.getURI();
	}

	@Override
	public File getFile() throws IOException {
		return inner.getFile();
	}

	@Override
	public long contentLength() throws IOException {
		return bytes.length;
	}

	@Override
	public long lastModified() throws IOException {
		return inner.lastModified();
	}

	@Override
	public Resource createRelative(String relativePath) throws IOException {
		return inner.createRelative(relativePath);
	}

	@Override
	public String getFilename() {
		return inner.getFilename();
	}

	@Override
	public String getDescription() {
		return inner.getDescription();
	}
}
