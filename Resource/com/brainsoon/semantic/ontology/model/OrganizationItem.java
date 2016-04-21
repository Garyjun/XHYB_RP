package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

public class OrganizationItem extends BaseObject {	
	private String nodeId;
	
	private String pid;
	
	private String xpath;
	
	private String ogId;
	
	private String caId;	
	
	private String path;
	
	private String score;
	
	private List<String> assets;
	
	private List<File> files;
	
	public String getNodeId() {
		return nodeId;	
	}
	
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
	public String getPid() {
		return pid;	
	}
	
	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getOgId() {
		return ogId;	
	}
	
	public void setOgId(String ogId) {
		this.ogId = ogId;
	}
	
	public String getCaId() {
		return caId;	
	}
	
	public void setCaId(String caId) {
		this.caId = caId;
	}
	
	public String getXpath() {
		return xpath;	
	}
	
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}
	
	public void addAsset(String asset) {
		if(assets == null) {
			assets = new ArrayList<String>();
		}
		assets.add(asset);
	}

	public void setAssets(List<String> assets) {
		this.assets = assets;
	}
	
	public List<String> getAssets() {
		return assets;
	}
	
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
	
	public String getJsonRes(int status) {
		return "";		
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}
	
	
}
