package com.aggrepoint.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.aggrepoint.utils.TriConsumer;
import com.aggrepoint.utils.TypeCast;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * 从JsonNode中提取值赋予对象
 * 
 * @author jiangmingyang
 */
public class JsonNodeValue {
	JsonNode node;
	JsonNodeValue This;

	public static enum ErrorType {
		NoField, WrongType, NoMatch
	};

	TriConsumer<ErrorType, String, String> onError;

	/** 遇到不存在的字段是否报错 */
	boolean optional = false;
	/** 遇到错误是否停止处理 */
	boolean stopOnError = true;
	boolean hasError = false;

	public class PatternMatcher {
		String field;
		String val;
		Matcher m;

		private PatternMatcher(String field, String val, Pattern p) {
			this.field = field;
			this.val = val;
			if (val != null && p != null) {
				m = p.matcher(val);
				m.find();
			}
		}

		public <T> PatternMatcher groupMatch(int group, Consumer<T> c, String s1, T t1, Object... others) {
			if (stopOnError && hasError)
				return this;

			if (m == null)
				return this;

			if (m.groupCount() < group) {
				onError(ErrorType.NoMatch, field, val);
				return this;
			}

			String str = m.group(group);
			if (!JsonNodeValue.matchStr(false, str, c, s1, t1, others))
				onError(ErrorType.NoMatch, field, val);

			return this;
		}

		public <T> PatternMatcher groupMatchIgnoreCase(int group, Consumer<T> c, String s1, T t1, Object... others) {
			if (stopOnError && hasError)
				return this;

			if (m == null)
				return this;

			if (m.groupCount() <= group) {
				onError(ErrorType.NoMatch, field, val);
				return this;
			}

			String str = m.group(group);
			if (!JsonNodeValue.matchStr(true, str, c, s1, t1, others))
				onError(ErrorType.NoMatch, field, val);
			return this;
		}

		public JsonNodeValue patternEnd() {
			return This;
		}
	}

	private JsonNodeValue(JsonNode node) {
		this.node = node;
		This = this;
	}

	public JsonNodeValue(JsonNode node, TriConsumer<ErrorType, String, String> onError) {
		this(node);
		this.onError = onError;
	}

	public JsonNodeValue(JsonNode node, Logger logger) {
		this(node);
		this.onError = (errorType, field, value) -> {
			switch (errorType) {
			case NoField:
				logger.info("字段{}不存在", field);
				break;
			case WrongType:
				logger.info("字段{}的类型不正确：{}", field, value);
				break;
			case NoMatch:
				logger.info("字段{}的值{}不正确", field, value);
				break;
			}
		};
	}

	private static <T> T get(JsonNode node, String field, Function<JsonNode, T> func) {
		if (node == null)
			return null;
		JsonNode nd = node.get(field);
		if (nd == null)
			return null;
		return func.apply(nd);
	}

	public static Integer intValue(JsonNode node, String field) {
		return get(node, field, JsonNode::asInt);
	}

	public static Long longValue(JsonNode node, String field) {
		return get(node, field, JsonNode::asLong);
	}

	public static String stringValue(JsonNode node, String field) {
		return get(node, field, JsonNode::asText);
	}

	public static Double doubleValue(JsonNode node, String field) {
		return get(node, field, JsonNode::asDouble);
	}

	private void onError(ErrorType et, String field, String val) {
		if (et == ErrorType.NoField && optional)
			return;

		hasError = true;
		if (onError != null)
			onError.accept(et, field, val);
	}

	private <T> T get(String field, Function<JsonNode, T> func) {
		JsonNode nd = node.get(field);
		if (nd == null) {
			onError(ErrorType.NoField, field, null);
			return null;
		}
		try {
			return func.apply(nd);
		} catch (Throwable e) {
			onError(ErrorType.WrongType, field, nd.asText());
			return null;
		}
	}

	public Integer intValue(String field) {
		return get(field, JsonNode::asInt);
	}

	public Long longValue(String field) {
		return get(field, JsonNode::asLong);
	}

	public String stringValue(String field) {
		return get(field, JsonNode::asText);
	}

	public Double doubleValue(String field) {
		return get(field, JsonNode::asDouble);
	}

	public JsonNodeValue intValue(String field, Consumer<Integer> c) {
		if (stopOnError && hasError)
			return this;

		Integer v = get(field, JsonNode::asInt);
		if (v != null && c != null)
			c.accept(v);

		return this;
	}

	public JsonNodeValue longValue(String field, Consumer<Long> c) {
		if (stopOnError && hasError)
			return this;

		Long v = get(field, JsonNode::asLong);
		if (v != null && c != null)
			c.accept(v);

		return this;
	}

	public JsonNodeValue stringValue(String field, Consumer<String> c) {
		if (stopOnError && hasError)
			return this;

		String v = get(field, JsonNode::asText);
		if (v != null && c != null)
			c.accept(v);
		return this;
	}

	public JsonNodeValue doubleValue(String field, Consumer<Double> c) {
		if (stopOnError && hasError)
			return this;

		Double v = get(field, JsonNode::asDouble);
		if (v != null && c != null)
			c.accept(v);
		return this;
	}

	/** 返回true表示找到匹配 */
	protected static <T> boolean matchStr(boolean ignoreCase, String str, Consumer<T> c, String s1, T t1,
			Object... others) {
		if (str == null && s1 == null || str != null && str.equals(s1)) {
			c.accept(t1);
			return true;
		} else if (others == null || others.length < 2) { // 没有匹配
			return false;
		}
		for (int i = 0; i < others.length; i += 2)
			if (str == null && others[i] == null
					|| str != null && (ignoreCase && str.equalsIgnoreCase(others[i].toString())
							|| !ignoreCase && str.equals(others[i]))) {
				c.accept(TypeCast.cast(others[i + 1]));
				return true;
			}
		return false;
	}

	public <T> JsonNodeValue match(String field, Consumer<T> c, String s1, T t1, Object... others) {
		if (stopOnError && hasError)
			return this;

		String str = stringValue(field);
		if (str == null && !optional)
			return this;

		// 必须在执行完毕stringValue重新设置matched
		if (!matchStr(false, str, c, s1, t1, others))
			onError(ErrorType.NoMatch, field, str);
		return this;
	}

	public <T> JsonNodeValue matchIgnoreCase(String field, Consumer<T> c, String s1, T t1, Object... others) {
		if (stopOnError && hasError)
			return this;

		String str = stringValue(field);
		if (str == null && !optional)
			return this;

		// 必须在执行完毕stringValue重新设置matched
		if (!matchStr(true, str, c, s1, t1, others))
			onError(ErrorType.NoMatch, field, str);
		return this;
	}

	public JsonNodeValue optional(boolean op) {
		optional = op;
		return this;
	}

	public JsonNodeValue stopOnError(boolean stop) {
		stopOnError = stop;
		return this;
	}

	public boolean hasError() {
		return hasError;
	}

	public PatternMatcher patternStart(String field, Pattern p) {
		String str = stringValue(field);

		return new PatternMatcher(field, str, p);
	}
}
