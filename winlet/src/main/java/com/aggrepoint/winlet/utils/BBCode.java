package com.aggrepoint.winlet.utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Supports syntax of BBCode editor: http://www.wysibb.com/
 *  
 * @author: Yang Jiang Ming
 */
public class BBCode {
	static final int TYPE_ROOT = 0;
	static final int TYPE_TEXT = 1;
	static final int TYPE_TAG_NESTED = 2;
	static final int TYPE_TAG_SIMPLE = 3;

	int type;
	String text;
	ArrayList<BBCode> content;

	static String[] TO_REPLACE = {
			"(?is)\\[\\s*img\\s*\\]([^\\[]*)\\[\\s*/\\s*img\\s*\\]",
			"(?is)\\[\\s*url\\s*\\]([^\\[]*)\\[\\s*/\\s*url\\s*\\]",
			"(?is)\\[\\s*list\\s*=\\s*1\\s*\\](.*)\\[\\s*/\\s*list\\s*\\]" };
	static String[] REPLACE_TO = { "[img=$1][/img]", "[url=$1]$1[/url]",
			"[olist]$1[/olist]" };

	private BBCode(String str) {
		this(TYPE_ROOT);

		for (int i = 0; i < TO_REPLACE.length; i++)
			str = str.replaceAll(TO_REPLACE[i], REPLACE_TO[i]);

		parse(str.toCharArray(), new int[1]);

		simplify();
	}

	private BBCode(int type) {
		this.type = type;

		if (type == TYPE_TEXT)
			text = "";
		else
			content = new ArrayList<BBCode>();
	}

	private void addText(String text) {
		if (text == null || "".equals(text))
			return;

		BBCode p = new BBCode(TYPE_TEXT);
		p.text = text;
		content.add(p);
	}

	private BBCode addTag() {
		BBCode p = new BBCode(TYPE_TAG_NESTED);
		content.add(p);
		return p;
	}

	private void parse(char[] chars, int[] idx) {
		StringBuffer sb = new StringBuffer();

		while (idx[0] < chars.length) {
			switch (chars[idx[0]]) {
			case '[':
				addText(sb.toString());
				sb = new StringBuffer();
				idx[0]++;
				addTag().parse(chars, idx);
				break;
			case ']':
				if (type == TYPE_ROOT)
					sb.append(chars[idx[0]]);
				else {
					addText(sb.toString());
					return;
				}
				break;
			default:
				sb.append(chars[idx[0]]);
				break;
			}

			idx[0]++;
		}

		addText(sb.toString());
	}

	private void simplify() {
		if (type != TYPE_TAG_NESTED && type != TYPE_ROOT)
			return;

		for (BBCode p : content) {
			if (p.type == TYPE_TAG_NESTED) {
				if (p.content.size() == 0) {
					p.type = TYPE_TAG_SIMPLE;
					p.text = "";
				} else if (p.content.size() == 1
						&& p.content.get(0).type == TYPE_TEXT) {
					p.type = TYPE_TAG_SIMPLE;
					p.text = p.content.get(0).text;
				} else
					p.simplify();
			}
		}
	}

	static final String BRACKET_ST = EncodeUtils.html("[");
	static final String BRACKET_ED = EncodeUtils.html("]");
	static final String[][] MAP = {
			// [center]
			{ "(?i)^\\s*center\\s*$", "<p style=\"text-align:center\">" },
			// [/center]
			{ "(?i)^\\s*/\\s*center\\s*$", "</p>" },
			// [left]
			{ "(?i)^\\s*left\\s*$", "<p style=\"text-align:left\">" },
			// [/left]
			{ "(?i)^\\s*/\\s*left\\s*$", "</p>" },
			// [right]
			{ "(?i)^\\s*right\\s*$", "<p style=\"text-align:right\">" },
			// [/right]
			{ "(?i)^\\s*/\\s*right\\s*$", "</p>" },
			// [size=?]
			{ "(?i)^\\s*size\\s*=\\s*([\\d\\w]+)\\s*$",
					"<span style=\"font-size: $1%\">" },
			// [/size]
			{ "(?i)^\\s*/\\s*size\\s*$", "</span>" },
			// [font=?]
			{ "(?i)^\\s*font\\s*=\\s*([\\w\\s]+)\\s*$",
					"<span style=\"font-family: $1\">" },
			// [/font]
			{ "(?i)^\\s*/\\s*font\\s*$", "</span>" },
			// [b]
			{ "(?i)^\\s*b\\s*$", "<b>" },
			// [/b]
			{ "(?i)^\\s*/\\s*b\\s*$", "</b>" },
			// [i]
			{ "(?i)^\\s*i\\s*$", "<i>" },
			// [/i]
			{ "(?i)^\\s*/\\s*i\\s*$", "</i>" },
			// [u]
			{ "(?i)^\\s*u\\s*$", "<u>" },
			// [/u]
			{ "(?i)^\\s*/\\s*u\\s*$", "</u>" },
			// [s]
			{ "(?i)^\\s*s\\s*$", "<strike>" },
			// [/s]
			{ "(?i)^\\s*/\\s*s\\s*$", "</strike>" },
			// [sup]
			{ "(?i)^\\s*sup\\s*$", "<sup>" },
			// [/sup]
			{ "(?i)^\\s*/\\s*sup\\s*$", "</sup>" },
			// [sub]
			{ "(?i)^\\s*sub\\s*$", "<sub>" },
			// [/sub]
			{ "(?i)^\\s*/\\s*sub\\s*$", "</sub>" },
			// [quote]
			{ "(?i)^\\s*quote\\s*$", "<blockquote>" },
			// [/quote]
			{ "(?i)^\\s*/\\s*quote\\s*$", "</blockquote>" },
			// [list]
			{ "(?i)^\\s*list\\s*$", "<ul>" },
			// [/list]
			{ "(?i)^\\s*/\\s*list\\s*$", "</ul>" },
			// [olist]
			{ "(?i)^\\s*olist\\s*$", "<ol>" },
			// [/olist]
			{ "(?i)^\\s*/\\s*olist\\s*$", "</ol>" },
			// [*]
			{ "(?i)^\\s*\\*\\s*$", "<li>" },
			// [/*]
			{ "(?i)^\\s*/\\s*\\*\\s*$", "</li>" },
			// [code]
			{ "(?i)^\\s*code\\s*$", "<pre>" },
			// [/code]
			{ "(?i)^\\s*/\\s*code\\s*$", "</pre>" },
			// [table]
			{ "(?i)^\\s*table\\s*$", "<table>" },
			// [/table]
			{ "(?i)^\\s*/\\s*table\\s*$", "</table>" },
			// [tr]
			{ "(?i)^\\s*tr\\s*$", "<tr>" },
			// [/tr]
			{ "(?i)^\\s*/\\s*tr\\s*$", "</tr>" },
			// [td]
			{ "(?i)^\\s*td\\s*$", "<td>" },
			// [/td]
			{ "(?i)^\\s*/\\s*td\\s*$", "</td>" },
			// [color=?]
			{ "(?i)^\\s*color\\s*=\\s*(\\S+)\\s*$", "<font color=\"$1\">" },
			// [/color]
			{ "(?i)^\\s*/\\s*color\\s*$", "</font>" },
			// [url=?]
			{
					"(?i)^\\s*url\\s*=\\s*((ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\:\\/\\\\\\+=&amp;%\\$#_]*)?)\\s*$",
					"<a href=\"$1\" target=\"_blank\">" },
			// [/url]
			{ "(?i)^\\s*/\\s*url\\s*$", "</a>" },
			// [img=?]
			{
					"(?i)^\\s*img\\s*=\\s*((ht|f)tp(s?)\\:\\/\\/[0-9a-zA-Z]([-.\\w]*[0-9a-zA-Z])*(:(0-9)*)*(\\/?)([a-zA-Z0-9\\-\\.\\?\\,\\:\\/\\\\\\+=&amp;%\\$#_]*)?)\\s*$",
					"<img src=\"$1\" />" },
			// [/img]
			{ "(?i)^\\s*/\\s*img\\s*$", "" } };
	static final Pattern[] BBC_TAGS;
	static {
		BBC_TAGS = new Pattern[MAP.length];
		for (int i = 0; i < MAP.length; i++)
			BBC_TAGS[i] = Pattern.compile(MAP[i][0]);
	}

	static final String[][] DISPLAY_FORMAT = { { "\\&#xd;\\&#xa;", "<br />" },
			{ "\\&#xd;", "<br />" }, { "\\&#xa;", "<br />" } };

	static String displayFormat(String str) {
		for (int i = 0; i < DISPLAY_FORMAT.length; i++)
			str = str.replaceAll(DISPLAY_FORMAT[i][0], DISPLAY_FORMAT[i][1]);
		return str;
	}

	private void toHtml(StringBuffer sb) {
		switch (type) {
		case TYPE_ROOT:
			for (BBCode p : content)
				p.toHtml(sb);
			break;
		case TYPE_TEXT:
			sb.append(displayFormat(EncodeUtils.html(text)));
			break;
		case TYPE_TAG_SIMPLE:
			int i = 0;
			for (; i < BBC_TAGS.length; i++) {
				Matcher m = BBC_TAGS[i].matcher(text);
				if (m.find()) {
					sb.append(m.replaceAll(MAP[i][1]));
					break;
				}
			}

			if (i < BBC_TAGS.length)
				break;

			sb.append(BRACKET_ST);
			sb.append(displayFormat(EncodeUtils.html(text)));
			sb.append(BRACKET_ED);
			break;
		case TYPE_TAG_NESTED:
			sb.append(BRACKET_ST);
			for (BBCode p : content)
				p.toHtml(sb);
			sb.append(BRACKET_ED);
			break;
		}
	}

	public static String toHtml(String bbcode) {
		BBCode code = new BBCode(bbcode);
		StringBuffer sb = new StringBuffer();
		code.toHtml(sb);

		return sb.toString();
	}
}
