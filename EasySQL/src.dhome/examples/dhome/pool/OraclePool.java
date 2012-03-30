package examples.dhome.pool;

import java.io.IOException;

import org.easysql.ReadProperties;


/**
 * 
 * <p>
 * Oracle9i��ݿ�l�ӳ�
 * </p>
 * 
 * @User: HUBO
 * @Date Feb 29, 2012
 * @Time 9:53:03 PM
 */
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

					log
							.info("*****************����OraclePoolʵ��*****************");
				}
			}
		}
		return pool;
	}

	private static void initProperties() throws IOException {

		ReadProperties proUtil = ReadProperties.getInstance();

		String path = "D:\\work\\myeclipse6.6\\EasySQL\\src.dhome\\jdbc.properties";

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
