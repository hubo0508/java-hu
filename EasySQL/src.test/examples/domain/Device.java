package examples.domain;

import java.util.Date;

import com.easysql.MapHandler;
import com.easysql.core.Entity;

/**
 * @Description: 设备信息
 * @author HUBO
 */
public class Device extends Entity {

	private static final long serialVersionUID = 408814904393580895L;

	private Long id;
	private String factoryCode;// 厂家编号
	private String deviceCode;// 设备编号
	private String infoTypeId;// 信息类型ID
	private String content;// 信息内容
	private String storagePerson;// 入库人
	private Date storageDate = new Date();// 入库时间
	private Integer active = 1;// 设备状态
	private Date activeDate;// 激活操作时间

	private String activeStr;// 设备状态(未存储数据库)

	@Override
	public MapHandler notTake() {

		MapHandler ifmap = new MapHandler();

		// 不作表字段
		ifmap.notTask("serialVersionUID");
		ifmap.notTask("activeStr");

		// 当前字段与数据库字段不一样，需替换
		ifmap.put(MapHandler.REPLACE,
				new String[] { "factoryCode:factoryCodeReplace" });

		// 對每張表單獨設值虛列
		ifmap.put(MapHandler.GENERATOR, "deviceSeq");

		return ifmap;
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
