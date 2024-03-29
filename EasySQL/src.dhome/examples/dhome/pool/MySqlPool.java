package examples.dhome.pool;

import java.io.IOException;

import org.easysql.ReadProperties;


public class MySqlPool extends DBPool {

	private static DBPool pool = null;

	public MySqlPool() {

	}

	public static DBPool getInstance() {
		if (pool == null) {
			synchronized (initLock) {
				if (pool == null) {
					pool = new DBPool();

					try {
						initProperties();
					} catch (IOException e) {
						e.printStackTrace();
					}
					log.info("******开启MySqlPool实例******");
				}
			}
		}
		return pool;
	}

	private static void initProperties() throws IOException {

		ReadProperties proUtil = ReadProperties.getInstance();
		String path = "D:\\work\\myeclipse6.6\\EasySQL\\src.dhome\\jdbc.properties";

		String poolName = proUtil.getProperty(path, "mysql.jdbc.poolName");
		String driver = proUtil.getProperty(path, "mysql.jdbc.driver");
		String URL = proUtil.getProperty(path, "mysql.jdbc.url");
		String username = proUtil.getProperty(path, "mysql.jdbc.username");
		String password = proUtil.getProperty(path, "mysql.jdbc.password");
		int maxCon = Integer.parseInt(proUtil.getProperty(path,
				"mysql.jdbc.maxConn"));

		pool.setPoolName(poolName);
		pool.setDriver(driver);
		pool.setUrl(URL);
		pool.setUser(username);
		pool.setPassword(password);
		pool.setMaxConn(maxCon);
	}
}
