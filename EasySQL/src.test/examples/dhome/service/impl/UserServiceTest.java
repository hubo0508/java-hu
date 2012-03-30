package examples.dhome.service.impl;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
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
	}

	public void update(User u) {
		EntityHandler eHandler = new EntityHandler(User.class);
		String sql = SQLHandler.getUpdateSQL(eHandler);
		Object[] params = SQLHandler.objectArray(eHandler, u, sql);

		System.out.println(sql);
	}

	@SuppressWarnings("unchecked")
	public void save(User u) {

		DBPool pool = MySqlPool.getInstance();

		try {
			EntityHandler eHandler = new EntityHandler(User.class);
			String sql = SQLHandler.getInsertSQL(eHandler);
			Object[] params = SQLHandler.objectArray(eHandler, u);

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
