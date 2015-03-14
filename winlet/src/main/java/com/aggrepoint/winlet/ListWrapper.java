package com.aggrepoint.winlet;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * 将List做封装，防止被应用修改
 * 
 * @author Jim
 *
 * @param <E>
 */
public class ListWrapper<E> implements List<E> {
	List<E> wrapped;

	public ListWrapper(List<E> wrap) {
		wrapped = wrap;
	}

	@Override
	public int size() {
		return wrapped.size();
	}

	@Override
	public boolean isEmpty() {
		return wrapped.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return wrapped.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return wrapped.iterator();
	}

	@Override
	public Object[] toArray() {
		return wrapped.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return wrapped.toArray(a);
	}

	@Override
	public boolean add(E e) {
		return false;
	}

	@Override
	public boolean remove(Object o) {
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return wrapped.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}

	@Override
	public void clear() {
	}

	@Override
	public E get(int index) {
		return wrapped.get(index);
	}

	@Override
	public E set(int index, E element) {
		return null;
	}

	@Override
	public void add(int index, E element) {
	}

	@Override
	public E remove(int index) {
		return null;
	}

	@Override
	public int indexOf(Object o) {
		return 0;
	}

	@Override
	public int lastIndexOf(Object o) {
		return 0;
	}

	@Override
	public ListIterator<E> listIterator() {
		return null;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return null;
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return null;
	}

}
