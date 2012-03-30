package org.easysql.core;

import org.easysql.handlers.EntityFilter;

public abstract class Entity extends ObjectManage implements
		java.io.Serializable {
	
	public static final String FILTER = "filter";

	private static final long serialVersionUID = 1L;

	public abstract EntityFilter filter();
}
