package pool;

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
 * Oracle9i数据库连接池
 * </p>
 * 
 * @User: HUBO
 * @Date Feb 29, 2012
 * @Time 9:40:03 PM
 */
public class DBPool {

	protected static Logger log = Logger.getLogger(DBPool.class);

	/**
	 * 连接接口
	 */
	private Connection con = null;

	/**
	 * 使用的连接数
	 */
	private int inUsed = 0;

	/**
	 * 容器，空闲连接
	 */
	private ArrayList freeConnections = new ArrayList();

	/**
	 * 最大连接
	 */
	private int maxConn;

	/**
	 * 连接池名字
	 */
	private String poolName;

	/**
	 * 密码
	 */
	private String password;

	/**
	 * 数据库连接地址
	 */
	private String url;

	/**
	 * 驱动
	 */
	private String driver;

	/**
	 * 用户名
	 */
	private String user;

	/**
	 * 定时
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
	 * 用完，释放连接
	 */
	public synchronized void freeConnection(Connection con) {

		try {

			if (!con.isClosed() && maxConn >= inUsed
					&& maxConn >= freeConnections.size()) {
				freeConnections.add(con);// 添加到空闲连接的末尾
				inUsed = inUsed-- <= 0 ? 0 : inUsed--;
				writeLog("释放");
			} else {
				log.debug("关闭连接"+poolName);
				inUsed = inUsed-- <= 0 ? 0 : inUsed--;
				con.close();
			}

		} catch (SQLException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 
	 * 从连接池里得到连接
	 * 
	 * @return
	 */
	public synchronized Connection getConnection() {

//		int count = 0;
//		Connection con = null;
//
//		if (this.freeConnections.size() > 0 && inUsed <= maxConn) {
//
//			con = (Connection) this.freeConnections.get(0);
//			this.freeConnections.remove(0);// 如果连接分配出去了，就从空闲连接里删除
//
//			if (con == null && count < freeConnections.size()) {
//				con = getConnection();
//				count++;
//			}
//
//		} else {
//			con = newConnection();
//		}
//
//		if (con != null) {
//			this.inUsed++;
//			writeLog("得到");
//		}
//
//		return con;
		
		return newConnection();
	}

	/**
	 * 释放全部连接
	 */
	public synchronized void release() {

		Iterator allConns = this.freeConnections.iterator();
		while (allConns.hasNext()) {
			Connection con = (Connection) allConns.next();
			try {
				con.close();
			} catch (SQLException e) {
				log.error("释放全部连接异常 : " + e.getMessage(), e);
			}
		}
		this.freeConnections.clear();
	}

	/**
	 * 创建新连接
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
			log.error("sorry can't create Connection! : " + e1.getMessage(), e1);
		}

		return con;

	}

	private void writeLog(String message) {
		log.debug(message + poolName + "连接，现有" + freeConnections.size()
				+ "个连接空闲，有" + inUsed + "个连接正在使用");
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

	public static void close(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
