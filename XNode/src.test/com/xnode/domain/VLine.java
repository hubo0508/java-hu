package com.xnode.domain;

/**
 * <p>
 * description
 * </p>
 * 
 * @User: HUBO
 * @Date Dec 22, 2011
 * @Time 4:50:08 PM
 * 
 * <p>
 * Write a detailed description
 * </p>
 */
public class VLine {

	private String thickness;
	private String color;

	public VLine(String thickness, String color) {
		super();
		this.thickness = thickness;
		this.color = color;
	}

	public String getThickness() {
		return thickness;
	}

	public void setThickness(String thickness) {
		this.thickness = thickness;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
