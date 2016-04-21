package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

public class OrganizationList extends BaseObjectList {
	private List<Organization> organizations;

	public void addOrganization(Organization organization) {
		if(organizations == null) {
			organizations = new ArrayList<Organization>();
		}
		organizations.add(organization);
	}

	public void setOrganizations(List<Organization> organizations) {
		this.organizations = organizations;
	}
	
	public List<Organization> getOrganizations() {
		return organizations;
	}
}
