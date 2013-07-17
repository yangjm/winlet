package com.aggrepoint.winlet;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Vector;

import com.icebean.core.locale.LocaleManager;
import com.icebean.core.msg.MessageBoundle;

/**
 * [@CN:ce shi@EN:test]
 * 
 * [|site name|app name@CN:zhan dian (0) zhong ying yong (1) bao han de chuang
 * kou@EN:windows in app (1) of site (0)]
 * 
 * @author Owner
 * 
 */
public class LangTextProcessor {
	char[] m_chars;
	int m_len;
	int m_mlen;
	int m_posi;
	Vector<String> m_lss;

	protected LangTextProcessor() {
	}

	String parseExpression() throws IOException {
		if (m_chars[m_posi] != '[' || m_posi >= m_mlen)
			return null;

		Vector<String> keys = new Vector<String>();
		StringBuffer sb = new StringBuffer();
		boolean bInPlaceMode = false;
		/** 1:in lsid 2:in text 3:in replace string */
		int mode = 1;

		if (m_chars[m_posi + 1] == '@')
			bInPlaceMode = false;
		else if (m_chars[m_posi + 1] == '|') {
			bInPlaceMode = true;
			mode = 3;
		} else
			return null;

		boolean bFinish = false;
		boolean bKeep = false;
		StringBuffer out = new StringBuffer();
		m_posi += 2;

		for (; !bFinish && m_posi < m_len; m_posi++) {
			switch (mode) {
			case 1:
				if (m_chars[m_posi] == ':') {
					bKeep = m_lss.contains(sb.toString().trim());
					mode = 2;
					sb = new StringBuffer();
				} else
					sb.append(m_chars[m_posi]);
				break;
			case 2:
				if (m_chars[m_posi] == ']') {
					mode = 0;
					if (bKeep)
						if (bInPlaceMode)
							out.append(MessageBoundle.constructMessageStatic(sb
									.toString(), keys.toArray(new String[keys
									.size()])));
						else
							out.append(sb.toString());
					m_posi--;
					bFinish = true;
				} else if (m_chars[m_posi] == '@') {
					mode = 1;
					if (bKeep)
						if (bInPlaceMode)
							out.append(MessageBoundle.constructMessageStatic(sb
									.toString(), keys.toArray(new String[keys
									.size()])));
						else
							out.append(sb.toString());
					sb = new StringBuffer();
				} else {
					String str = parseExpression();
					if (str == null)
						sb.append(m_chars[m_posi]);
					else
						sb.append(str);
				}
				break;
			case 3:
				if (m_chars[m_posi] == '|') {
					keys.add(sb.toString());
					sb = new StringBuffer();
				} else if (m_chars[m_posi] == '@') {
					keys.add(sb.toString());
					mode = 1;
					sb = new StringBuffer();
				} else {
					String str = parseExpression();
					if (str == null)
						sb.append(m_chars[m_posi]);
					else
						sb.append(str);
				}
				break;
			}
		}

		return out.toString();
	}

	void doParse(Writer out, String str) throws IOException {
		m_lss = LocaleManager.getLSIDs(LangTextProcessor.class, null);
		m_chars = str.toCharArray();
		m_len = m_chars.length;
		m_mlen = m_len - 1;
		m_posi = 0;

		for (m_posi = 0; m_posi < m_len; m_posi++) {
			if (m_chars[m_posi] == '['
					&& m_posi < m_mlen
					&& (m_chars[m_posi + 1] == '@' || m_chars[m_posi + 1] == '|'))
				out.append(parseExpression());
			else
				out.append(m_chars[m_posi]);
		}
	}

	public static void parseToWriter(Writer out, String str) throws IOException {
		new LangTextProcessor().doParse(out, str);
	}

	public static String parse(String str) throws IOException {
		StringWriter sw = new StringWriter();
		new LangTextProcessor().doParse(sw, str);
		return sw.toString();
	}
}
