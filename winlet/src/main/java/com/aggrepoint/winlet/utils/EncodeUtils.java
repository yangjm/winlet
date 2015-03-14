package com.aggrepoint.winlet.utils;

import java.util.ArrayList;

import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.errors.EncodingException;
import org.owasp.esapi.reference.DefaultEncoder;

/**
 * @author: Yang Jiang Ming
 */
public class EncodeUtils {
	private static Encoder encoder;
	static {
		try {
			ArrayList<String> encoders = new ArrayList<String>();
			encoders.add("HTMLEntityCodec");
			encoders.add("XMLEntityCodec");
			encoder = new DefaultEncoder(encoders);
		} catch (Exception e) {
			e.printStackTrace();
			encoder = ESAPI.encoder();
		}
	}

	private static String toStr(Object obj) {
		if (obj == null)
			return null;

		return encoder.canonicalize(obj.toString(), false, false);
	}

	/**
	 * Encode string for HTML body context
	 * 
	 * @param obj
	 * @return
	 */
	public static String html(Object obj) {
		return encoder.encodeForHTML(toStr(obj));
	}

	public static String canonicalize(Object obj) {
		return toStr(obj);
	}

	/**
	 * Encode string for HTML body context
	 * 
	 * @param obj
	 * @return
	 */
	public static String bbcode2html(Object obj) {
		return BBCode.toHtml(toStr(obj));
	}

	/**
	 * Decode a string previously encoded for HTML context
	 * 
	 * @param str
	 * @return
	 */
	public static String decodeHtml(String str) {
		return encoder.decodeForHTML(str);
	}

	/**
	 * Encode string for HTML element attribute context.
	 * 
	 * @param obj
	 * @return
	 */
	public static String attr(Object obj) {
		return encoder.encodeForHTMLAttribute(toStr(obj));
	}

	/**
	 * Encode string for CSS context
	 * 
	 * @param obj
	 * @return
	 */
	public static String css(Object obj) {
		return encoder.encodeForCSS(toStr(obj));
	}

	/**
	 * Encode string for JavaScript context
	 * 
	 * @param obj
	 * @return
	 */
	public static String js(Object obj) {
		return encoder.encodeForJavaScript(toStr(obj));
	}

	/**
	 * Encode string for URL context
	 * 
	 * @param obj
	 * @return
	 * @throws EncodingException
	 */
	public static String url(Object obj) throws EncodingException {
		return encoder.encodeForURL(toStr(obj));
	}

	/**
	 * Encode string for XML context
	 * 
	 * @param obj
	 * @return
	 */
	public static String xml(Object obj) {
		return encoder.encodeForXML(toStr(obj));
	}

	/**
	 * Encode string for XML element attribute context
	 * 
	 * @param obj
	 * @return
	 */
	public static String xmlAttr(Object obj) {
		return encoder.encodeForXMLAttribute(toStr(obj));
	}

	/**
	 * Encode the load message
	 * 
	 * @param input
	 * @return
	 */
	public static String logMessage(String input) {
		if (input == null)
			return "";
		return input.replace("\n", "\\n").replace("\r", "\\r");
	}

	/*
	private static Object encodeJSONValue(Object val) throws JSONException {
		if (val instanceof JSONObject)
			return encodeJSONObject((JSONObject) val);
		else if (val instanceof JSONArray) {
			return encodeJSONArray((JSONArray) val);
		} else if (val instanceof String)
			return html((String) val);
		else
			return val;
	}

	private static JSONArray encodeJSONArray(JSONArray obj)
			throws JSONException {
		JSONArray encoded = new JSONArray();
		for (int i = 0; i < obj.size(); i++)
			encoded.add(encodeJSONValue(obj.get(i)));
		return encoded;
	}

	private static JSONObject encodeJSONObject(JSONObject obj)
			throws JSONException {
		JSONObject encoded = new JSONObject();
		for (Object name : obj.keySet())
			encoded.put(name, encodeJSONValue(obj.get(name)));
		return encoded;
	}
	*/

	/**
	 * Encodes a JSON string for HTML output. Only values are encoded.
	 * 
	 * @param str
	 * @return
	 */
	/*
	public static String json(String str) {
		try {
			return encodeJSONObject(JSONObject.fromObject(str)).toString();
		} catch (JSONException e) {
			return html(str);
		}
	}
	*/
}
