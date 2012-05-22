package test.pojo;

import java.sql.Date;

/**
 * @author LiLong
 * @date 2012-3-20
 * @version V 1.0
 */

public class Port {
	private long id;
	private String deviceCname;
	private long deviceid;
	private long portNum;
	private long portSpeed;
	private String portDes;
	private long btnmNum;
	private String customAliase;
	private long hasData;
	private Date newTime;
	private Date modifiedTime;
	private String configUser;
	private double inOctets; // ����
	private double outOctets;// ����
	private double inNUcastPkts;// �㲥����
	private double outNUcastPkts;// �㲥���
	private double inErrors;// �����
	private double outErrors;// ����
	private String time;// ʱ��

	public long getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(long deviceid) {
		this.deviceid = deviceid;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDeviceCname() {
		return deviceCname;
	}

	public void setDeviceCname(String deviceCname) {
		this.deviceCname = deviceCname;
	}

	public long getPortNum() {
		return portNum;
	}

	public void setPortNum(long portNum) {
		this.portNum = portNum;
	}

	public long getPortSpeed() {
		return portSpeed;
	}

	public void setPortSpeed(long portSpeed) {
		this.portSpeed = portSpeed;
	}

	public String getPortDes() {
		return portDes;
	}

	public void setPortDes(String portDes) {
		this.portDes = portDes;
	}

	public long getBtnmNum() {
		return btnmNum;
	}

	public void setBtnmNum(long btnmNum) {
		this.btnmNum = btnmNum;
	}

	public String getCustomAliase() {
		return customAliase;
	}

	public void setCustomAliase(String customAliase) {
		this.customAliase = customAliase;
	}

	public long getHasData() {
		return hasData;
	}

	public void setHasData(long hasData) {
		this.hasData = hasData;
	}

	public Date getNewTime() {
		return newTime;
	}

	public void setNewTime(Date newTime) {
		this.newTime = newTime;
	}

	public Date getModifiedTime() {
		return modifiedTime;
	}

	public void setModifiedTime(Date modifiedTime) {
		this.modifiedTime = modifiedTime;
	}

	public String getConfigUser() {
		return configUser;
	}

	public void setConfigUser(String configUser) {
		this.configUser = configUser;
	}

	public double getInOctets() {
		return inOctets;
	}

	public void setInOctets(double inOctets) {
		this.inOctets = inOctets;
	}

	public double getOutOctets() {
		return outOctets;
	}

	public void setOutOctets(double outOctets) {
		this.outOctets = outOctets;
	}

	public double getInNUcastPkts() {
		return inNUcastPkts;
	}

	public void setInNUcastPkts(double inNUcastPkts) {
		this.inNUcastPkts = inNUcastPkts;
	}

	public double getOutNUcastPkts() {
		return outNUcastPkts;
	}

	public void setOutNUcastPkts(double outNUcastPkts) {
		this.outNUcastPkts = outNUcastPkts;
	}

	public double getInErrors() {
		return inErrors;
	}

	public void setInErrors(double inErrors) {
		this.inErrors = inErrors;
	}

	public double getOutErrors() {
		return outErrors;
	}

	public void setOutErrors(double outErrors) {
		this.outErrors = outErrors;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

}
