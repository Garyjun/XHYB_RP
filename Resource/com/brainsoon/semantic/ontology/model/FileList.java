package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

public class FileList extends BaseObjectList {

	private List<File> files;
	
	public void addFile(File file) {
		if(files == null) {
			files = new ArrayList<File>();
		}
		files.add(file);
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}
	
	public List<File> getFiles() {
		return files;
	}
}
