package com.pool;

import java.io.IOException;

import com.util.Path;
import com.util.PropertiesUtil;

/**
 * 
 * <p>
 * SQLServer2000��ݿ�l�ӳ�
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
				}
			}
		}
		return pool;
	}

	private static void initProperties() throws IOException {

		PropertiesUtil proUtil = PropertiesUtil.getInstance();

		String path = Path.getFullPathRelateClass(
				"../../../../config/jdbc.properties", Path.class);
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
