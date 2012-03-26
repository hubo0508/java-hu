package com.easysql.core;

import java.util.HashMap;

public class SqlMap extends HashMap<Object, Object> {

	private static final long serialVersionUID = -6311099281884071158L;

	public Object notTask(String key) {
		return super.put(key, false);
	}

	public Object task(String key) {
		return super.put(key, true);
	}
}
