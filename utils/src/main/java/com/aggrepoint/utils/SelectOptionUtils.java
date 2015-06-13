package com.aggrepoint.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SelectOptionUtils {
	public static class OptionGroup {
		private Object key;
		private String label;
		private ArrayList<Option> options;

		public OptionGroup(Object key, String label) {
			this.key = key;
			this.label = label;
			options = new ArrayList<Option>();
		}

		public OptionGroup(String label) {
			this(null, label);
		}

		public String getLabel() {
			return label;
		}

		public Object getKey() {
			return key;
		}

		public List<Option> getOptions() {
			return options;
		}

		public Option add(Option option) {
			options.add(option);
			return option;
		}
	}

	public static class Option {
		private String value;
		private String label;

		public Option(String value, String label) {
			this.value = value;
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}
	}

	public static <T, K> List<OptionGroup> build(Collection<T> groups,
			Collection<K> options, Function<T, ?> groupKeyMapper,
			Function<T, String> groupLabelMapper,
			Function<K, ?> optionGroupKeyMapper,
			Function<K, String> optionValueMapper,
			Function<K, String> optionLabelMapper) {
		List<OptionGroup> all = groups
				.stream()
				.map(p -> new OptionGroup(groupKeyMapper.apply(p),
						groupLabelMapper.apply(p)))
				.collect(Collectors.toList());

		HashMap<Object, OptionGroup> map = CollectionUtils.toHashMap(all,
				OptionGroup::getKey);
		options.stream().forEach(
				p -> {
					OptionGroup group = map.get(optionGroupKeyMapper.apply(p));
					if (group != null)
						group.add(new Option(optionValueMapper.apply(p),
								optionLabelMapper.apply(p)));
				});
		return all;
	}

	public static <T> List<Option> build(List<T> list,
			Function<T, Object> keyFunc, Function<T, Object> valueFunc) {
		return list
				.stream()
				.map(p -> {
					Object key = keyFunc.apply(p);
					Object value = valueFunc.apply(p);
					return new Option(key == null ? "" : key.toString(),
							value == null ? "" : value.toString());
				}).collect(Collectors.toList());
	}

}
