package pool;

import org.db.jdbcutils.PropertiesFile;


public class MySqlPool extends DBPool {

	private static class Instance {
		public static final MySqlPool pool = new MySqlPool();
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

		PropertiesFile proUtil = PropertiesFile.getInstance();
		String path = "D:\\work\\myeclipse6.6\\JdbcUtils\\src.test\\jdbc.properties";

		String driver = proUtil.getProperty(path, "mysql.jdbc.driver");
		String URL = proUtil.getProperty(path, "mysql.jdbc.url");
		String username = proUtil.getProperty(path, "mysql.jdbc.username");
		String password = proUtil.getProperty(path, "mysql.jdbc.password");

		getInstance().setDriver(driver);
		getInstance().setUrl(URL);
		getInstance().setUser(username);
		getInstance().setPassword(password);
	}
}
