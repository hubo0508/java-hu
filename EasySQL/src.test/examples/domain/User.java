package examples.domain;

import java.util.ArrayList;
import java.util.List;

import com.easysql.core.Entity;
import com.easysql.handlers.EntityFilter;

public class User extends Entity {

	private static final long serialVersionUID = 1L;

	private long id;
	private String username;
	private String password;
	private String testRow;

	private List<Account> listAccount = new ArrayList<Account>();

	@Override
	public EntityFilter notTake() {

		EntityFilter sqlMap = new EntityFilter();
		sqlMap.notTask("serialVersionUID");
		sqlMap.notTask("testRow");

		return sqlMap;
	}

	public List<Account> getListAccount() {
		return listAccount;
	}

	public void setListAccount(List<Account> listAccount) {
		this.listAccount = listAccount;
	}

	public String getTestRow() {
		return testRow;
	}

	public void setTestRow(String testRow) {
		this.testRow = testRow;
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
