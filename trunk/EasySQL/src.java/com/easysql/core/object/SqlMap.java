package com.easysql.core.object;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class SqlMap extends HashMap implements Map {

	private static final long serialVersionUID = -6311099281884071158L;

	public Object notTask(String key) {
		return super.put(key, false);
	}

	public Object task(String key) {
		return super.put(key, true);
	}
}
