package com.brainsoon.semantic.schema;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;


//同步资源扩展元数据、教师发展专业扩展元数据 
@XmlRootElement
public class CustomMetaData extends BaseObject {
	private List<LomProperty> customPropertys;

	
	public void addCustomProperty(LomProperty property) {
		if(customPropertys == null) {
			customPropertys = new ArrayList<LomProperty>();
		}
		customPropertys.add(property);
	}
	
	public void setCustomPropertys(List<LomProperty> propertys) {
		this.customPropertys = propertys;
	}
	
	public List<LomProperty> getCustomPropertys() {
		return customPropertys;
	}

}
