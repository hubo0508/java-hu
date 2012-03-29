package examples.dhome.domain;

import com.easysql.core.Entity;
import com.easysql.handlers.EntityFilter;

@SuppressWarnings("serial")
public class User extends Entity {

	private Integer id;
	private String username;
	private String password;
	private String cname;

	@Override
	public EntityFilter notTake() {

		EntityFilter ef = new EntityFilter();
		ef.notTask("cname");

		return ef;
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

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User() {

	}

}
