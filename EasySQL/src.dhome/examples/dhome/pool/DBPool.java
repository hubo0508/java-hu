package examples.dhome.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;

import org.apache.log4j.Logger;

/**
 * 
 * <p>
 * Oracle9i��ݿ�l�ӳ�
 * </p>
 * 
 * @User: HUBO
 * @Date Feb 29, 2012
 * @Time 9:40:03 PM
 */
public class DBPool {

	protected static Logger log = Logger.getLogger(DBPool.class);

	protected static Object initLock = new Object();

	/**
	 * l�ӽӿ�
	 */
	private Connection con = null;

	/**
	 * ʹ�õ�l����
	 */
	private int inUsed = 0;

	/**
	 * �������l��
	 */
	private ArrayList<Connection> freeConnections = new ArrayList<Connection>();

	/**
	 * ���l��
	 */
	private int maxConn;

	/**
	 * l�ӳ�����
	 */
	private String poolName;

	/**
	 * ����
	 */
	private String password;

	/**
	 * ��ݿ�l�ӵ�ַ
	 */
	private String url;

	/**
	 * ��
	 */
	private String driver;

	/**
	 * �û���
	 */
	private String user;

	/**
	 * ��ʱ
	 */
	public Timer timer;

	public DBPool() {
	}

	public DBPool(String poolName, String driver, String URL, String user,
			String password, int maxConn) {
		this.poolName = poolName;
		this.driver = driver;
		this.url = URL;
		this.user = user;
		this.password = password;
		this.maxConn = maxConn;
	}

	/**
	 * ���꣬�ͷ�l��
	 */
	public synchronized void freeConnection(Connection con) {

		try {

			if (!con.isClosed() && maxConn >= inUsed
					&& maxConn >= freeConnections.size()) {
				freeConnections.add(con);// ��ӵ�����l�ӵ�ĩβ
				inUsed = inUsed-- <= 0 ? 0 : inUsed--;
				writeLog("�ͷ�");
			} else {
				log.debug("�ر�l��" + poolName);
				inUsed = inUsed-- <= 0 ? 0 : inUsed--;
				con.close();
			}

		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * ��l�ӳ���õ�l��
	 * 
	 * @return
	 */
	public synchronized Connection getConnection() {

		int count = 0;
		Connection con = null;

		if (this.freeConnections.size() > 0 && inUsed <= maxConn) {

			con = (Connection) this.freeConnections.get(0);
			this.freeConnections.remove(0);// ���l�ӷ����ȥ�ˣ��ʹӿ���l����ɾ��

			if (con == null && count < freeConnections.size()) {
				con = getConnection();
				count++;
			}

		} else {
			con = newConnection();
		}

		if (con != null) {
			this.inUsed++;
			writeLog("�õ�");
		}

		return con;
	}

	/**
	 * �ͷ�ȫ��l��
	 */
	public synchronized void release() {

		Iterator<Connection> allConns = this.freeConnections.iterator();
		while (allConns.hasNext()) {
			Connection con = allConns.next();
			try {
				con.close();
			} catch (SQLException e) {
				log.error("�ͷ�ȫ��l���쳣 : " + e.getMessage(), e);
			}
		}
		this.freeConnections.clear();
	}

	/**
	 * ������l��
	 * 
	 * @return
	 */
	private Connection newConnection() {

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
		} catch (ClassNotFoundException e) {
			log.error("sorry can't find db driver! : " + e.getMessage(), e);
		} catch (SQLException e1) {
			log
					.error("sorry can't create Connection! : "
							+ e1.getMessage(), e1);
		}

		return con;

	}

	private void writeLog(String message) {
		log.debug(message + poolName + "l�ӣ�����" + freeConnections.size()
				+ "��l�ӿ��У���" + inUsed + "��l������ʹ��");
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public int getMaxConn() {
		return maxConn;
	}

	public void setMaxConn(int maxConn) {
		this.maxConn = maxConn;
	}

	public String getPoolName() {
		return poolName;
	}

	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
