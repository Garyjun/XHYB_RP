package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

public class Organization extends BaseObjectList {

	private String objectId;
	
	private String name;
	
	private String caId;
	
	private String sumScore;
	
	private List<OrganizationItem> organizationItems;

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
	
	public String getCaId() {
		return caId;	
	}
	
	public void setCaId(String caId) {
		this.caId = caId;
	}
  	
	public String getSumScore() {
		return sumScore;
	}

	public void setSumScore(String sumScore) {
		this.sumScore = sumScore;
	}

	public void addOrganizationItem(OrganizationItem organizationItem) {
		if(organizationItems == null) {
			organizationItems = new ArrayList<OrganizationItem>();
		}
		organizationItems.add(organizationItem);
	}

	public void setOrganizationItems(List<OrganizationItem> organizationItems) {
		this.organizationItems = organizationItems;
	}
	
	public List<OrganizationItem> getOrganizationItems() {
		return organizationItems;
	} 	
	
	public String getJsonRes(int status) {
		return "";		
	}
}
