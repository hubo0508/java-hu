package test.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Users {

	private Integer id;
	private String username;
	private String passord;
	
	private List users = new ArrayList();
	
	public Map sqlFilter(){
		
		Map filter = new HashMap();
		filter.put("Users", "NhwmUsers");
		
		return filter;
	}

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
