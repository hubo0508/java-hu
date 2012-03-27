package com.easysql.core.object;

import java.util.HashMap;

public class IfMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;

	public static final String REPLACE = "replace-ifmap";

	public Object notTask(String key) {
		return this.put(key, false);
	}

	public Object task(String key) {
		return this.put(key, true);
	}
}
