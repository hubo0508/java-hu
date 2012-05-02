package pool;

import util.PropertiesUtil;

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

		PropertiesUtil proUtil = PropertiesUtil.getInstance();
		String path = "D:\\work\\myeclipse6.6\\JdbcUtils\\src.test\\jdbc.properties";

		String poolName = proUtil.getProperty(path, "oracle.jdbc.poolName");
		String driver = proUtil.getProperty(path, "oracle.jdbc.driver");
		String URL = proUtil.getProperty(path, "oracle.jdbc.url");
		String username = proUtil.getProperty(path, "oracle.jdbc.username");
		String password = proUtil.getProperty(path, "oracle.jdbc.password");
		int maxCon = Integer.parseInt(proUtil.getProperty(path,
				"oracle.jdbc.maxConn"));

		getInstance().setPoolName(poolName);
		getInstance().setDriver(driver);
		getInstance().setUrl(URL);
		getInstance().setUser(username);
		getInstance().setPassword(password);
		getInstance().setMaxConn(maxCon);
	}
}
