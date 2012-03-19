/**
 * @Project: TianYiAssistant
 * @Title: Page.java
 * @Package com.cdsf.assistant.core
 * @Description: TODO
 * @author HUBO hubo.0508@gmail.com  
 * @date 2011-5-1 下午07:25:56
 * @Copyright: 2011 biiawy Inc. All rights reserved.
 * @version V1.0  
 */
package com.util;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Page
 * @Description: TODO
 * @author HUBO hubo.0508@gmail.com
 * @date 2011-5-1 下午07:25:56
 * 
 */
public class Page<T> {

	// 公共变量 //
	public static final String ASC = "asc";
	public static final String DESC = "desc";

	private Integer limit;// 每页显示行数
	private Integer start;// 每页起始行

	private Integer pageSize;// 每页显示行数

	private Integer currentPage = 0;// 当前页

	private long totalPage;// 总页数

	private long totalCount;// 总记录数

	private Integer pageFirst = 1;// 首頁
	private Integer pagePrev;// 上一頁
	private Integer pageNext;// 下一頁
	private Integer pageLast;// 末頁

	// 返回结果 //
	private List<T> result = new ArrayList<T>();
	
	public Object obj;

	public Page() {
		super();
	}

	/**
	 * @param @param pageSize 每显显示记录数
	 * @param @param totalCount 总记录数
	 * @param @param result 查询结果
	 */
	public Page(Integer pageSize, long totalCount, Integer currentPage, List<T> result) {
		super();
		this.pageSize = pageSize;
		this.totalCount = totalCount;
		this.result = result;
		this.currentPage = currentPage;

		this.totalPage = this.getTotalPages();// 共多少页
		//this.currentPage = this.getCurrentPages();// 当前页

		this.setPagePrev(currentPage > 1 ? currentPage - 1 : currentPage);
		this.setPageNext(currentPage < totalPage ? currentPage + 1
				: currentPage);
		this.setPageLast(Integer.valueOf(totalPage + ""));
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

	public Integer getPageSize() {
		return pageSize;
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void setCurrentPage(Integer currentPage) {

		this.currentPage = currentPage;
	}

	public Integer getCurrentPages() {
		return currentPage < totalPage ? currentPage + 1 : currentPage;
	}

	public Integer getLimit() {
		return limit;
	}

	public Integer getStart() {
		return start;
	}

	public long getTotalCount() {
		return totalCount;
	}

	public List<T> getResult() {
		return result;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public void setTotalCount(long totalCount) {
		this.totalCount = totalCount;
	}

	public void setResult(List<T> result) {
		this.result = result;
	}

	public long getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(long totalPage) {

		if (currentPage > totalPage) {
			currentPage = Integer.valueOf(totalPage + "");
		}

		if (currentPage == totalPage) {
			currentPage = 1;
		}

		this.totalPage = totalPage;
	}

	public Integer getPageFirst() {
		return pageFirst;
	}

	public Integer getPagePrev() {
		return pagePrev;
	}

	public Integer getPageNext() {
		return pageNext;
	}

	public Integer getPageLast() {
		return pageLast;
	}

	public void setPageFirst(Integer pageFirst) {
		this.pageFirst = pageFirst;
	}

	public void setPagePrev(Integer pagePrev) {
		this.pagePrev = pagePrev;
	}

	public void setPageNext(Integer pageNext) {
		this.pageNext = pageNext;
	}

	public void setPageLast(Integer pageLast) {
		this.pageLast = pageLast;
	}

	/**
	 * 适用hibernate的query查询，起始行为0
	 * @return
	 */
	public Integer getStartRow(){
		return (currentPage - 1) * pageSize;
	}
}
