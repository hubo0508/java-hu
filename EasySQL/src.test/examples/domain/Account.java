package examples.domain;

import com.easysql.MapHandler;
import com.easysql.core.Entity;

public class Account extends Entity {

	private static final long serialVersionUID = 7877823162646493241L;

	@Override
	public MapHandler notTake() {
		MapHandler sqlMap = new MapHandler();
		sqlMap.notTask("serialVersionUID");

		return sqlMap;
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
