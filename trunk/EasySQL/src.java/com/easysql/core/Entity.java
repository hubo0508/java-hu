package com.easysql.core;

import com.easysql.core.object.SqlMap;


public abstract class Entity extends ObjectManage implements
		java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	protected SqlMap map = new SqlMap();

	public abstract SqlMap notTake();

}
