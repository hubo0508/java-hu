package examples.dhome.service.impl;

import java.sql.Connection;
import java.sql.SQLException;
import com.easysql.handlers.EntityHandler;
import com.easysql.handlers.SQLHandler;
import com.easysql.jdbc.JdbcRunner;

import examples.BaseTest;
import examples.dhome.domain.User;
import examples.dhome.pool.DBPool;
import examples.dhome.pool.MySqlPool;

public class UserServiceTest extends BaseTest {
	
	
	
	public static void main(String[] args) {
		UserServiceTest test = new UserServiceTest();
		test.save();
	}
	
	public void save(){
		
		DBPool pool = MySqlPool.getInstance();
		Connection con = pool.getConnection();
		
		try {
			User u = new User();
			u.setUsername("hubo-test-2");
			u.setPassword("hubo-password");
			
			String sql = SQLHandler.getInsertSQL(new EntityHandler(User.class));
			
			JdbcRunner runner = new JdbcRunner();
			int i = runner.update(con, sql, new Object[]{u.getUsername(),u.getPassword(),null});
			
			System.out.println(i);
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			pool.release();
		}
	}
}
