package com.xnode.domain;

/**
 * <p>
 * description
 * </p>
 * 
 * @User: HUBO
 * @Date Dec 23, 2011
 * @Time 9:43:29 AM
 * 
 * <p>
 * Write a detailed description
 * </p>
 */
public class Application {

	private String toObject;
	private String styles;

	public Application(String toObject, String styles) {
		super();
		this.toObject = toObject;
		this.styles = styles;
	}

	public String getToObject() {
		return toObject;
	}

	public void setToObject(String toObject) {
		this.toObject = toObject;
	}

	public String getStyles() {
		return styles;
	}

	public void setStyles(String styles) {
		this.styles = styles;
	}

}
