package com.brainsoon.semantic.schema;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

public class LomProperty extends BaseObject {	
	
	private String nameSpace;
	
	private String uri;
	
	private String dataType;	
	
	private int isMeta; 	
	
	private int order;
	
	private int necessary; 
	
	private String domain;	
	
	private String refer;
	
	//平台自定义
	private String key;
	//code:中文
	private Map<String, String> collections;
	
	@XmlTransient
	public String getNameSpace() {
		return nameSpace;
	}
	
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	
	public void setUri(String name) {
		uri = nameSpace + name;
	}
	
	public String getUri() {
		return uri;
	}
	public String getDataType() {
		return dataType;	
	}
	
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	public int getIsMeta() {
		return isMeta;	
	}
	
	public void setIsMeta(int isMeta) {
		this.isMeta = isMeta;
	}
	
	public int getOrder() {
		return order;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}
	
	public int getNecessary() {
		return necessary;
	}
	
	public void setNecessary(int necessary) {
		this.necessary = necessary;
	}
	
	public String getDomain() {
		return domain;	
	}
	
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getRefer() {
		return refer;
	}
	
	public void setRefer(String refer) {
		this.refer = refer;
	}
	@XmlTransient
	public Map<String, String> getCollections() {
		return collections;	
	}
	
	public void setCollections(Map<String, String> collections) {
		this.collections = collections;	
	}
	
	public void putCollection(String key, String value) {
		if(collections == null)
			collections = new HashMap<String, String>();
		collections.put(key, value);	
	}
	
	@XmlTransient
	public String getCollection(String key) {
		return collections.get(key);
	}
	
	public String getKey() {
		return getName()+","+getIsMeta()+","+getNecessary()+","+getOrder()+","+getRefer();
	}

	public void setKey(String key) {
		this.key = key;
	}
}
