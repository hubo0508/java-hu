package examples.domain;

import com.easysql.core.Entity;
import com.easysql.handlers.EntityFilter;

public class Account extends Entity {

	private static final long serialVersionUID = 7877823162646493241L;

	@Override
	public EntityFilter notTake() {
		EntityFilter sqlMap = new EntityFilter();
		sqlMap.notTask("serialVersionUID");

		return sqlMap;
	}

	private long id;
	private String accountName;
	private String accountPass;

	private User_ userId;

	public User_ getUserId() {
		return userId;
	}

	public void setUserId(User_ userId) {
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
