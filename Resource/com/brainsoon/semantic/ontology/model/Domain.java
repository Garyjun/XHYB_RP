package com.brainsoon.semantic.ontology.model;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Domain extends WordWithSKOS { 
	private String label;
	
	private String code;
	
	private String nodeId;
	
	private String pid;
	
	private String version;
	
	private int nodeType = 0; //0:教材版本，1:教育阶段，2.适用年级 3.学科  4.分册,5.图书目录，6:资源节点,7.资源模块,8.资源类型 ,9.资源文件格式

	private int level; //1.章,2.节,3.文章
	
	private String xpath;
	
	private String xpathName;
	
	private List<String> broaderCodes;
	
	private List<String> broaderNames;
	
	private List<String> assets;
	
	private String pCode;
	
	public String getLabel() {
		return label;	
	}
	
	public void setLabel(String label) {
		this.label = label;
	}
	
	public String getCode() {
		return code;	
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
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
	
	public int getNodeType() {
		return nodeType;	
	}
	
	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	public String getVersion() {
		return version;	
	}
	
	public void setVersion(String version) {
		this.version = version;
	}
	
	public void addBroaderCode(String borderCode) {
		if(broaderCodes == null) {
			broaderCodes = new ArrayList<String>();
		}
		broaderCodes.add(borderCode);
	}

	public void setBroaderCodes(List<String> broaderCodes) {
		this.broaderCodes = broaderCodes;
	}
	
	public List<String> getBroaderCodes() {
		return broaderCodes;
	}
	
	public void addBroaderName(String broaderName) {
		if(broaderNames == null) {
			broaderNames = new ArrayList<String>();
		}
		broaderNames.add(broaderName);
	}

	public void setBroaderNames(List<String> broaderNames) {
		this.broaderNames = broaderNames;
	}
	
	public List<String> getBroaderNames() {
		return broaderNames;
	}
	
	public int getLevel() {
		return level;	
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public String getXpath() {
		return xpath;	
	}
	
	public void setXpath(String xpath) {
		this.xpath = xpath;
	}	
	
	public void setXpathName(String xpathName) {
		this.xpathName = xpathName;
	}
	
	public String getXpathName() {
		return xpathName;	
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
	
	public String getPCode() {
		return pCode;	
	}
	
	public void setPCode(String pCode) {
		this.pCode = pCode;
	}
	
	public String getJsonRes(int status) {
		String json = getJsonResSuccess();		
		if(status != 0)
			json = getJsonResFail(status);
		return json;
	}
	
	private String getJsonResSuccess() {
		String json = "{\"state\":0, \"desc\":\"创建本体描述成功\",\"type\":\"Domain\"";		
		json += ",\"objectId\":\"" + getObjectId() + "\"";
		json += "}";

		return json;
	}
	
	private String getJsonResFail(int status) {
		String json = "{\"state\":-1, \"desc\":\"创建本体描述失败，\",\"type\":\"Domain\"}";
		return json;
	}
}
