package com.easysql.core;


public abstract class Entity extends ObjectManage implements
		java.io.Serializable {

	private static final long serialVersionUID = 1L;

	protected SqlMap map = new SqlMap();

	public abstract SqlMap notTake();

	public SqlMap getMap() {
		return map;
	}

	public void setMap(SqlMap map) {
		this.map = map;
	}

}
