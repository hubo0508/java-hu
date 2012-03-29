package examples.dhome.service.impl;

import com.easysql.handlers.EntityHandler;
import com.easysql.handlers.SQLHandler;

import examples.BaseTest;
import examples.dhome.domain.User;

public class UserServiceTest extends BaseTest {
	
	
	
	public static void main(String[] args) {
		UserServiceTest test = new UserServiceTest();
		test.save();
	}
	
	public void save(){
		
		User u = new User();
		u.setUsername("hubo-test");
		u.setPassword("hubo-password");
		
		String sql = SQLHandler.getInsertSQL(new EntityHandler(User.class));
		
		System.out.println(sql);
	}
}
