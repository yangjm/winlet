package com.aggrepoint.winlet;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.aggrepoint.utils.CollectionUtils;

public class CodeValue {
	private String code;
	private String value;

	public CodeValue(String code, String value) {
		this.code = code;
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public static <T> List<CodeValue> list(List<T> list,
			Function<T, Object> keyFunc, Function<T, Object> valueFunc) {
		return list
				.stream()
				.map(p -> {
					Object key = keyFunc.apply(p);
					Object value = valueFunc.apply(p);
					return new CodeValue(key == null ? "" : key.toString(),
							value == null ? "" : value.toString());
				}).collect(Collectors.toList());
	}

	public static <T> Map<String, String> map(List<T> list,
			Function<T, Object> keyFunc, Function<T, Object> valueFunc) {
		return CollectionUtils.toHashMap(list, p -> {
			Object obj = keyFunc.apply(p);
			return obj == null ? "" : obj.toString();
		}, p -> {
			Object obj = valueFunc.apply(p);
			return obj == null ? "" : obj.toString();
		});
	}
}
