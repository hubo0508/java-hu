package com.easysql.core;

import com.easysql.handlers.EntityFilter;

public abstract class Entity extends ObjectManage implements
		java.io.Serializable {
	
	public static final String NOT_TAKE = "notTake";

	private static final long serialVersionUID = 1L;

	public abstract EntityFilter notTake();
}
