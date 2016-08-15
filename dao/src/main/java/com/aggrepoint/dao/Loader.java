package com.aggrepoint.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import com.aggrepoint.utils.CollectionUtils;
import com.aggrepoint.utils.ThreadContext;

public class Loader {
	public static final String THREAD_ATTR_LOADER = Loader.class.getName()
			+ ".LOADER";

	public static <T, K, S, SC extends Collection<S>> SC load(
			Collection<T> list, Function<T, K> keyGetter,
			Function<S, K> loadedKeyGetter, BiConsumer<T, S> match) {
		@SuppressWarnings("unchecked")
		Function<Collection<K>, SC> loader = (Function<Collection<K>, SC>) ThreadContext
				.getAttribute(THREAD_ATTR_LOADER);
		if (loader == null)
			return null;

		if (list == null || list.size() == 0)
			return null;

		Set<K> keys = CollectionUtils.toSet(list, keyGetter);
		if (keys == null || keys.size() == 0)
			return null;

		SC subs = loader.apply(keys);
		if (subs == null || subs.size() == 0)
			return subs;

		HashMap<K, List<S>> map = CollectionUtils.groupAsList(subs,
				loadedKeyGetter);

		for (T item : list) {
			K key = keyGetter.apply(item);
			if (key == null)
				continue;

			List<S> sublist = map.get(key);
			if (sublist == null || sublist.size() == 0)
				continue;

			sublist.forEach(p -> match.accept(item, p));
		}

		return subs;
	}
}
