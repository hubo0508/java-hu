package org.db.pool;

import java.sql.*;

public class SqlServerConnectionTest {

	// dataSource = (BasicDataSource)BasicDataSourceFactory.createDataSource(p);
	public static void main(String[] args) {

		DBPool pool = SqlServerPool.getInstance();
		pool.printDataSourceStats();

		try {
			pool.shutdownDataSource();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		Connection conn = null;
		Statement stmt = null;
		ResultSet rset = null;
		try {
			conn = pool.getConnection();
			stmt = conn.createStatement();
			rset = stmt.executeQuery("SELECT * FROM nhwm_config_device");
			int numcols = rset.getMetaData().getColumnCount();
			while (rset.next()) {
				for (int i = 1; i <= numcols; i++) {
					System.out.print("\t" + rset.getString(i));
				}
				System.out.println("");
			}
			pool.printDataSourceStats();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			pool.close(rset, stmt, conn);
			pool.freeConnection(conn);
		}
	}
}
