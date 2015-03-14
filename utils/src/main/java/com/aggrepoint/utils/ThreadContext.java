package com.aggrepoint.utils;

import java.util.Hashtable;
import java.util.Stack;

/**
 * @author: Yang Jiang Ming
 */
public class ThreadContext {
	private static ThreadLocal<Hashtable<Object, Object>> threaContext = new ThreadLocal<Hashtable<Object, Object>>() {
		protected synchronized Hashtable<Object, Object> initialValue() {
			return new Hashtable<Object, Object>();
		}
	};

	private static ThreadLocal<Hashtable<Object, Object>> threaContextInheritable = new InheritableThreadLocal<Hashtable<Object, Object>>() {
		protected synchronized Hashtable<Object, Object> initialValue() {
			return new Hashtable<Object, Object>();
		}

		@SuppressWarnings("unchecked")
		protected synchronized Hashtable<Object, Object> childValue(
				Hashtable<Object, Object> parentValue) {
			Hashtable<Object, Object> obj = (Hashtable<Object, Object>) parentValue
					.clone();
			return obj;
		}
	};

	private static class ThreadStack<E> extends Stack<E> {
		private static final long serialVersionUID = 1L;
	}

	public static void setAttribute(String name, Object value,
			boolean accessBySub) {
		if (value != null) {
			threaContext.get().put(name, value);

			if (accessBySub)
				threaContextInheritable.get().put(name, value);
		} else {
			threaContext.get().remove(name);

			if (accessBySub)
				threaContextInheritable.get().remove(name);
		}
	}

	public static void clear(boolean clearSubAsWell) {
		threaContext.get().clear();
		if (clearSubAsWell)
			threaContextInheritable.get().clear();
	}

	public static void removeAttribute(String name, boolean accessBySub) {
		threaContext.get().remove(name);

		if (accessBySub)
			threaContextInheritable.get().remove(name);
	}

	@SuppressWarnings("unchecked")
	public static void pushAttribute(String name, Object value,
			boolean accessBySub) {
		if (value == null)
			return;

		Object obj;
		ThreadStack<Object> stack;

		obj = threaContext.get().get(name);
		if (obj == null || !(obj instanceof ThreadStack)) {
			stack = new ThreadStack<Object>();
			if (obj != null)
				stack.push(obj);
			stack.push(value);
			threaContext.get().put(name, stack);
		} else {
			stack = (ThreadStack<Object>) obj;
			stack.push(value);
		}

		if (accessBySub) {
			obj = threaContextInheritable.get().get(name);
			if (obj == null || !(obj instanceof ThreadStack)) {
				stack = new ThreadStack<Object>();
				if (obj != null)
					stack.push(obj);
				stack.push(value);
				threaContextInheritable.get().put(name, stack);
			} else {
				stack = (ThreadStack<Object>) obj;
				stack.push(value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static Object popAttribute(String name, boolean accessBySub) {
		Object val = null;
		ThreadStack<Object> stack;

		Object obj = threaContext.get().get(name);
		if (obj != null) {
			if (!(obj instanceof ThreadStack)) {
				val = obj;
				threaContext.get().remove(obj);
			} else {
				stack = (ThreadStack<Object>) obj;
				if (stack.size() > 0)
					val = stack.pop();
				if (stack.size() == 0)
					threaContext.get().remove(obj);
			}
		}

		if (accessBySub) {
			obj = threaContextInheritable.get().get(name);
			if (obj != null) {
				if (!(obj instanceof ThreadStack)) {
					if (val == null)
						val = obj;
					threaContextInheritable.get().remove(obj);
				} else {
					stack = (ThreadStack<Object>) obj;
					if (stack.size() > 0)
						if (val == null)
							val = stack.pop();
						else
							stack.pop();
					if (stack.size() == 0)
						threaContextInheritable.get().remove(obj);
				}
			}
		}

		return val;
	}

	public static void setAttribute(String name, Object value) {
		setAttribute(name, value, false);
	}

	public static void removeAttribute(String name) {
		removeAttribute(name, false);
	}

	public static Object popAttribute(String name) {
		return popAttribute(name, false);
	}

	@SuppressWarnings("unchecked")
	public static Object getAttribute(String name, boolean peekTrace,
			boolean traceUp) {
		Object obj = threaContext.get().get(name);

		if (obj == null && traceUp)
			obj = threaContextInheritable.get().get(name);

		if (obj != null && peekTrace && obj instanceof ThreadStack) {
			ThreadStack<Object> stack = (ThreadStack<Object>) obj;
			if (stack.size() == 0)
				return null;
			return stack.peek();
		}

		return obj;
	}

	public static Object getAttribute(String name, boolean traceUp) {
		return getAttribute(name, true, traceUp);
	}

	public static Object getAttribute(String name) {
		return getAttribute(name, false);
	}
}