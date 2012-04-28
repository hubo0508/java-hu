package pool;

import java.io.IOException;

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

	private static DBPool pool = null;

	public SQLServerPool() {

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

					log
							.info("*****************开启SQLServerPool实例*****************");
				}
			}
		}
		return pool;
	}

	private static void initProperties() throws IOException {

		PropertiesUtil proUtil = PropertiesUtil.getInstance();
		String path = "D:\\work\\myeclipse6.6\\JdbcUtils\\src.test\\jdbc.properties";

		String poolName = proUtil.getProperty(path, "sqlserver.jdbc.poolName");
		String driver = proUtil.getProperty(path, "sqlserver.jdbc.driver");
		String URL = proUtil.getProperty(path, "sqlserver.jdbc.url");
		String username = proUtil.getProperty(path, "sqlserver.jdbc.username");
		String password = proUtil.getProperty(path, "sqlserver.jdbc.password");
		int maxCon = Integer.parseInt(proUtil.getProperty(path,
				"sqlserver.jdbc.maxConn"));

		pool.setPoolName(poolName);
		pool.setDriver(driver);
		pool.setUrl(URL);
		pool.setUser(username);
		pool.setPassword(password);
		pool.setMaxConn(maxCon);
	}
}
