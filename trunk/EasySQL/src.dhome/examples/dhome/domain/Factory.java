package examples.dhome.domain;

import java.util.Date;

import org.easysql.core.Entity;
import org.easysql.handlers.EntityFilter;


@SuppressWarnings("serial")
public class Factory  extends Entity {

	private Long id;
	private String factoryCode;// 厂家编号 
	private String factoryName;// 厂家名
	private String userName;// 授权用户
	private String password;// 授权密码
	private Date effectiveTime;// 有效时间
	private Date createTime;// 创建时间
	
	@Override
	public EntityFilter notTake() {
		return null;
	}
	

	public Factory() {
		super();
	}


	public Factory(Date createTime, Date effectiveTime, String factoryCode,
			String factoryName, Long id, String password, String userName) {
		super();
		this.createTime = createTime;
		this.effectiveTime = effectiveTime;
		this.factoryCode = factoryCode;
		this.factoryName = factoryName;
		this.id = id;
		this.password = password;
		this.userName = userName;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getFactoryCode() {
		return factoryCode;
	}

	public void setFactoryCode(String factoryCode) {
		this.factoryCode = factoryCode;
	}

	public String getFactoryName() {
		return factoryName;
	}

	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getEffectiveTime() {
		return effectiveTime;
	}

	public void setEffectiveTime(Date effectiveTime) {
		this.effectiveTime = effectiveTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

}
