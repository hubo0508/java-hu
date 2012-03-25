package examples.domain;

import com.easysql.core.Entity;
import com.easysql.core.object.SqlMap;

public class User extends Entity {

	private static final long serialVersionUID = 1L;

	private long id;
	private String username;
	private String password;

	@Override
	public SqlMap notTake() {

		log.info("serialVersionUID");

		SqlMap map = new SqlMap();
		map.notTask("serialVersionUID");

		return map;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
