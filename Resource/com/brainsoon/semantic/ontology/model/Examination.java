package com.brainsoon.semantic.ontology.model;

import java.util.List;


public class Examination{	
	
	private String material;
	private List<SmallQuestion> samllQuesions ;
	public String getMaterial() {
		return material;
	}
	public void setMaterial(String material) {
		this.material = material;
	}
	public List<SmallQuestion> getSamllQuesions() {
		return samllQuesions;
	}
	public void setSamllQuesions(List<SmallQuestion> samllQuesions) {
		this.samllQuesions = samllQuesions;
	}
	
}
