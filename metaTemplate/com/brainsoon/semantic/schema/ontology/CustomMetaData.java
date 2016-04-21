package com.brainsoon.semantic.schema.ontology;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.StringUtils;


//同步资源扩展元数据、教师发展专业扩展元数据 
@XmlRootElement
public class CustomMetaData {
	private String name;
	
	private String nameCN;
	
	private String sName;
	
	
	private String groupId;
	
	
	private String classType;
	
	private List<MetadataDefinition> metadataDefinitions;
	
	public void addCustomProperty(MetadataDefinition property) {
		if(metadataDefinitions == null) {
			metadataDefinitions = new ArrayList<MetadataDefinition>();
		}
		metadataDefinitions.add(property);
	}
	
	public void setCustomPropertys(List<MetadataDefinition> propertys) {
		this.metadataDefinitions = propertys;
	}
	
	public List<MetadataDefinition> getCustomPropertys() {
		return metadataDefinitions;
	}

	public void setSName(String sName) {
		this.sName = sName;
	}
	
	public String getSName() {
		return sName;
	}
	
	public String getName() {
		return name; 	
	}
	
	public void setName(String name) {
		this.name = name;
		if(StringUtils.isNotBlank(name)) {
			String label = name.toLowerCase();
			label = label.substring(0, 3);
			setSName(label);
		}		
	}
	
	public String getNameCN() {
		return nameCN; 	
	}
	
	public void setNameCN(String nameCN) {
		this.nameCN = nameCN;
	}

	public String getClassType() {
		return classType; 	
	}
	
	public void setClassType(String classType) {
		this.classType = classType;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	
 } 