package org.easysql.handlers;

import org.easysql.EasySQL;

/**
 * 不同数据库兼容处理工具类
 */
public class CompatibleHandler {

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
	private String database;

	/**
	 * 主键生成机制
	 */
	private String generator;

	/**
	 * 取得主键生成机制</br></br>
	 * 
	 * 1、根据EasySQL.xml中的配置信息，判断主键的生成规则，是自动生成还是手动维护主键。</br>
	 * 
	 * @param databasename
	 *            数据库类型(mysql、sqlserver、oracle)
	 * 
	 * @return true:主键生成机制为自动 || false:主键生成机制为手动维护
	 * 
	 */
	public boolean getPrimaryKeyMechanism(String databasename, String generator) {

		if (EasySQL.ORACLE.equals(databasename)) {
			if (EasySQL.NATIVE.equals(generator)
					|| EasySQL.NATIVE.equals(generator)) {
				return true;
			}
		}

		if (EasySQL.SQLSERVICE.equals(databasename)) {

		}

		if (EasySQL.MYSQL.equals(databasename)) {
			if (EasySQL.NATIVE.equals(generator)
					|| EasySQL.IDENTITY.equals(generator)) {
				return true;
			}
		}

		return false;
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
