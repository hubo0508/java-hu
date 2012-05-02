package pool;

import util.PropertiesUtil;

/**
 * 
 * <p>
 * SQLServer2000数据库连接池
 * </p>
 * 
 * @User: HUBO
 * @Date Feb 29, 2012
 * @Time 9:52:31 PM
 */
public class SQLServerPool extends DBPool {

	private static class Instance {
		public static final OraclePool pool = new OraclePool();
	}

	public static DBPool getInstance() {
		return Instance.pool;
	}

	static {
		log.info("Setting up data source.");
		setupProperties();
		log.info("Setting up data source done.");
	}

	private static void setupProperties() {

		PropertiesUtil proUtil = PropertiesUtil.getInstance();
		String path = "D:\\work\\myeclipse6.6\\JdbcUtils\\src.test\\jdbc.properties";

		String poolName = proUtil.getProperty(path, "sqlserver.jdbc.poolName");
		String driver = proUtil.getProperty(path, "sqlserver.jdbc.driver");
		String URL = proUtil.getProperty(path, "sqlserver.jdbc.url");
		String username = proUtil.getProperty(path, "sqlserver.jdbc.username");
		String password = proUtil.getProperty(path, "sqlserver.jdbc.password");
		int maxCon = Integer.parseInt(proUtil.getProperty(path,
				"sqlserver.jdbc.maxConn"));

		getInstance().setPoolName(poolName);
		getInstance().setDriver(driver);
		getInstance().setUrl(URL);
		getInstance().setUser(username);
		getInstance().setPassword(password);
		getInstance().setMaxConn(maxCon);
	}
}
