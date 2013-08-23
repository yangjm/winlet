package com.aggrepoint.dao;

import java.util.List;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 *
 */
public class PageList<T> {
	private List<T> list;
	private int totalPage;
	private int totalCount;
	private int pageSize;
	private int currentPage;

	public List<T> getList() {
		return list;
	}

	@SuppressWarnings("unchecked")
	protected void setList(List<?> list) {
		this.list = (List<T>)list;
	}

	public int getTotalPage() {
		return totalPage;
	}

	protected void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	protected void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	protected void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	protected void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
}
