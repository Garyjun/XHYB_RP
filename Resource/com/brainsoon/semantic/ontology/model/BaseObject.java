package com.brainsoon.semantic.ontology.model;                     

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.annotate.JsonTypeInfo.Id;

@XmlRootElement
@JsonTypeInfo(use = Id.NONE)
public abstract class BaseObject implements Serializable {

	protected String objectId;
	
	protected String id;
	
	protected String name;	
	
	private String type;
	
	public String getObjectId() {
		return objectId;	
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}	
	
	public String getId() {
		return id;	
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name; 	
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getType() {
		return type;	
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public abstract String getJsonRes(int status);
}
