package com.aggrepoint.dao;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Jiangming Yang (yangjm@gmail.com)
 * 
 */
public class PageList<T> implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<T> list;
	private int totalPage;
	private int totalCount;
	private int pageSize;
	private int currentPage;

	public PageList() {

	}

	public PageList(PageList<?> ref) {
		this.totalPage = ref.totalPage;
		this.totalCount = ref.totalCount;
		this.pageSize = ref.pageSize;
		this.currentPage = ref.currentPage;
	}

	public PageList(PageList<?> ref, List<T> list) {
		this.totalPage = ref.totalPage;
		this.totalCount = ref.totalCount;
		this.pageSize = ref.pageSize;
		this.currentPage = ref.currentPage;
		this.list = list;
	}

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
