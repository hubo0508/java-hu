package examples.dhome.service.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.ArrayListHandler;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.easysql.core.Entity;
import org.easysql.core.Page;
import org.easysql.core.SQLResult;
import org.easysql.handlers.EntityHandler;
import org.easysql.handlers.SQLHandler;

import examples.BaseTest;
import examples.dhome.domain.User;
import examples.dhome.pool.DBPool;
import examples.dhome.pool.MySqlPool;

@SuppressWarnings("unused")
public class UserServiceTest extends BaseTest {

	public static void main(String[] args) {

		User u = new User();
		u.setId(7);
		u.setUsername("hubo-te-----");
		u.setPassword("hubo-password");

		UserServiceTest test = new UserServiceTest();
		// test.save(u);
		// test.update(u);
		test.selectUser();
	}

	@SuppressWarnings("unchecked")
	public void selectUser() {
		DBPool pool = MySqlPool.getInstance();
		Connection con = pool.getConnection();
		try {

			SQLHandler sqlHandler = new SQLHandler(
					new EntityHandler(User.class));
			String sql = sqlHandler.getSelectSQL();
			// sql = sqlHandler.getPagingSQL(sql);

			Object list = new QueryRunner(new Page(1, 2)).query(con, sql,
					new BeanListHandler(User.class));

			Page page = (Page) list;
			List<User> listuser = (List<User>) page.getResult();

			for (User user : listuser) {
				System.out.println(user.getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println(con == null);
			pool.release();
		}
	}

	public void update(User u) {

		DBPool pool = MySqlPool.getInstance();
		try {

			SQLHandler sqlHandler = new SQLHandler(
					new EntityHandler(User.class));
			String sql = sqlHandler.getUpdateSQL();
			Object[] params = sqlHandler.objectArray(u, sql);

			System.out.println(sql);

			int i = new QueryRunner().update(pool.getConnection(), sql, params);

			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

	@SuppressWarnings("unchecked")
	public void save(User u) {

		DBPool pool = MySqlPool.getInstance();

		try {
			SQLHandler sqlHandler = new SQLHandler(
					new EntityHandler(User.class));
			String sql = sqlHandler.getInsertSQL();
			Object[] params = sqlHandler.objectArray(u);

			System.out.println(sql);

			int i = new QueryRunner().update(pool.getConnection(), sql, params);

			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

}
