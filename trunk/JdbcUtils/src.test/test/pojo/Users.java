package test.pojo;

import java.util.ArrayList;
import java.util.List;

public class Users {

	private Integer id;
	private String username;
	private String passord;
	
	private List users = new ArrayList();

	public List getUsers() {
		return users;
	}

	public void setUsers(List users) {
		this.users = users;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassord() {
		return passord;
	}

	public void setPassord(String passord) {
		this.passord = passord;
	}

}
