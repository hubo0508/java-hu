package com.easysql.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@SuppressWarnings("unchecked")
public class JdbcRunner<T, F extends java.io.Serializable> extends
		AbstractJdbcRunner {

	private Connection conn;

	public JdbcRunner() {

	}

	public JdbcRunner(Connection conn) {
		this.conn = conn;
	}

	public int update(Connection con, String sql, Object[] params)
			throws SQLException {

		if (con == null) {
			throw new SQLException("Null connection");
		}

		if (sql == null) {
			throw new SQLException("Null SQL statement");
		}

		PreparedStatement stmt = null;
		int rows = 0;

		try {
			stmt = this.prepareStatement(con, sql);
			this.fillStatement(stmt, params);
			rows = stmt.executeUpdate();

		} catch (SQLException e) {
			this.rethrow(e, sql, params);

		} finally {
			close(stmt);
			close(conn);
		}

		return rows;
	}

}
