package com.aggrepoint.dao;


public class CachePageList<T> extends PageList<T> {
	private static final long serialVersionUID = 1L;

	private CacheMetaData<T> mdata;

	CachePageList(String method, Object[] args) {
		mdata = new CacheMetaData<T>(method, args);
	}

	CacheMetaData<T> getMetaData() {
		return mdata;
	}

	void setTimestamp(long timestamp) {
		mdata.setTimestamp(timestamp);
	}

	@Override
	public void setTotalCount(int totalCount) {
		mdata.setCount(totalCount);
		super.setTotalCount(totalCount);
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof CachePageList) {
			@SuppressWarnings("unchecked")
			CachePageList<T> cl = (CachePageList<T>) other;

			return mdata.equals(cl.mdata);
		}

		return super.equals(other);
	}
}
