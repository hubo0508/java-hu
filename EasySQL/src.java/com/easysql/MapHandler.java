package com.easysql;

import java.util.HashMap;

public class MapHandler extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	
	public static final String REPLACE = "replace-mapHandler";
	
	public static final String GENERATOR = "generator-mapHandler";

	public Object notTask(String key) {
		return this.put(key, false);
	}

	public Object task(String key) {
		return this.put(key, true);
	}
}
