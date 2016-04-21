package com.brainsoon.semantic.schema;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class BaseObject {
	protected String objectId;
	
	protected String name;
	
	protected String nameCN;
	
	public String getObjectId() {
		return objectId;	
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	public String getName() {
		return name; 	
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getNameCN() {
		return nameCN; 	
	}
	
	public void setNameCN(String nameCN) {
		this.nameCN = nameCN;
	}
}
