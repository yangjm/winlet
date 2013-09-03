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
	public void setList(List<?> list) {
		this.list = (List<T>) list;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
}
