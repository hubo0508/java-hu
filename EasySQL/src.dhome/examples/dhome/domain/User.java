package examples.dhome.domain;

import org.easysql.core.Entity;
import org.easysql.handlers.EntityFilter;


@SuppressWarnings("serial")
public class User extends Entity {

	private Integer id;
	private String username;
	private String password;
	private String cname;
	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public EntityFilter notTake() {

		EntityFilter ef = new EntityFilter();
		ef.notTask("userName");

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
