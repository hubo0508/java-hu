/**
 * @Project: citywork
 * @Package com.dream.citywork.core.ListResults.java
 * @date Oct 2, 2011 2:01:09 PM
 * @Copyright: 2011 HUBO Inc. All rights reserved.
 * @version V1.0  
 */
package com.dream.citywork.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @author HUBO
 */
public class ListResults<T> {

	/**
	 * 返回XML数据
	 */
	private String xml;

	/**
	 * 返回Object数据
	 */
	private Object obj;

	private Object entity;

	/**
	 * true，表示此次操作成功;false,表示失败
	 */
	private boolean success = true;

	/**
	 * 返回消息，成功或者失败的详细信息
	 */
	private String message;

	/**
	 * 结果总数
	 */
	private long totalSize;

	/**
	 * 当前页
	 */
	private int currentPage;

	/**
	 * 返回数据集合
	 */
	private List<T> list;

	public ListResults() {
		this.totalSize = 0;
		this.list = new ArrayList<T>();
	}

	public long getTotalSize() {
		return totalSize;
	}

	public void setTotalSize(long totalSize) {
		this.totalSize = totalSize;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public Object getEntity() {
		return entity;
	}

	public void setEntity(Object entity) {
		this.entity = entity;
	}
}
