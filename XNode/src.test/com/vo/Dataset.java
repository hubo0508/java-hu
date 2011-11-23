package com.vo;

import java.util.ArrayList;
import java.util.List;

public class Dataset {

	private String seriesName;
	private String color;

	List<Set> set = new ArrayList<Set>();

	public Dataset() {
		super();
	}

	public Dataset(String seriesName, String color) {
		super();
		this.seriesName = seriesName;
		this.color = color;
	}

	public String getSeriesName() {
		return seriesName;
	}

	public void setSeriesName(String seriesName) {
		this.seriesName = seriesName;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public List<Set> getSet() {
		return set;
	}

	public void setSet(List<Set> set) {
		this.set = set;
	}



}
