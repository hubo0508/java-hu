package com.xnode.domain;

/**
 * <p>
 * description
 * </p>
 * 
 * @User: HUBO
 * @Date Dec 23, 2011
 * @Time 9:37:00 AM
 * 
 * <p>
 * Write a detailed description
 * </p>
 */
public class Trendlines {

	private String startValue;
	private String color;
	private String displayValue;

	public Trendlines(String startValue, String color, String displayValue) {
		super();
		this.startValue = startValue;
		this.color = color;
		this.displayValue = displayValue;
	}

	public String getStartValue() {
		return startValue;
	}

	public void setStartValue(String startValue) {
		this.startValue = startValue;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

}
