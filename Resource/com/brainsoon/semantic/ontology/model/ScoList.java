package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

public class ScoList  extends BaseObjectList {

	private List<Sco> scos;

	public void addSco(Sco sco) {
		if(scos == null) {
			scos = new ArrayList<Sco>();
		}
		scos.add(sco);
	}

	public void setScos(List<Sco> scos) {
		this.scos = scos;
	}
	
	public List<Sco> getScos() {
		return scos;
	}
}
