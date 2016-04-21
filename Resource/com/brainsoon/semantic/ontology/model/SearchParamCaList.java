package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

public class SearchParamCaList  extends BaseObjectList {

	private List<SearchParamCa> cas;
	private String uploadFilePath;

	public void addSearchParamCa(SearchParamCa ca) {
		if(cas == null) {
			cas = new ArrayList<SearchParamCa>();
		}
		cas.add(ca);
	}
	

	public String getUploadFilePath() {
		return uploadFilePath;
	}



	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}


	public List<SearchParamCa> getCas() {
		return cas;
	}


	public void setCas(List<SearchParamCa> cas) {
		this.cas = cas;
	}

	

}
