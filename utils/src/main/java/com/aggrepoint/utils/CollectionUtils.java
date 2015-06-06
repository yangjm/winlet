package com.aggrepoint.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 
 * @author jiangmingyang
 */
public class CollectionUtils {
	public static <T, K, M extends Map<K, T>> M toMap(Collection<T> entities,
			Function<T, K> keyMapper, Supplier<M> supplier) {
		return entities.stream().collect(
				Collectors.toMap(keyMapper, Function.identity(), (a, b) -> b,
						supplier));
	}

	public static <T, K, V, M extends Map<K, V>> M toMap(
			Collection<T> entities, Function<T, K> keyMapper,
			Function<T, V> valueMapper, Supplier<M> supplier) {
		return entities.stream()
				.collect(
						Collectors.toMap(keyMapper, valueMapper, (a, b) -> b,
								supplier));
	}

	/**
	 * Convert a collection to HashMap, The key is decided by keyMapper, value
	 * is the object in collection
	 * 
	 * @param entities
	 * @param keyMapper
	 * @return
	 */

	public static <T, K> HashMap<K, T> toHashMap(Collection<T> entities,
			Function<T, K> keyMapper) {
		return toMap(entities, keyMapper, HashMap<K, T>::new);
	}

	public static <T, K> HashMap<K, T> toHashMap(T[] entities,
			Function<T, K> keyMapper) {
		return toHashMap(Arrays.asList(entities), keyMapper);
	}

	public static <T, K, V> HashMap<K, V> toHashMap(Collection<T> entities,
			Function<T, K> keyMapper, Function<T, V> valueMapper) {
		return toMap(entities, keyMapper, valueMapper, HashMap<K, V>::new);
	}

	public static <T, K, V> HashMap<K, V> toHashMap(T[] entities,
			Function<T, K> keyMapper, Function<T, V> valueMapper) {
		return toHashMap(Arrays.asList(entities), keyMapper, valueMapper);
	}

	/**
	 * Convert a collection to Hashtable. The key is decided by keyMapper, value
	 * is the object in collection
	 * 
	 * @param entities
	 * @param keyMapper
	 * @return
	 */
	public static <T, K> Hashtable<K, T> toHashtable(Collection<T> entities,
			Function<T, K> keyMapper) {
		return toMap(entities, keyMapper, Hashtable<K, T>::new);
	}

	public static <T, K> Hashtable<K, T> toHashtable(T[] entities,
			Function<T, K> keyMapper) {
		return toHashtable(Arrays.asList(entities), keyMapper);
	}

	public static <T, K, V> Hashtable<K, V> toHashtable(Collection<T> entities,
			Function<T, K> keyMapper, Function<T, V> valueMapper) {
		return toMap(entities, keyMapper, valueMapper, Hashtable<K, V>::new);
	}

	public static <T, K, V> Hashtable<K, V> toHashtable(T[] entities,
			Function<T, K> keyMapper, Function<T, V> valueMapper) {
		return toHashtable(Arrays.asList(entities), keyMapper, valueMapper);
	}

	public static <T, K, V, C extends Collection<V>> HashMap<K, C> group(
			Collection<T> entities, Function<T, K> keyMapper,
			Function<T, V> valueMapper, Supplier<C> collectionFactory) {
		return entities.stream().collect(
				Collectors.groupingBy(
						keyMapper,
						HashMap::new,
						Collectors.mapping(valueMapper,
								Collectors.toCollection(collectionFactory))));
	}

	public static <T, K, V> HashMap<K, HashSet<V>> group(
			Collection<T> entities, Function<T, K> keyMapper,
			Function<T, V> valueMapper) {
		return group(entities, keyMapper, valueMapper, HashSet::new);
	}

	public static <T, K, V> HashMap<K, String> groupJoining(
			Collection<T> entities, Function<T, K> keyMapper,
			Function<T, String> valueMapper, String delimiter) {
		return entities.stream().collect(
				Collectors.groupingBy(
						keyMapper,
						HashMap::new,
						Collectors.mapping(valueMapper,
								Collectors.joining(delimiter))));
	}

	public static <T, K, V> HashMap<K, HashSet<V>> group(T[] entities,
			Function<T, K> keyMapper, Function<T, V> valueMapper) {
		return group(Arrays.asList(entities), keyMapper, valueMapper);
	}

	/**
	 * Extract attribute from objects in collection and store them in array and
	 * return.
	 * 
	 * @param entities
	 * @param keyMapper
	 * @param arr
	 * @return
	 */
	public static <T, K> K[] toArray(Collection<T> entities,
			Function<T, K> keyMapper, K[] arr) {
		return entities.stream().map(keyMapper).toArray(size -> arr);
	}

	/**
	 * Move an object in List up
	 * 
	 * @param list
	 * @param key
	 * @param keyMapper
	 * @return
	 */
	public static <T, K> boolean moveUp(List<T> list, K key,
			Function<T, K> keyMapper, int n) {
		ArrayList<T> newList = new ArrayList<T>();
		boolean changed = false;
		for (int i = 0; i < list.size(); i++) {
			T item = list.get(i);

			if (i > 0 && key.equals(keyMapper.apply(item))) {
				int posi = i - n;
				if (posi < 0)
					posi = 0;
				newList.add(posi, item);
				changed = true;
			} else
				newList.add(item);
		}

		if (changed) {
			list.clear();
			list.addAll(newList);
			return true;
		}
		return false;
	}

	public static <T, K> boolean moveUp(List<T> list, K key,
			Function<T, K> keyMapper) {
		return moveUp(list, key, keyMapper, 1);
	}

	/**
	 * Move an object in List down
	 * 
	 * @param list
	 * @param key
	 * @param keyMapper
	 * @return
	 */
	public static <T, K> boolean moveDown(List<T> list, K key,
			Function<T, K> keyMapper, int n) {
		ArrayList<T> newList = new ArrayList<T>();
		boolean changed = false;
		int start = list.size() - 1;
		for (int i = start; i >= 0; i--) {
			T item = list.get(i);

			if (i != start && key.equals(keyMapper.apply(item))) {
				int posi = n;
				if (newList.size() < posi)
					posi = newList.size();
				newList.add(posi, item);
				changed = true;
			} else
				newList.add(0, item);
		}

		if (changed) {
			list.clear();
			list.addAll(newList);
			return true;
		}
		return false;
	}

	public static <T, K> boolean moveDown(List<T> list, K key,
			Function<T, K> keyMapper) {
		return moveDown(list, key, keyMapper, 1);
	}

	/**
	 * 将列表中id为key的项向前或向后移动n个位置。n为正表示向后移动，n为负表示向前移动。
	 * 
	 * @param list
	 * @param key
	 * @param keyMapper
	 * @param n
	 * @return
	 */
	public static <T, K> boolean move(List<T> list, K key,
			Function<T, K> keyMapper, int n) {
		if (n == 0)
			return false;

		if (n > 0)
			return moveDown(list, key, keyMapper, n);

		return moveUp(list, key, keyMapper, -n);
	}

	/**
	 * 如果before为true则把key移动到ref前面，否则把key移动到ref后面
	 * 
	 * @param list
	 * @param key
	 * @param ref
	 * @param keyMapper
	 * @param before
	 * @return 被移动的元素移动后在列表中的位置，-1表示没有被移动
	 */
	public static <T, K> List<T> move(List<T> list, K key, K ref,
			Function<T, K> keyMapper, boolean before,
			Function<T, Integer> orderGet, OrderSetter<T> orderSet, int orderGap) {
		if (key == ref)
			return null;

		// { 找到要移动的项和参考项所在位置
		int keyidx = -1;
		int refidx = -1;
		int idx = 0;

		for (T t : list) {
			K id = keyMapper.apply(t);
			if (id.equals(key))
				keyidx = idx;
			if (id.equals(ref))
				refidx = idx;

			if (keyidx != -1 && refidx != -1)
				break;

			idx++;
		}

		if (keyidx == -1 || refidx == -1)
			return null;
		// }

		// { 拿走要移动的项
		T t = list.remove(keyidx);
		if (refidx >= keyidx)
			refidx--;

		if (!before)
			refidx++;
		// }

		// 放回要移动的项
		list.add(refidx, t);

		// { 设置排序位置
		ArrayList<T> moved = new ArrayList<T>();

		int startOrder = 0;
		if (refidx > 0)
			startOrder = orderGet.apply(list.get(refidx - 1));

		for (int i = refidx + 1; i < list.size(); i++) {
			int order = orderGet.apply(list.get(i));
			if (order - startOrder >= i - refidx + 1) {
				int gap = (order - startOrder) / (i - refidx + 1);
				for (int j = refidx; j < i; j++) {
					T move = list.get(j);

					orderSet.apply(move, startOrder + (j - refidx + 1) * gap);
					moved.add(move);
				}

				return moved;
			}
		}

		for (int i = refidx; i < list.size(); i++) {
			T move = list.get(i);

			orderSet.apply(move, startOrder + (i - refidx + 1) * orderGap);
			moved.add(move);
		}

		return moved;
		// }
	}

	/**
	 * 建立树形结构
	 * 
	 * @param list
	 * @param keyMapper
	 * @param parentKeyMapper
	 * @param childMapper
	 * @return
	 */
	public static <T, K> ArrayList<T> buildTree(Collection<T> list,
			Function<T, K> keyMapper, Function<T, K> parentKeyMapper,
			Function<T, Collection<T>> childMapper) {
		HashMap<K, T> map = toHashMap(list, keyMapper);

		ArrayList<T> root = new ArrayList<T>();
		for (T t : list) {
			K key = keyMapper.apply(t);
			K parentKey = parentKeyMapper.apply(t);
			if (parentKey == null || key.equals(parentKey))
				root.add(t);
			else
				childMapper.apply(map.get(parentKey)).add(t);
		}

		return root;
	}

	public static <T, K> ArrayList<T> copyTrees(Collection<T> trees,
			Function<T, K> keyMapper, Function<T, Collection<T>> childMapper,
			Function<T, T> cloneMapper, Collection<K> toCopy) {
		ArrayList<T> copy = new ArrayList<T>();

		if (toCopy == null)
			return copy;

		for (T node : trees) {
			T copyNode = copyTree(node, keyMapper, childMapper, cloneMapper,
					toCopy);
			if (copyNode != null)
				copy.add(copyNode);
		}

		return copy;
	}

	public static <T, K> T copyTree(T node, Function<T, K> keyMapper,
			Function<T, Collection<T>> childMapper, Function<T, T> cloneMapper,
			Collection<K> toCopy) {
		if (toCopy == null)
			return null;

		T copy = null;

		Collection<T> children = childMapper.apply(node);
		if (children != null && children.size() > 0) {
			for (T child : children) {
				T childCopy = copyTree(child, keyMapper, childMapper,
						cloneMapper, toCopy);
				if (childCopy != null) {
					if (copy == null)
						copy = cloneMapper.apply(node);
					childMapper.apply(copy).add(childCopy);
				}
			}
		}

		if (copy == null && toCopy.contains(keyMapper.apply(node)))
			copy = cloneMapper.apply(node);

		return copy;
	}

	public static int[] toArray(Collection<Integer> col) {
		if (col == null)
			return null;
		int[] arr = new int[col.size()];
		int i = 0;
		for (Integer v : col)
			arr[i++] = v.intValue();
		return arr;
	}

	/**
	 * 根据主对象的组件批量加载子对象，然后把子对象分配给各个主对象
	 * 
	 * @param list 主对象列表
	 * @param keyMapper 从主对象获取ID
	 * @param childCollectionMapper 从主对象获取保存子对象的集合
	 * @param childLoader 负责批量加载子对象
	 * @param parentKeyMapper 从子对象中获取主对象ID
	 * @return
	 */
	public static <T, K, C> Collection<T> loadChildren(Collection<T> list,
			Function<T, K> keyMapper,
			Function<T, Collection<C>> childCollectionMapper,
			Function<Collection<K>, Collection<C>> childLoader,
			Function<C, K> parentKeyMapper) {
		Stream<C> children = childLoader.apply(
				list.stream().map(keyMapper).collect(Collectors.toList()))
				.stream();

		list.forEach(p -> {
			Collection<C> cc = childCollectionMapper.apply(p);
			K key = keyMapper.apply(p);

			children.filter(p1 -> parentKeyMapper.apply(p1) == key).forEach(
					p1 -> {
						cc.add(p1);
					});
		});
		return list;
	}
}
