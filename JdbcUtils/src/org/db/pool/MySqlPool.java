package org.db.pool;

import util.PropertiesUtil;

public class MySqlPool extends DBPool {

	private static DBPool pool = null;

	static {
		log.info("Setting up data source.");
		setupProperties();
		log.info("Done.");
	}

	private static void setupProperties() {

		PropertiesUtil p = PropertiesUtil.getInstance();
		String path = "D:\\work\\myeclipse6.6\\JdbcUtils\\src.test\\jdbc.properties";

		String username = p.getProperty(path, "mysql.jdbc.username");
		String password = p.getProperty(path, "mysql.jdbc.password");
		String url = p.getProperty(path, "mysql.jdbc.url");
		String driverClassName = p.getProperty(path, "mysql.jdbc.driver");
		boolean defaultAutoCommit = Boolean.valueOf(
				p.getProperty(path, "mysql.jdbc.defaultAutoCommit"))
				.booleanValue();
		int initialSize = Integer.valueOf(
				p.getProperty(path, "mysql.jdbc.initialSize")).intValue();
		int maxActive = Integer.valueOf(
				p.getProperty(path, "mysql.jdbc.maxActive")).intValue();
		int maxIdle = Integer
				.valueOf(p.getProperty(path, "mysql.jdbc.maxIdle")).intValue();
		int minIdle = Integer
				.valueOf(p.getProperty(path, "mysql.jdbc.minIdle")).intValue();
		int maxWait = Integer
				.valueOf(p.getProperty(path, "mysql.jdbc.maxWait")).intValue();
		boolean poolPreparedStatements = Boolean.valueOf(
				p.getProperty(path, "mysql.jdbc.poolPreparedStatements"))
				.booleanValue();
		int maxOpenPreparedStatements = Integer.valueOf(
				p.getProperty(path, "mysql.jdbc.maxOpenPreparedStatements"))
				.intValue();
		int timeBetweenEvictionRunsMillis = Integer
				.valueOf(
						p.getProperty(path,
								"mysql.jdbc.timeBetweenEvictionRunsMillis"))
				.intValue();
		boolean removeAbandoned = Boolean.valueOf(
				p.getProperty(path, "mysql.jdbc.removeAbandoned"))
				.booleanValue();
		int removeAbandonedTimeout = Integer.valueOf(
				p.getProperty(path, "mysql.jdbc.removeAbandonedTimeout"))
				.intValue();
		boolean logAbandoned = Boolean.valueOf(
				p.getProperty(path, "mysql.jdbc.logAbandoned")).booleanValue();

		if (pool == null) {
			getInstance();
		}

		pool.setUsername(username);
		pool.setPassword(password);
		pool.setUrl(url);
		pool.setDriverClassName(driverClassName);
		pool.setDefaultAutoCommit(defaultAutoCommit);
		pool.setInitialSize(initialSize);
		pool.setMaxActive(maxActive);
		pool.setMaxIdle(maxIdle);
		pool.setMinIdle(minIdle);
		pool.setMaxWait(maxWait);
		pool.setPoolPreparedStatements(poolPreparedStatements);
		pool.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
		pool.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
		pool.setRemoveAbandoned(removeAbandoned);
		pool.setRemoveAbandonedTimeout(removeAbandonedTimeout);
		pool.setLogAbandoned(logAbandoned);
	}

	public static DBPool getInstance() {
		synchronized (initLock) {
			if (pool == null) {
				pool = new MySqlPool();
			}
		}

		return pool;
	}
}
