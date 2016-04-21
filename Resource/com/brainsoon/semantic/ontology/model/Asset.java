package com.brainsoon.semantic.ontology.model;

import java.util.List;


public class Asset extends ResBaseObject {	
	
	private String artId;
	
	private String fragType;	
	
	private String content;
	
	private List<File> realFiles;
	/*@XmlJavaTypeAdapter(JsonMapAdapter.class)
	private Map<String,String> fileMetadataMap = new HashMap<String,String>();*/
	
	/*public Map<String, String> getFileMetadataMap() {
		return fileMetadataMap;
	}

	public void setFileMetadataMap(Map<String, String> fileMetadataMap) {
		this.fileMetadataMap = fileMetadataMap;
	}
	public void putFileMetadataMap(String key,String value){
		fileMetadataMap.put(key, value);
	}
	
	public String getFileMetadataMapValue(String key){
		return fileMetadataMap.get(key);
	}*/
	public String getArtId() {
		return artId;
	}

	public void setArtId(String artId) {
		this.artId = artId;
	}

	public String getFragType() {
		return fragType;
	}

	public void setFragType(String fragType) {
		this.fragType = fragType;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<File> getRealFiles() {
		return realFiles;
	}

	public void setRealFiles(List<File> realFiles) {
		this.realFiles = realFiles;
	}
	
}
