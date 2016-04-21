package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WordWithSKOS extends BaseObject {

	protected Map<String, String> prefLabels = new HashMap<String, String>();
	
	protected Map<String, String> altLabels = new HashMap<String, String>();
	
	protected List<String> broaders; 
	
	protected List<String> narrowers;
	
	protected Map<String, String> relateds;	
	
	protected List<String> domains;
	
	protected List<String> xpaths;
	
	protected String comment;
	
	protected List<String> keywords;
	
	public void addPrefLabel(String lang, String prefLabel) {
		prefLabels.put(lang, prefLabel);
	}
	
	public void setPrefLabels(Map<String, String> prefLabels) {
		this.prefLabels = prefLabels;
	}
	
	public String getPrefLabel(String lang) {
		return prefLabels.get(lang);
	}
	
	public Map<String, String> getPrefLabels() {
		return prefLabels;
	}
	
	public void addAltLabel(String lang, String altLabel) {
		altLabels.put(lang, altLabel);
	}
	
	public void setAltLabels(Map<String, String> altLabels) {
		this.altLabels = altLabels;
	}	
	
	public String getAltLabel(String lang) {
		return altLabels.get(lang);
	}
	
	public Map<String, String> getAltLabels() {
		return altLabels;
	}
	
	public void addBroader(String broader) {
		if(broaders == null) {
			broaders = new ArrayList<String>();
		}
		broaders.add(broader);
	}
	
	public List<String> getBroaders() {
		return broaders;
	}
	
	public void setBroaders(List<String> broaders) {
		this.broaders = broaders;
	}
	
	public void addNarrower(String narrower) {
		if(narrowers == null) {
			narrowers = new ArrayList<String>();
		}
		narrowers.add(narrower);
	}
	
	public void setNarrowers(List<String> narrowers) {
		this.narrowers = narrowers;
	}
	
	public List<String> getNarrowers() {
		return narrowers; 
	}
	
	public void addRelated(String related, String kind) {
		if(relateds == null) {
			relateds = new HashMap<String, String>();
		}
		relateds.put(related, kind);
	}
	
	public void setRelateds(Map<String, String> relateds) {
		this.relateds = relateds;
	}
	
	public Map<String, String> getRelateds() {
		return relateds;
	}
	
	public void addDomain(String domain) {
		if(domains == null) {
			domains = new ArrayList<String>();
		}
		domains.add(domain);
	}
	
	public void setDomains(List<String> domains) {
		this.domains = domains;
	}
	
	public List<String> getDomains() {
		return domains; 
	}
	
	public void addXpath(String xpath) {
		if(xpaths == null) {
			xpaths = new ArrayList<String>();
		}
		xpaths.add(xpath);
	}

	public void setXpaths(List<String> xpaths) {
		this.xpaths = xpaths;
	}
	
	public List<String> getXpaths() {
		return xpaths;
	}
	
	public String getComment() {
		return comment;	
	}
	
	public void setComment(String comment) {
		this.comment = comment;	
	}
	
	public void addKeyword(String keyword) {
		if(keywords == null) {
			keywords = new ArrayList<String>();
		}
		keywords.add(keyword);
	}
	
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	
	public List<String> getKeywords() {
		return keywords;
	}
	
	public String getJsonRes(int status) {
		String json = getJsonResSuccess();		
		if(status != 0)
			json = getJsonResFail(status);
		return json;
	}
	
	private String getJsonResSuccess() {
		String json = "{\"state\":0, \"desc\":\"创建本体描述成功\",\"type\":\"Word\"";		
		json += ",\"objectId\":\"" + getObjectId() + "\"";
		json += "}";

		return json;
	}
	
	private String getJsonResFail(int status) {
		String json = "{\"state\":-1, \"desc\":\"创建本体描述失败，\",\"type\":\"Word\"}";
		return json;
	}
}