package com.vo;

import java.util.ArrayList;
import java.util.List;

public class Chart {

	private String caption;
	private String xAxisName;
	private String yAxisName;
	private String showValues;
	private String decimals;
	private String formatNumberScale;
	private String palette;
	
	@SuppressWarnings("unused")
	private Author author;

	List<Categories> categories = new ArrayList<Categories>();

	List<Dataset> dataset = new ArrayList<Dataset>();

	public Chart() {
		super();
	}

	public Chart(String caption, String xAxisName, String yAxisName,
			String showValues, String decimals, String formatNumberScale,
			String palette) {
		super();
		this.caption = caption;
		this.xAxisName = xAxisName;
		this.yAxisName = yAxisName;
		this.showValues = showValues;
		this.decimals = decimals;
		this.formatNumberScale = formatNumberScale;
		this.palette = palette;
	}

	public void setDefaultValue() {
		this.setShowValues("0");
		this.setDecimals("0");
		this.setFormatNumberScale("0");
		this.setPalette("4");
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getxAxisName() {
		return xAxisName;
	}

	public void setxAxisName(String xAxisName) {
		this.xAxisName = xAxisName;
	}

	public String getyAxisName() {
		return yAxisName;
	}

	public void setyAxisName(String yAxisName) {
		this.yAxisName = yAxisName;
	}

	public String getShowValues() {
		return showValues;
	}

	public void setShowValues(String showValues) {
		this.showValues = showValues;
	}

	public String getDecimals() {
		return decimals;
	}

	public void setDecimals(String decimals) {
		this.decimals = decimals;
	}

	public String getFormatNumberScale() {
		return formatNumberScale;
	}

	public void setFormatNumberScale(String formatNumberScale) {
		this.formatNumberScale = formatNumberScale;
	}

	public String getPalette() {
		return palette;
	}

	public void setPalette(String palette) {
		this.palette = palette;
	}

	public List<Categories> getCategories() {
		return categories;
	}

	public void setCategories(List<Categories> categories) {
		this.categories = categories;
	}

	public List<Dataset> getDataset() {
		return dataset;
	}

	public void setDataset(List<Dataset> dataset) {
		this.dataset = dataset;
	}

}
