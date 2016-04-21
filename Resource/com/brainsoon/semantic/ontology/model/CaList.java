package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

public class CaList  extends BaseObjectList {

	private List<Ca> cas;
	private String uploadFilePath;

	public void addCa(Ca ca) {
		if(cas == null) {
			cas = new ArrayList<Ca>();
		}
		cas.add(ca);
	}
	

	public String getUploadFilePath() {
		return uploadFilePath;
	}



	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}



	public void setCas(List<Ca> cas) {
		this.cas = cas;
	}
	
	public List<Ca> getCas() {
		return cas;
	}
}
