package com.easysql.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ObjectManage {

	protected Log log = LogFactory.getLog(ObjectManage.class);

	private String canonicalName;

	private Class<?> clazz;

	public ObjectManage() {
		super();
	}

	@SuppressWarnings("unchecked")
	public ObjectManage(String canonicalName) {
		super();
		this.canonicalName = canonicalName;
	}

	public String getCanonicalName() {
		return canonicalName;
	}

	public void setCanonicalName(String canonicalName) {
		this.canonicalName = canonicalName;
	}

	public Class<?> getClazz() {
		return clazz;
	}

	public void setClazz(Class<?> clazz) {
		this.clazz = clazz;
	}

}
