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
		 test.update(u);
		//test.selectUser();
	}

	public void selectUser() {
		DBPool pool = MySqlPool.getInstance();
		try {

			SQLHandler sqlHandler = new SQLHandler(
					new EntityHandler(User.class));
			String sql = sqlHandler.getSelectSQL("select * from User");

			List<User> list = new QueryRunner().query(pool.getConnection(), sql, new BeanListHandler<User>(User.class));

			System.out.println(list.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
