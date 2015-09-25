package com.aggrepoint.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;

public class CompressFilter implements Filter {
	static final Pattern JSON_LD = Pattern
			.compile(
					"<script\\s+type\\s*=\\s*([\"'])\\s*application/ld\\+json\\s*\\1\\s*>(.*?)</script>",
					Pattern.DOTALL);

	private static class CompressResponseStream extends ServletOutputStream {
		private ByteArrayOutputStream baos = null;
		private boolean closed = false;
		private HttpServletResponse response = null;
		private ServletOutputStream output = null;

		public CompressResponseStream(HttpServletResponse response)
				throws IOException {
			super();
			closed = false;
			this.response = response;
			this.output = response.getOutputStream();
			baos = new ByteArrayOutputStream();
		}

		public void close() throws IOException {
			if (closed) {
				throw new IOException(
						"This output stream has already been closed");
			}

			String contentType = response.getContentType();
			if (contentType != null && contentType.startsWith("text/html")) {
				String html = baos.toString();

				// { extract JSON-LD before compression, HtmlCompressor cannot
				// handle them
				ArrayList<String> jsonLd = new ArrayList<String>();

				while (true) {
					Matcher m = JSON_LD.matcher(html);
					if (!m.find())
						break;

					jsonLd.add(m.group(2).replace("\n", "").replace("\r", ""));
					html = m.replaceFirst("");
				}
				// }

				HtmlCompressor compressor = new HtmlCompressor();
				compressor.setCompressJavaScript(true);
				compressor.setCompressCss(true);
				html = compressor.compress(html);

				// insert JSON-LD back to before </head> or </body>
				if (jsonLd.size() > 0) {
					StringBuffer sb = new StringBuffer();
					for (String str : jsonLd)
						sb.append("<script type=\"application/ld+json\">")
								.append(str).append("</script>");

					String lower = html.toLowerCase();
					int idx = lower.indexOf("</head>");
					if (idx <= 0)
						idx = lower.indexOf("</body>");
					if (idx > 0)
						html = html.substring(0, idx) + sb.toString()
								+ html.substring(idx);
				}

				output.write(html.getBytes());
			} else
				output.write(baos.toByteArray());

			output.flush();
			output.close();
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

		@Override
		public boolean isReady() {
			return output.isReady();
		}

		@Override
		public void setWriteListener(WriteListener writeListener) {
			output.setWriteListener(writeListener);
		}
	}

	private class CompressResponseWrapper extends HttpServletResponseWrapper {
		protected HttpServletResponse origResponse = null;
		protected ServletOutputStream stream = null;
		protected PrintWriter writer = null;

		public CompressResponseWrapper(HttpServletResponse response) {
			super(response);
			origResponse = response;
		}

		public ServletOutputStream createOutputStream() throws IOException {
			return new CompressResponseStream(origResponse);
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

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		CompressResponseWrapper wrappedResponse = new CompressResponseWrapper(
				(HttpServletResponse) response);
		chain.doFilter((HttpServletRequest) request, wrappedResponse);
		wrappedResponse.finishResponse();
	}

	@Override
	public void destroy() {
	}
}
