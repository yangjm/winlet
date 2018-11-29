package com.aggrepoint.winlet.format;

import java.util.regex.Pattern;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;

public class HtmlSanitizeUtil {
	static Policy policy = null;
	static {
		try {
			policy = Policy.getInstance(HtmlSanitizeUtil.class.getResource("/antisamy.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static Pattern CLOSED_IFRAME = Pattern.compile("<iframe([^>]+)/>", Pattern.MULTILINE);

	public static String sanitize(String html) {
		if (html == null)
			return html;

		try {
			String cleaned = new AntiSamy().scan(html, policy).getCleanHTML();
			if (cleaned != null)
				cleaned = CLOSED_IFRAME.matcher(cleaned).replaceAll("<iframe$1></iframe>");

			return cleaned;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}
}
