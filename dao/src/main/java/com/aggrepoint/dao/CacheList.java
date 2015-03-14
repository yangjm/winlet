package com.aggrepoint.dao;

import java.util.ArrayList;

public class CacheList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 2L;

	private CacheMetaData<T> mdata;

	CacheList(String method, Object[] args) {
		mdata = new CacheMetaData<T>(method, args);
	}

	CacheMetaData<T> getMetaData() {
		return mdata;
	}

	void setTimestamp(long timestamp) {
		mdata.setTimestamp(timestamp);
	}

	void setCount(long count) {
		mdata.setCount(count);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof CacheList) {
			@SuppressWarnings("unchecked")
			CacheList<T> cl = (CacheList<T>) other;

			return mdata.equals(cl.mdata);
		}

		return super.equals(other);
	}
}
