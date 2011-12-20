package com.poi.excel.vo;

public class StatisticsVo {

	private Long id;// id
	private Integer rankings;// 排名
	private String province;// 省
	private String hall;// 厅
	private String category;// 类别
	private String applicationName;// 应用名称
	private Integer downloadsNumbers;// 下载量

	private String modelName;// 机型
	private String factoryName;// 厂家

	private Integer downloadsNumbersTotal;
	private Integer applicationNameTotal;
	private String categoryTotal;

	private String downNumTotal;
	private String appNameTotal;
	private String cateTotal;

	public StatisticsVo() {
		super();
	}

	public StatisticsVo(Integer downloadsNumbersTotal,
			Integer applicationNameTotal, String categoryTotal) {
		super();
		this.downloadsNumbersTotal = downloadsNumbersTotal;
		this.applicationNameTotal = applicationNameTotal;
		this.categoryTotal = categoryTotal;

	}

	public StatisticsVo(String downNumTotal, String appNameTotal,
			String category) {
		super();
		this.category = category;
		this.downNumTotal = downNumTotal;
		this.appNameTotal = appNameTotal;
	}

	public Long getId() {
		return id;
	}

	public Integer getRankings() {
		return rankings;
	}

	public String getProvince() {
		return province;
	}

	public String getHall() {
		return hall;
	}

	public String getCategory() {
		return category;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public Integer getDownloadsNumbers() {
		return downloadsNumbers;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setRankings(Integer rankings) {
		this.rankings = rankings;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public void setHall(String hall) {
		this.hall = hall;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public void setDownloadsNumbers(Integer downloadsNumbers) {
		this.downloadsNumbers = downloadsNumbers;
	}

	public Integer getDownloadsNumbersTotal() {
		return downloadsNumbersTotal;
	}

	public Integer getApplicationNameTotal() {
		return applicationNameTotal;
	}

	public String getCategoryTotal() {
		return categoryTotal;
	}

	public void setDownloadsNumbersTotal(Integer downloadsNumbersTotal) {
		this.downloadsNumbersTotal = downloadsNumbersTotal;
	}

	public void setApplicationNameTotal(Integer applicationNameTotal) {
		this.applicationNameTotal = applicationNameTotal;
	}

	public void setCategoryTotal(String categoryTotal) {
		this.categoryTotal = categoryTotal;
	}

	public String getModelName() {
		return modelName;
	}

	public String getFactoryName() {
		return factoryName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	public String getDownNumTotal() {
		return downNumTotal;
	}

	public String getAppNameTotal() {
		return appNameTotal;
	}

	public String getCateTotal() {
		return cateTotal;
	}

	public void setDownNumTotal(String downNumTotal) {
		this.downNumTotal = downNumTotal;
	}

	public void setAppNameTotal(String appNameTotal) {
		this.appNameTotal = appNameTotal;
	}

	public void setCateTotal(String cateTotal) {
		this.cateTotal = cateTotal;
	}

}
