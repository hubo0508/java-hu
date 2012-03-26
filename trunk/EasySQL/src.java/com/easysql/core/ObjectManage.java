package com.easysql.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ObjectManage {

	protected Log log = LogFactory.getLog(ObjectManage.class);

	private String canonicalName;

	private Class<?> clazz;
	
	protected static Map<String, Boolean> collection = new HashMap<String, Boolean>();

	static {
		collection.put("interface java.util.List", true);
		collection.put("interface java.util.Map", true);
		collection.put("interface java.util.Set", true);
	}

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
