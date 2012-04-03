package org.easysql.handlers;

/**
 * 不同数据库兼容处理工具类
 */
public class CompatibleHandler {

	/**
	 * 数据库类型
	 */
	private String database;

	/**
	 * 主键生成机制
	 */
	private String generator;

	public CompatibleHandler() {

	}

	public CompatibleHandler(String database) {
		super();
		this.database = database;
	}

	public CompatibleHandler(String database, String generator) {
		super();
		this.database = database;
		this.generator = generator;
	}

	/**
	 * 数据库类型
	 */
	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	/**
	 * 主键生成机制
	 */
	public String getGenerator() {
		return generator;
	}

	public void setGenerator(String generator) {
		this.generator = generator;
	}

}
