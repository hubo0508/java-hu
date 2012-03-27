package com.easysql.handlers;

import java.util.HashMap;

public class EntityFilter extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	
	public static final String REPLACE = "replace-mapHandler";
	
	public static final String GENERATOR_SEQ_VALUE = "generator-mapHandler";

	public Object notTask(String key) {
		return this.put(key, false);
	}

	public Object task(String key) {
		return this.put(key, true);
	}
}
