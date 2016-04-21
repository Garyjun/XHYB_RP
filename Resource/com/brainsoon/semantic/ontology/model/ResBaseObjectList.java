package com.brainsoon.semantic.ontology.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public abstract class ResBaseObjectList {         	
	
	protected Short state;
	
	protected Long totle;
	
	protected String type;
	
	public String getType() {
		return type;	
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public Short getState() {
		return state;	
	}
	
	public void setState(Short state) {
		this.state = state;
	}
	
	public Long getTotle() {
		return totle;	
	}
	
	public void setTotle(Long totle) {
		this.totle = totle;
	}
}
