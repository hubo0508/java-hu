package examples.dhome.domain;

import org.easysql.core.Entity;
import org.easysql.handlers.EntityFilter;

@SuppressWarnings("serial")
public class Device extends Entity {

	private long id;
	private String deviceIp;
	private String deviceEname;
	private String deviceCname;
	private String deviceType;
	private String deviceFactory;
	private long hasData;

	@Override
	public EntityFilter filter() {

		EntityFilter ef = new EntityFilter();
		ef.put(EntityFilter.REPLACE, new String[]{"Device:nhwm_config_device"});

		return ef;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getDeviceEname() {
		return deviceEname;
	}

	public void setDeviceEname(String deviceEname) {
		this.deviceEname = deviceEname;
	}

	public String getDeviceCname() {
		return deviceCname;
	}

	public void setDeviceCname(String deviceCname) {
		this.deviceCname = deviceCname;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceFactory() {
		return deviceFactory;
	}

	public void setDeviceFactory(String deviceFactory) {
		this.deviceFactory = deviceFactory;
	}

	public long getHasData() {
		return hasData;
	}

	public void setHasData(long hasData) {
		this.hasData = hasData;
	}

}
