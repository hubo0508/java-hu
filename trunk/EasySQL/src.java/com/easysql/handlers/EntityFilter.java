package com.easysql.handlers;

import java.util.HashMap;

public class EntityFilter extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	/**
	 * 替换
	 */
	public static final String REPLACE = "replace-mapHandler";

	/**
	 * 当数据库使用Oracle时，可对每个单独的表设置序列
	 */
	public static final String GENERATOR_SEQ_VALUE = "generator-mapHandler";

	/**
	 * ID
	 */
	public static final String ID = "id-mapHandler";

	public EntityFilter() { 
		super.put(ID, "id");
		super.put("serialVersionUID", false);
	}

	public Object notTask(String key) {
		return this.put(key, false);
	}

	public Object task(String key) {
		return this.put(key, true);
	}
}
