package examples.dhome.domain;

import java.util.Date;

import com.easysql.core.Entity;
import com.easysql.handlers.EntityFilter;


@SuppressWarnings("serial")
public class Device  extends Entity {

	private Long id;
	private String factoryCode;// 厂家编号
	private String deviceCode;// 设备编号
	private String infoTypeId;// 信息类型ID
	private String content;// 信息内容
	private String storagePerson;// 入库人
	private Date storageDate = new Date();// 入库时间
	private Integer active = 0;// 设备状态
	private Date activeDate;// 激活操作时间

	// private Date lastDate;//最后操作时间

	private String activeStr;// 设备状态(未存储数据库)

	@Override
	public EntityFilter notTake() {
		return null;
	}
	
	
	public Device(String factoryCode, String deviceCode) {
		super();
		this.factoryCode = factoryCode;
		this.deviceCode = deviceCode;
	}

	public Device() {
		super();
	}

	public Device(Long id, String factoryCode, String deviceCode,
			String infoTypeId, String content, String storagePerson,
			Date storageDate, Integer active, Date activeDate, String activeStr) {
		super();
		this.id = id;
		this.factoryCode = factoryCode;
		this.deviceCode = deviceCode;
		this.infoTypeId = infoTypeId;
		this.content = content;
		this.storagePerson = storagePerson;
		this.storageDate = storageDate;
		this.active = active;
		this.activeDate = activeDate;
		this.activeStr = activeStr;
	}

	public Device(String factoryCode, String deviceCode, String infoTypeId,
			String content, String storagePerson, Date storageDate,
			Integer active) {
		super();
		this.factoryCode = factoryCode;
		this.deviceCode = deviceCode;
		this.infoTypeId = infoTypeId;
		this.content = content;
		this.storagePerson = storagePerson;
		this.storageDate = storageDate;
		this.active = active;
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

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public String getInfoTypeId() {
		return infoTypeId;
	}

	public void setInfoTypeId(String infoTypeId) {
		this.infoTypeId = infoTypeId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getStoragePerson() {
		return storagePerson;
	}

	public void setStoragePerson(String storagePerson) {
		this.storagePerson = storagePerson;
	}

	public Date getStorageDate() {
		return storageDate;
	}

	public void setStorageDate(Date storageDate) {
		this.storageDate = storageDate;
	}

	public Integer getActive() {
		return active;
	}

	public void setActive(Integer active) {
		this.active = active;
	}

	public Date getActiveDate() {
		return activeDate;
	}

	public void setActiveDate(Date activeDate) {
		this.activeDate = activeDate;
	}

	public String getActiveStr() {
		return activeStr;
	}

	public void setActiveStr(String activeStr) {
		this.activeStr = activeStr;
	}

}
