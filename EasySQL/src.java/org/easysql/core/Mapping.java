package org.easysql.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unchecked")
public class Mapping {

	private static Map<String, Object> map = new HashMap<String, Object>();

	private static final long serialVersionUID = -6311099281884071158L;

	private static Object initLock = new Object();

	private static Mapping sqlMap;

	public Mapping() {

	}

	/**
	 * 取得全局配置文件对象
	 */
	public static Mapping getInstance() {
		if (sqlMap == null) {
			synchronized (initLock) {
				sqlMap = new Mapping();
			}
		}
		return sqlMap;
	}

	public Map getSqlMap() {
		return map;
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

}
