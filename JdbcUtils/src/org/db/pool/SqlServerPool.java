package org.db.pool;

import util.PropertiesUtil;

public class SqlServerPool extends DBPool {

	private static DBPool pool = null;

	static {
		log.info("Setting up data source.");
		setupProperties();
		log.info("Setting up data source done.");
	}

	private static void setupProperties() {

		PropertiesUtil p = PropertiesUtil.getInstance();
		String path = "D:\\work\\myeclipse6.6\\JdbcUtils\\src.test\\jdbc.properties";

		String username = p.getProperty(path, "sqlserver.jdbc.username");
		String password = p.getProperty(path, "sqlserver.jdbc.password");
		String url = p.getProperty(path, "sqlserver.jdbc.url");
		String driverClassName = p.getProperty(path, "sqlserver.jdbc.driver");
		boolean defaultAutoCommit = Boolean.valueOf(
				p.getProperty(path, "sqlserver.jdbc.defaultAutoCommit"))
				.booleanValue();
		int initialSize = Integer.valueOf(
				p.getProperty(path, "sqlserver.jdbc.initialSize")).intValue();
		int maxActive = Integer.valueOf(
				p.getProperty(path, "sqlserver.jdbc.maxActive")).intValue();
		int maxIdle = Integer.valueOf(
				p.getProperty(path, "sqlserver.jdbc.maxIdle")).intValue();
		int minIdle = Integer.valueOf(
				p.getProperty(path, "sqlserver.jdbc.minIdle")).intValue();
		int maxWait = Integer.valueOf(
				p.getProperty(path, "sqlserver.jdbc.maxWait")).intValue();
		boolean poolPreparedStatements = Boolean.valueOf(
				p.getProperty(path, "sqlserver.jdbc.poolPreparedStatements"))
				.booleanValue();
		int maxOpenPreparedStatements = Integer
				.valueOf(
						p.getProperty(path,
								"sqlserver.jdbc.maxOpenPreparedStatements"))
				.intValue();
		int timeBetweenEvictionRunsMillis = Integer.valueOf(
				p.getProperty(path,
						"sqlserver.jdbc.timeBetweenEvictionRunsMillis"))
				.intValue();
		boolean removeAbandoned = Boolean.valueOf(
				p.getProperty(path, "sqlserver.jdbc.removeAbandoned"))
				.booleanValue();
		int removeAbandonedTimeout = Integer.valueOf(
				p.getProperty(path, "sqlserver.jdbc.removeAbandonedTimeout"))
				.intValue();
		boolean logAbandoned = Boolean.valueOf(
				p.getProperty(path, "sqlserver.jdbc.logAbandoned"))
				.booleanValue();
		int numTestsPerEvictionRun = Integer.valueOf(
				p.getProperty(path, "sqlserver.jdbc.numTestsPerEvictionRun"))
				.intValue();
		boolean testWhileIdle = Boolean.valueOf(
				p.getProperty(path, "sqlserver.jdbc.testWhileIdle"))
				.booleanValue();
		boolean testOnBorrow = Boolean.valueOf(
				p.getProperty(path, "sqlserver.jdbc.testOnBorrow"))
				.booleanValue();
		boolean testOnReturn = Boolean.valueOf(
				p.getProperty(path, "sqlserver.jdbc.testOnReturn"))
				.booleanValue();
		String validationQuery = p.getProperty(path,
				"sqlserver.jdbc.validationQuery");

		if (pool == null) {
			getInstance();
		}
		
		pool.setValidationQuery(validationQuery);
		pool.setTestOnReturn(testOnReturn);
		pool.setTestOnBorrow(testOnBorrow);
		pool.setTestWhileIdle(testWhileIdle);
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
		pool.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
	}

	public static DBPool getInstance() {
		synchronized (initLock) {
			if (pool == null) {
				pool = new SqlServerPool();
			}
		}

		return pool;
	}
}
