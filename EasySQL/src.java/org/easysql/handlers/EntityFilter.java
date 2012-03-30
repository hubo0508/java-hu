package org.easysql.handlers;

import java.util.HashMap;

/**
 * POJO过滤器
 */
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
	
	/**
	 * 默认构造ID
	 */
	public EntityFilter() {
		super.put(ID, "id");
		super.put("serialVersionUID", false);
	}

	/**
	 * 在生成SQL时，该字段不作显示
	 */
	public Object notTask(String key) {
		return this.put(key, false);
	}

	/**
	 * 在生成SQL时，该字段显示
	 */
	public Object task(String key) {
		return this.put(key, true);
	}
}
