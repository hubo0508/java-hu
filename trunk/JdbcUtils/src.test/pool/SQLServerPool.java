package pool;

import org.db.jdbcutils.utils.PropertiesFile;

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

		String path = "D:\\work\\myeclipse6.6\\JdbcUtils\\src.test\\jdbc.properties";
		PropertiesFile proUtil = PropertiesFile.getInstance(path);

		String poolName = proUtil.getValue("sqlserver.jdbc.poolName");
		String driver = proUtil.getValue("sqlserver.jdbc.driver");
		String URL = proUtil.getValue("sqlserver.jdbc.url");
		String username = proUtil.getValue("sqlserver.jdbc.username");
		String password = proUtil.getValue("sqlserver.jdbc.password");
		int maxCon = Integer.parseInt(proUtil
				.getValue("sqlserver.jdbc.maxConn"));

		getInstance().setPoolName(poolName);
		getInstance().setDriver(driver);
		getInstance().setUrl(URL);
		getInstance().setUser(username);
		getInstance().setPassword(password);
		getInstance().setMaxConn(maxCon);
	}
}
