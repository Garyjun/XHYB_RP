package com.brainsoon.semantic.ontology.model;                     

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import com.brainsoon.semantic.vocabulary.BrainsoonTerms;


public class ResBaseObject extends BaseObject {	
	private CommonMetaData commonMetaData;
	
	private Map<String, String> metadataMap = new HashMap<String, String>();
	
	protected List<String> xpaths;
	
	protected List<String> xpathNames;
	
	private String importXpath;
	
	private String importXpathName;
	
	private String knowledgeXpath;
	
	private String knowledgeXpathName;
	
	protected String resType;
	
	protected String batchNum;
	
	public Map<String, String> getMetadataMap() {
		return metadataMap;
	}

	public void setMetadataMap(Map<String, String> metadataMap) {
		this.metadataMap = metadataMap;
	}

	public void putMetadataMap(String key, String value) {
		metadataMap.put(key, value);	
	}
	
	@XmlTransient
	public String getMetadataMapValue(String key) {
		return metadataMap.get(key);
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

	public void addXpathName(String xpathName) {
		if(xpathNames == null) {
			xpathNames = new ArrayList<String>();
		}
		xpathNames.add(xpathName);
	}

	public void setXpathNames(List<String> xpathNames) {
		this.xpathNames = xpathNames;
	}
	
	public List<String> getXpathNames() {
		return xpathNames;
	}

	public CommonMetaData getCommonMetaData() {
		return commonMetaData;
	}

	public void setImportXpath(String xpath) {
		this.importXpath = xpath;
	}
	
	public String getImportXpath() {
		return importXpath;
	}
	
	public void setImportXpathName(String xpathName) {
		this.importXpathName = xpathName;
	}
	
	public String getImportXpathName() {
		return importXpathName;
	}
	
	public void setKnowledgeXpath(String xpath) {
		this.knowledgeXpath = xpath;
	}
	
	public String getKnowledgeXpath() {
		return knowledgeXpath;
	}
	
	public void setKnowledgeXpathName(String xpathName) {
		this.knowledgeXpathName = xpathName;
	}
	
	public String getKnowledgeXpathName() {
		return knowledgeXpathName;  
	}
	
	public void setCommonMetaData(CommonMetaData commonMetaData) {
		this.commonMetaData = commonMetaData;
	}
	
	public String getJsonRes(int status) {
		String json = getJsonResSuccess();		
		if(status != 0)
			json = getJsonResFail(status);
		return json;
	}
	
	private String getJsonResSuccess() {
		if(getObjectId() != null) {
			setObjectId(getObjectId().replaceFirst(	BrainsoonTerms.getNameSpace(), ""));
		}
		
		String json = "{\"state\":0, \"desc\":\"创建本体描述成功\",\"type\":\"ResObject\"";		
		json += ",\"objectId\":\"" + getObjectId() + "\"";
		json += "}";

		return json;
	}
	
	public String getResType() {
		return resType;
	}

	public void setResType(String resType) {
		this.resType = resType;
	}
	
	public String getBatchNum() {
		return batchNum;
	}

	public void setBatchNum(String batchNum) {
		this.batchNum = batchNum;
	}

	private String getJsonResFail(int status) {
		String json = "{\"state\":-1, \"desc\":\"创建本体描述失败，\",\"type\":\"ResObject\"}";
		return json;
	}
}
         