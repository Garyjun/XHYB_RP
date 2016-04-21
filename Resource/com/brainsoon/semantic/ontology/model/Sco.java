package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

public class Sco extends ResBaseObject {
	private String status;	
	
	private String caId;
	
	private String content;
	
	private List<Asset> assets;
	
	private List<File> realFiles;
	
	//相似文章
	private List<String> relateds;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getCaId() {
		return caId;
	}

	public void setCaId(String caId) {
		this.caId = caId;
	}
	
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	public void addAsset(Asset asset) {
		if(assets == null) {
			assets = new ArrayList<Asset>();
		}
		assets.add(asset);
	}

	public void setAssets(List<Asset> assets) {
		this.assets = assets;
	}
	
	public List<Asset> getAssets() {
		return assets;
	}
	
	public void addRelated(String related) {
		if(relateds == null) {
			relateds = new ArrayList<String>();
		}
		relateds.add(related);
	}
	
	public void setRelateds(List<String> relateds) {
		this.relateds = relateds;
	}
	
	public List<String> getRelateds() {
		return relateds; 
	}
	
	public List<File> getRealFiles() {
		return realFiles;
	}

	public void setRealFiles(List<File> realFiles) {
		this.realFiles = realFiles;
	}
}
