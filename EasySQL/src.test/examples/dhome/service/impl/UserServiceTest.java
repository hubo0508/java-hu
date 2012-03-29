package examples.dhome.service.impl;

import java.sql.Connection;
import java.sql.SQLException;

import com.easysql.core.Entity;
import com.easysql.handlers.EntityHandler;
import com.easysql.handlers.SQLHandler;
import com.easysql.jdbc.JdbcRunner;

import examples.BaseTest;
import examples.dhome.domain.User;
import examples.dhome.pool.DBPool;
import examples.dhome.pool.MySqlPool;

@SuppressWarnings("unused")
public class UserServiceTest extends BaseTest {

	public static void main(String[] args) {
		UserServiceTest test = new UserServiceTest();
		test.save();
	}

	@SuppressWarnings("unchecked")
	public void save() {

		DBPool pool = MySqlPool.getInstance();
		Connection con = pool.getConnection();

		try {
			User u = new User();
			u.setUsername("hubo-test-3");
			u.setPassword("hubo-password");

			EntityHandler eHandler = new EntityHandler(User.class);
			String sql = SQLHandler.getInsertSQL(eHandler);
			Object[] params = SQLHandler
					.entityConvertedObjectArray(eHandler, u);

			System.out.println(sql);

			int i = new JdbcRunner().update(con, sql, params);

			System.out.println(i);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pool.release();
		}
	}

}
