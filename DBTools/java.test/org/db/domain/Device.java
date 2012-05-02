package org.db.domain;

public class Device {

	private Integer id;
	private String deviceIp;
	private String deviceEname;
	private String deviceType;
	private String deviceFactory;
	private Integer hasData;
	private String deviceCname;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
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

	public Integer getHasData() {
		return hasData;
	}

	public void setHasData(Integer hasData) {
		this.hasData = hasData;
	}

	public String getDeviceCname() {
		return deviceCname;
	}

	public void setDeviceCname(String deviceCname) {
		this.deviceCname = deviceCname;
	}

}