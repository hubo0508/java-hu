package org.db.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;

public class DBPool extends BasicDataSource {

	protected static Logger log = Logger.getLogger(DBPool.class);

	protected static Object initLock = new Object();

	public DBPool() {
		super();
	}

	public void printDataSourceStats() {
		StringBuffer writeLog = new StringBuffer();
//		writeLog.append("\ndriverClassName：" + getDriverClassName());
		//writeLog.append("\ndefaultAutoCommit：" + getDefaultAutoCommit());
//		writeLog.append("\ninitialSize：" + getInitialSize());
		writeLog.append(" maxIdle：" + getMaxIdle());
		writeLog.append(" minIdle：" + getMinIdle());
//		writeLog.append("\nmaxWait：" + getMaxWait());
//		writeLog.append("\npoolPreparedStatements：" + poolPreparedStatements);
//		writeLog.append("\nmaxOpenPreparedStatements："
//				+ getMaxOpenPreparedStatements());
//		writeLog.append("\ntimeBetweenEvictionRunsMillis："
//				+ getTimeBetweenEvictionRunsMillis());
//		writeLog.append("\nremoveAbandoned：" + getRemoveAbandoned());
//		writeLog.append("\nremoveAbandonedTimeout："
//				+ getRemoveAbandonedTimeout());
//		writeLog.append("\nlogAbandoned：" + getLogAbandoned());

		log.info(writeLog.toString());
	}

	public void shutdownDataSource() throws SQLException {
		close();
	}

	public void close(ResultSet rset) {
		try {
			if (rset != null)
				rset.close();
		} catch (Exception e) {
		}
	}

	public void close(Statement stmt) {
		try {
			if (stmt != null)
				stmt.close();
		} catch (Exception e) {
		}
	}

	public void close(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (Exception e) {
		}
	}

	public void close(ResultSet rset, Statement stmt, Connection conn) {
		this.close(rset);
		this.close(stmt);
		this.close(conn);
	}

	public void freeConnection(Connection conn) {
		this.close(conn);
	}

}
