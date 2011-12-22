package com.xnode.domain;

/**
 * <p>
 * description
 * </p>
 * 
 * @User: HUBO
 * @Date Dec 22, 2011
 * @Time 3:45:19 PM
 * 
 * <p>
 * Write a detailed description
 * </p>
 */
public class Set {

	private String label;
	private String value;

	public Set(String label, String value) {
		super();
		this.label = label;
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
