package examples.domain;

import com.easysql.core.Entity;
import com.easysql.core.object.SqlMap;

public class Account extends Entity {

	private static final long serialVersionUID = 7877823162646493241L;

	@Override
	public SqlMap notTake() {
		map.notTask("serialVersionUID");		
		return map;
	}

	private long id;
	private String accountName;
	private String accountPass;

	private User userId;

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountPass() {
		return accountPass;
	}

	public void setAccountPass(String accountPass) {
		this.accountPass = accountPass;
	}

}
