package com.easysql.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


@SuppressWarnings("unchecked")
public class SqlMap {

	private static Map<String, Object> map = new HashMap<String, Object>();

	private static final long serialVersionUID = -6311099281884071158L;

	private static Object initLock = new Object();

	private static SqlMap sqlMap;

	public SqlMap() {
		// new NodeEngine().init();
	}

	public static SqlMap getInstance() {
		if (sqlMap == null) {
			synchronized (initLock) {
				sqlMap = new SqlMap();
			}
		}
		return sqlMap;
	}

	public void clear() {
		map.clear();
	}

	public boolean containsKey(Object key) {
		return map.containsKey(key);
	}

	public boolean containsValue(Object value) {
		return map.containsValue(value);
	}

	public Set entrySet() {
		return map.entrySet();
	}

	public Object get(Object key) {
		return map.get(key);
	}

	public boolean isEmpty() {
		return map.isEmpty();
	}

	public Set keySet() {
		return map.keySet();
	}

	public Object put(String key, Object value) {
		return map.put(key, value);
	}

	public void putAll(Map m) {
		map.putAll(m);
	}

	public Object remove(Object key) {
		return map.remove(key);
	}

	public int size() {
		return map.size();
	}

	public Collection values() {
		return map.values();
	}

	public Object notTask(String key) {
		return map.put(key, false);
	}

	public Object task(String key) {
		return map.put(key, true);
	}

}
