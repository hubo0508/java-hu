package com.xnode.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * description
 * </p>
 * 
 * @User: HUBO
 * @Date Dec 23, 2011
 * @Time 9:42:39 AM
 * 
 * <p>
 * Write a detailed description
 * </p>
 */
public class Styles {

	private List<Definition> definition = new ArrayList<Definition>();
	private List<Application> application = new ArrayList<Application>();
	
	

	public Styles(List<Definition> definition, List<Application> application) {
		super();
		this.definition = definition;
		this.application = application;
	}

	public List<Definition> getDefinition() {
		return definition;
	}

	public void setDefinition(List<Definition> definition) {
		this.definition = definition;
	}

	public List<Application> getApplication() {
		return application;
	}

	public void setApplication(List<Application> application) {
		this.application = application;
	}

}
