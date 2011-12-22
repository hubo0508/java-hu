package com.xnode.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 * 
 * @User: HUBO
 * @Date Dec 22, 2011
 * @Time 3:43:30 PM
 * 
 * <p>
 * Write a detailed description
 * </p>
 */
public class Chart {

	private String caption;
	private String subcaption;
	private String xAxisName;
	private String yAxisName;
	private String numberPrefix;

	private List<Set> set = new ArrayList<Set>();
	private Line line;

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public List<Set> getSet() {
		return set;
	}

	public void setSet(List<Set> set) {
		this.set = set;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getSubcaption() {
		return subcaption;
	}

	public void setSubcaption(String subcaption) {
		this.subcaption = subcaption;
	}

	public String getXAxisName() {
		return xAxisName;
	}

	public void setXAxisName(String axisName) {
		xAxisName = axisName;
	}

	public String getYAxisName() {
		return yAxisName;
	}

	public void setYAxisName(String axisName) {
		yAxisName = axisName;
	}

	public String getNumberPrefix() {
		return numberPrefix;
	}

	public void setNumberPrefix(String numberPrefix) {
		this.numberPrefix = numberPrefix;
	}

}
