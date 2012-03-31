package org.easysql.core;

import java.util.ArrayList;
import java.util.List;

public class Page<T extends java.io.Serializable> {

	/**
	 * 每页显示行数
	 */
	private int limit;

	/**
	 * 每页起始行
	 */
	private int start;

	/**
	 * 每页显示行数
	 */
	public static final int pageSize = 10;

	/**
	 * 当前页
	 */
	private int currentPage = 0;

	/**
	 * 总页数
	 */
	private long totalPage;

	/**
	 * 总记录数
	 */
	private long totalCount;

	/** ************************************************************************ */

	/**
	 * 首頁
	 */
	private int pageFirst = 1;

	/**
	 * 上一頁
	 */
	private int pagePrev;

	/**
	 * 下一頁
	 */
	private int pageNext;

	/**
	 * 末頁
	 */
	private int pageLast;

	/** ************************************************************************ */

	// 返回结果 //
	private List<T> result = new ArrayList<T>();

	public Page() {

	}

	/**
	 * @User: HUBO
	 * 
	 * @param pageSize
	 *            每显显示记录数
	 * @param totalCount
	 *            总记录数
	 * @param result
	 *            查询结果
	 */
	public Page(long totalCount, int currentPage, List<T> result) {
		this.totalCount = totalCount;
		this.result = result;
		this.currentPage = currentPage;

		this.totalPage = this.getTotalPages();// 共多少页

		this.setPagePrev(currentPage > 1 ? currentPage - 1 : currentPage);
		this.setPageNext(currentPage < totalPage ? currentPage + 1
				: currentPage);
		this.setPageLast(Integer.parseInt((totalPage + "")));
	}

	/**
	 * 根据pageSize与totalCount计算总页数, 默认值为-1.
	 */
	public long getTotalPages() {

		if (totalCount < 0)
			return -1;

		long count = totalCount / pageSize;
		if (totalCount % pageSize > 0) {
			count++;
		}
		return count;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public long getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(long totalPage) {
		this.totalPage = totalPage;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageFirst() {
		return pageFirst;
	}

	public void setPageFirst(int pageFirst) {
		this.pageFirst = pageFirst;
	}

	public int getPagePrev() {
		return pagePrev;
	}

	public void setPagePrev(int pagePrev) {
		this.pagePrev = pagePrev;
	}

	public int getPageNext() {
		return pageNext;
	}

	public void setPageNext(int pageNext) {
		this.pageNext = pageNext;
	}

	public int getPageLast() {
		return pageLast;
	}

	public void setPageLast(int pageLast) {
		this.pageLast = pageLast;
	}

	public List<T> getResult() {
		return result;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}
}
