package examples.dhome.domain;

import java.util.Date;

import org.easysql.core.Entity;
import org.easysql.handlers.EntityFilter;


@SuppressWarnings("serial")
public class Interface extends Entity {
	private long id;
	private String URL;
	private String describle;// 描述信息
	private String factoryCode;// 厂商编号
	private String setPerson;// 配置人
	private Date setTime;// 配置时间

	@Override
	public EntityFilter notTake() {
		return null;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getURL() {
		return URL;
	}

	public void setURL(String url) {
		URL = url;
	}

	public String getDescrible() {
		return describle;
	}

	public void setDescrible(String describle) {
		this.describle = describle;
	}

	public String getFactoryCode() {
		return factoryCode;
	}

	public void setFactoryCode(String factoryCode) {
		this.factoryCode = factoryCode;
	}

	public String getSetPerson() {
		return setPerson;
	}

	public void setSetPerson(String setPerson) {
		this.setPerson = setPerson;
	}

	public Date getSetTime() {
		return setTime;
	}

	public void setSetTime(Date setTime) {
		this.setTime = setTime;
	}

	public Interface() {
		super();
	}

}
