package com.brainsoon.semantic.ontology.model;


public class Statistics {
	private String objectId;
	
	private long count;
	
	private float sum;
	
	private String type;
	private String typeName;
	
	private String version;
	
	private String source;
	
	private String status;	
	
	public String getObjectId() {
		return objectId;	
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}		
	
	public long getCount() {
		return count;	
	}
	
	public void setCount(long count) {
		this.count = count;
	}	
	
	public float getSum() {
		return sum;	
	}
	
	public void setSum(float sum) {
		this.sum = sum;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getVersion() {
		return version; 	
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getSource() {
		return source;	
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public String getStatus() {
		return status;	
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
}
