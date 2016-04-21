package com.brainsoon.semantic.schema;

import java.util.HashMap;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Property;

public class LomResource extends BaseObject {	
	private Map<String, LomProperty> propertys;
	
	private Map<String, Property> propertyVocabs;
	
	public void addLomProperty(String name, LomProperty property) {
		if(propertys == null) {
			propertys = new HashMap<String, LomProperty>();
		}
		propertys.put(name, property);
	}

	public LomProperty getLomProperty(String propertyName) {
		return propertys.get(propertyName);
	}
	
	public void setLomPropertys(Map<String, LomProperty> propertys) {
		this.propertys = propertys;
	}
	
	public Map<String, LomProperty> getLomPropertys() {
		return propertys;
	}
	
	public Property getProperty(String name) {
		return propertyVocabs.get(name);
	}
	
	public boolean existProperty(String name) {
		return propertyVocabs.containsKey(name);
	}
	
	public void addProperty(String name, Property property) {
		propertyVocabs.put(name, property);		
	}
	public Map<String, Property> getPropertys() {
		return propertyVocabs;
	}
}
