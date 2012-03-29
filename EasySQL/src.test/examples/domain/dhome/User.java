package examples.domain.dhome;


import com.easysql.core.Entity;
import com.easysql.handlers.EntityFilter;

@SuppressWarnings("serial")
public class User extends Entity {

	private Integer ID;
	private String username;
	private String password;
	private String cname;

	@Override
	public EntityFilter notTake() {
		return null;
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

	public Integer getID() {
		return ID;
	}

	public void setID(Integer id) {
		ID = id;
	}

	public User() {

	}

}
