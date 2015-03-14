package com.aggrepoint.dao;

import java.io.Serializable;

/**
 * @author Jim
 * 
 * @param <T>
 */
public class CacheMetaData<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 实现的方法 */
	private String method;
	/** 加载列表时传递的参数 */
	private Object[] args;
	private long timestamp;
	private long count;

	private long syncTime;

	CacheMetaData(String method, Object[] args) {
		this.method = method;
		this.args = args;
		syncTime = System.currentTimeMillis();
	}

	void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	long getTimestamp() {
		return timestamp;
	}

	void setCount(long count) {
		this.count = count;
	}

	long getCount() {
		return count;
	}

	void updateSyncTime() {
		syncTime = System.currentTimeMillis();
	}

	long getSyncTime() {
		return syncTime;
	}

	boolean equals(Object a, Object b) {
		if (a == null && b == null)
			return true;

		if (a == null || b == null)
			return false;

		return a.equals(b);
	}

	@Override
	public boolean equals(Object other) {
		if (other == null)
			return false;

		if (other instanceof CacheMetaData) {
			@SuppressWarnings("unchecked")
			CacheMetaData<T> ci = (CacheMetaData<T>) other;

			if (timestamp != ci.timestamp || count != ci.count)
				return false;

			if (!method.equals(ci.method))
				return false;

			if (args == null && ci.args != null || args != null
					&& ci.args == null)
				return false;

			if (args != null) {
				if (args.length != ci.args.length)
					return false;

				for (int i = 0; i < args.length; i++)
					if (!equals(args[i], ci.args[i]))
						return false;
			}

			return true;
		}

		return super.equals(other);
	}
}
