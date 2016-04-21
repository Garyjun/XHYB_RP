package com.brainsoon.semantic.ontology.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.brainsoon.semantic.vocabulary.CommonDCTerms;

@XmlRootElement
public class FilePath  {
	private String path;
	private String dir;
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	
	
	
}
