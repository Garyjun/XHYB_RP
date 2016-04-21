package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WordWithSKOSDetail extends BaseObject {

	private Map<String, String> prefLabels = new HashMap<String, String>();
	
	private Map<String, String> altLabels = new HashMap<String, String>();
	
	/**
	 * 上位
	 */
	private List<WordWithSKOS> broaders;
	
	/**
	 * 下位
	 */
	private List<WordWithSKOS> narrowers;
	
	/**
	 * 相关位
	 */
	private List<WordWithSKOS> relateds;
	
	private List<String> domains;
	
	private List<String> keywords;
	
	private String comment;	
	
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
	
	public void addBroader(WordWithSKOS broader) {
		if(broaders == null) {
			broaders = new ArrayList<WordWithSKOS>();
		}
		broaders.add(broader);
	}
	
	public void setBroaders(List<WordWithSKOS> broaders) {
		this.broaders = broaders;
	}
	
	public List<WordWithSKOS> getBroaders() {
		return broaders;
	}

	public void addNarrower(WordWithSKOS narrower) {
		if(narrowers == null) {
			narrowers = new ArrayList<WordWithSKOS>();
		}
		narrowers.add(narrower);
	}
	
	public void setNarrowers(List<WordWithSKOS> narrowers) {
		this.narrowers = narrowers;
	}
	
	public List<WordWithSKOS> getNarrowers() {
		return narrowers;
	}

	public void addRelated(WordWithSKOS related) {
		if(relateds == null) {
			relateds = new ArrayList<WordWithSKOS>();
		}
		relateds.add(related);
	}
	
	public void setRelateds(List<WordWithSKOS> relateds) {
		this.relateds = relateds;
	}
	
	public List<WordWithSKOS> getRelateds() {
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