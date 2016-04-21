package com.brainsoon.resource.support;

import java.io.File;

import com.brainsoon.appframe.support.UserInfo;
import com.brainsoon.semantic.ontology.model.Ca;

public class DeleteResFileForCaIds {
	private Ca ca;
	private String id;
	
	public DeleteResFileForCaIds() {
		super();
	}
	public Ca getCa() {
		return ca;
	}
	public void setCa(Ca ca) {
		this.ca = ca;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
