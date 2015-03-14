package com.aggrepoint.winlet.plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.aggrepoint.utils.CollectionUtils;
import com.aggrepoint.winlet.CodeValue;
import com.aggrepoint.winlet.HashMapWrapper;
import com.aggrepoint.winlet.ListWrapper;

/**
 * 使用主、子表结构的数据库表提供多个列表
 * 
 * @author Jim
 *
 * @param <S>
 *            加载主表数据的服务类
 * @param <H>
 *            主表数据类型
 * @param <T>
 *            子表数据类型
 */
public class CachedListGroup<S, H, T> {
	S svc;
	Function<S, List<H>> loadFunc;
	Function<H, String> listNameFunc;
	Function<H, List<T>> listFunc;
	Function<T, Object> keyFunc;
	Function<T, Object> valueFunc;

	List<H> cached;
	/** 本CachedListGroup加载的列表的名称 */
	HashSet<String> listNames = new HashSet<String>();
	HashMap<String, ListUnit<?>> lists;

	class ListGroupUnit implements ListUnit<T> {
		HashMapWrapper<String, T> map;
		HashMapWrapper<String, String> cvMap;
		ListWrapper<T> list;
		ListWrapper<CodeValue> cvList;

		void set(HashMapWrapper<String, T> map,
				HashMapWrapper<String, String> cvMap, ListWrapper<T> list,
				ListWrapper<CodeValue> cvList) {
			this.map = map;
			this.cvMap = cvMap;
			this.list = list;
			this.cvList = cvList;
		}

		void reset() {
			map.clear();
			cvMap.clear();
			list.clear();
			cvList.clear();
		}

		@Override
		public List<T> getList() {
			update();
			return list;
		}

		@Override
		public List<CodeValue> getCodeValueList() {
			update();
			return cvList;
		}

		@Override
		public Map<String, T> getMap() {
			update();
			return map;
		}

		@Override
		public Map<String, String> getCodeValueMap() {
			update();
			return cvMap;
		}
	}

	/**
	 * 
	 * @param lists
	 * @param svc
	 * @param loadFunc
	 *            加载主表数据并连带加载子表数据集合
	 * @param listNameFunc
	 *            从主表数据对象中获取列表名称的方法
	 * @param listFunc
	 *            从主表数据对象中获取子表对象列表的方法
	 * @param keyFunc
	 *            子表数据对象的键的获取方法
	 * @param valueFunc
	 *            字标数据对象的值得获取方法
	 */
	public CachedListGroup(HashMap<String, ListUnit<?>> lists, S svc,
			Function<S, List<H>> loadFunc, Function<H, String> listNameFunc,
			Function<H, List<T>> listFunc, Function<T, Object> keyFunc,
			Function<T, Object> valueFunc) {
		this.lists = lists;
		this.svc = svc;
		this.loadFunc = loadFunc;
		this.listNameFunc = listNameFunc;
		this.listFunc = listFunc;
		this.keyFunc = keyFunc;
		this.valueFunc = valueFunc;
		
		update();
	}

	@SuppressWarnings("unchecked")
	private synchronized void update() {
		List<H> list = loadFunc.apply(svc);

		if (cached != list) {
			// 保存当前的列表集合中由本CachedListGroup加载的列表的名称，用于删除已经不在数据库中存在的列表
			HashSet<String> currNames = new HashSet<String>();

			if (listNames != null) {
				currNames.addAll(listNames);

				for (String name : listNames) {
					ListUnit<?> existing = lists.get(name);
					if (existing != null
							&& existing instanceof CachedListGroup.ListGroupUnit) {
						((ListGroupUnit) existing).reset();
					}
				}
			}

			listNames = new HashSet<String>();

			if (list != null)
				for (H h : list) {
					String name = listNameFunc.apply(h);
					listNames.add(name);

					List<T> l = listFunc.apply(h);

					HashMapWrapper<String, T> map = new HashMapWrapper<String, T>(
							CollectionUtils.toHashMap(l, p -> {
								Object obj = keyFunc.apply(p);
								return obj == null ? "" : obj.toString();
							}, p -> p));
					HashMapWrapper<String, String> cvMap = new HashMapWrapper<String, String>(
							CodeValue.map(l, keyFunc, valueFunc));
					ListWrapper<T> lt = new ListWrapper<T>(l);
					ListWrapper<CodeValue> cvList = new ListWrapper<CodeValue>(
							CodeValue.list(l, keyFunc, valueFunc));

					ListGroupUnit unit = null;
					ListUnit<?> existing = lists.get(name);
					if (existing != null
							&& existing instanceof CachedListGroup.ListGroupUnit)
						unit = (ListGroupUnit) existing;
					else
						unit = new ListGroupUnit();
					unit.set(map, cvMap, lt, cvList);

					lists.put(name, unit);
				}

			currNames.removeAll(listNames);
			for (String name : currNames)
				lists.remove(name);

			cached = list;
		}
	}
}
