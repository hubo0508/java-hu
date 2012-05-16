package pool;

import org.db.jdbcutils.utils.PropertiesFile;

public class OraclePool extends DBPool {

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

		String poolName = proUtil.getValue("oracle.jdbc.poolName");
		String driver = proUtil.getValue("oracle.jdbc.driver");
		String URL = proUtil.getValue("oracle.jdbc.url");
		String username = proUtil.getValue("oracle.jdbc.username");
		String password = proUtil.getValue("oracle.jdbc.password");
		int maxCon = Integer.parseInt(proUtil.getValue("oracle.jdbc.maxConn"));

		getInstance().setPoolName(poolName);
		getInstance().setDriver(driver);
		getInstance().setUrl(URL);
		getInstance().setUser(username);
		getInstance().setPassword(password);
		getInstance().setMaxConn(maxCon);
	}
}
