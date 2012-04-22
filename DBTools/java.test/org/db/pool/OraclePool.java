package org.db.pool;

import java.io.IOException;

public class OraclePool extends DBPool {

	private static DBPool pool = null;

	public OraclePool() {

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

					log.info("***********OraclePool************");
				}
			}
		}
		return pool;
	}

	private static void initProperties() throws IOException {

		PropertiesUtil proUtil = PropertiesUtil.getInstance();

		// String path = Path.getFullPathRelateClass(Const.PATH_PRO
		// + "jdbc.properties", Path.class);
		String path = "D:\\work\\myeclipse6.6\\NHWM\\properties\\jdbc.properties";

		// String path = Const.JDBC;

		String poolName = proUtil.getProperty(path, "oracle.jdbc.poolName");
		String driver = proUtil.getProperty(path, "oracle.jdbc.driver");
		String URL = proUtil.getProperty(path, "oracle.jdbc.url");
		String username = proUtil.getProperty(path, "oracle.jdbc.username");
		String password = proUtil.getProperty(path, "oracle.jdbc.password");
		int maxCon = Integer.parseInt(proUtil.getProperty(path,
				"oracle.jdbc.maxConn"));

		pool.setPoolName(poolName);
		pool.setDriver(driver);
		pool.setUrl(URL);
		pool.setUser(username);
		pool.setPassword(password);
		pool.setMaxConn(maxCon);
	}

	public static void main(String[] args) throws IOException {
		initProperties();
	}
}
