package examples.service;

import com.easysql.jdbc.JdbcTemplate;

import examples.domain.User;

public class UserService extends JdbcTemplate<User, java.lang.Long> implements
		IUserService {
	
}
